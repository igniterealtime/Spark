/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.sparkimpl.profile;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.xml.SmackXmlParser;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.profile.ext.VCardUpdateExtension;
import org.jivesoftware.sparkimpl.settings.Sizes;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replaceChars;
import static org.jivesoftware.smack.packet.StanzaError.Condition.item_not_found;
import static org.jivesoftware.smack.packet.StanzaError.Condition.resource_constraint;

/**
 * VCardManager handles all VCard loading/caching within Spark.
 *
 * @author Derek DeMoro
 */
public class VCardManager {
    private VCard personalVCard;
    private final Map<EntityBareJid, VCard> vcards = Collections.synchronizedMap( new HashMap<>());
    private final Set<BareJid> delayedContacts = Collections.synchronizedSet( new HashSet<>());
    private boolean vcardLoaded;
    private final VCardEditor editor;
    private final File vcardStorageDirectory = SparkManager.getVCardsDir();
    private final LinkedBlockingQueue<EntityBareJid> queue = new LinkedBlockingQueue<>();
    private final CopyOnWriteArrayList<VCardListener> listeners = new CopyOnWriteArrayList<>();
	private final List<BareJid> writingQueue = Collections.synchronizedList( new ArrayList<>());

    public VCardManager() {
        // Register providers
        ProviderManager.addExtensionProvider( VCardUpdateExtension.ELEMENT_NAME, VCardUpdateExtension.NAMESPACE, new VCardUpdateExtension.Provider() );
        // Initialize vCard.
        personalVCard = new VCard();
        initializeUI();
        editor = new VCardEditor();
        // Start Listener
        startQueueListener();
    }

    /**
     * Listens for new VCards to lookup in a queue.
     */
    private void startQueueListener() {
    	final Runnable queueListener = () -> {
            while (true) {
                try {
                   EntityBareJid jid = queue.take();
                   reloadVCard(jid);
                }
                catch (InterruptedException e) {
                    Log.warning(e.getMessage());
                    break;
                }
            }
        };

        TaskEngine.getInstance().submit(queueListener);

        StanzaFilter filter = new AndFilter(stanza -> {
            Jid from = stanza.getFrom();
            return from != null;
        }, new StanzaTypeFilter(VCard.class));

        StanzaListener myListener = stanza -> {
            if (stanza instanceof VCard)
            {
                VCard VCardpacket = (VCard)stanza;
                EntityBareJid jid = VCardpacket.getFrom().asEntityBareJidIfPossible();
                if (VCardpacket.getType() == IQ.Type.result && delayedContacts.contains(jid))
                {
                    delayedContacts.remove(jid);
                    addVCard(jid, VCardpacket);
                    persistVCard(jid, VCardpacket);
                }
            }
        };
        	
		SparkManager.getConnection().addAsyncStanzaListener(myListener, filter);
    }

    /**
     * Adds a jid to lookup vCard.
     */
    public void addToQueue(EntityBareJid jid) {
        if (!queue.contains(jid)) {
            queue.add(jid);
        }
    }

    /**
     * Adds VCard capabilities to menus and other components in Spark.
     */
    private void initializeUI() {
        // See if we should disable the "Edit my profile" option under "File"
        if (Default.getBoolean(Default.DISABLE_EDIT_PROFILE) || !Enterprise.containsFeature(Enterprise.VCARD_FEATURE)) return;
        // Add Actions Menu
        final JMenu contactsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.contacts"));
        final JMenu communicatorMenu = SparkManager.getMainWindow().getJMenuBar().getMenu(0);

        JMenuItem editProfileMenu = new JMenuItem(SparkRes.getImageIcon(SparkRes.Icon.SMALL_BUSINESS_MAN_VIEW));
        ResourceUtils.resButton(editProfileMenu, Res.getString("menuitem.edit.my.profile"));

        int size = contactsMenu.getMenuComponentCount();

        communicatorMenu.insert(editProfileMenu, 1);

        editProfileMenu.addActionListener( e -> {
            SwingWorker vcardLoaderWorker = new SwingWorker() {
                @Override
				public Object construct() {
                    try {
                        final org.jivesoftware.smackx.vcardtemp.VCardManager smackVCardManager = org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(SparkManager.getConnection());
                        personalVCard = smackVCardManager.loadVCard();
                    }
                    catch (XMPPException | SmackException | InterruptedException e) {
                        Log.error("Error loading vcard information.", e);
                    }
                    return true;
                }

                @Override
				public void finished() {
                    editor.editProfile(personalVCard, SparkManager.getWorkspace());
                }
            };
            vcardLoaderWorker.start();
        } );

        JMenuItem viewProfileMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.Icon.FIND_TEXT_IMAGE));
        ResourceUtils.resButton(viewProfileMenu, Res.getString("menuitem.lookup.profile"));
        contactsMenu.insert(viewProfileMenu, size > 0 ? size - 3 : 0);
        viewProfileMenu.addActionListener( e -> {
            String jidToView = JOptionPane.showInputDialog(SparkManager.getMainWindow(), Res.getString("message.enter.jabber.id") + ":", Res.getString("title.lookup.profile"), JOptionPane.QUESTION_MESSAGE);
            if (isBlank(jidToView)) {
                return;
            }
            BareJid bareJid;
            try {
                bareJid = JidCreate.entityBareFrom(jidToView);
            } catch (XmppStringprepException ex){
                UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.invalid.jabber.id"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            viewProfile(bareJid, SparkManager.getWorkspace());
        } );
    }

    /**
     * Displays <code>VCardViewer</code> for a particular JID.
     *
     * @param jid    the jid of the user to display.
     * @param parent the parent component to use for displaying dialog.
     */
    public void viewProfile(final BareJid jid, final JComponent parent) {
        final SwingWorker vcardThread = new SwingWorker() {
            VCard vcard;

            @Override
			public Object construct() {
                vcard = getVCard(jid);
                return vcard;
            }

            @Override
			public void finished() {
                if (vcard == null) {
                    // Show vcard not found
                	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    JOptionPane.showMessageDialog(parent, Res.getString("message.unable.to.load.profile", jid), Res.getString("title.profile.not.found"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                editor.displayProfile(jid, vcard, parent);
            }
        };

        vcardThread.start();
    }

    /**
     * Returns the VCard for this Spark user. This information will be cached after loading.
     */
    public VCard getVCard() {
        if (!vcardLoaded) {
        	reloadPersonalVCard();
            vcardLoaded = true;
        }
        return personalVCard;
    }
    
    /**
     * Loads the vcard for this Spark user     
     */
	public void reloadPersonalVCard() {
		try {
            personalVCard = org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(SparkManager.getConnection()).loadVCard();
		}
		catch (Exception e) {
            StanzaError.Builder errorBuilder = StanzaError.getBuilder(StanzaError.Condition.conflict);
			personalVCard.setError(errorBuilder.build());
			Log.error(e);
		}
	}

    /**
     * Returns the Avatar in the form of an <code>ImageIcon</code>.
     *
     * @param vcard the vCard containing the avatar.
     * @return the ImageIcon or null if no avatar was present.
     */
    public static ImageIcon getAvatarIcon(VCard vcard) {
        // Set avatar
        byte[] bytes = vcard.getAvatar();
        if (bytes != null && bytes.length > 0) {
            ImageIcon icon = new ImageIcon(bytes);
            return GraphicUtils.scaleImageIcon(icon, Sizes.Avatar.SMALL, Sizes.Avatar.SMALL);
        }
        return null;
    }

	/**
	 * Returns the VCard. Will first look in VCard cache. You will receive a
	 * dummy vcard, if there is no vCard for specified jid in cache. Same as
	 * getVCard(jid, true)
	 * 
	 * @param jid
	 *            the users jid.
	 * @return the VCard.
	 */
    public VCard getVCard(BareJid jid) {
        EntityBareJid entityBareJid = jid.asEntityBareJidIfPossible();
        if (entityBareJid == null) {
            return emptyVcard(jid);
        }
        return getVCard(entityBareJid, true);
    }

    private static VCard emptyVcard(BareJid jid) {
        VCard vCard = new VCard();
        vCard.setJabberId(jid);
        return vCard;
    }

    /**
	 * Loads the vCard from memory. If no vCard is found in memory, will add it
	 * to a loading queue for future loading. Users of this method should only
	 * use it if the correct vCard is not important the first time around. You
	 * will get a dummy vCard if there is currently no vCard in memory.
	 * 
	 * @return the users VCard or an empty VCard.
	 */
    private VCard getVCardFromMemory(EntityBareJid jid) {
        // Check in memory first.
        VCard currentVcard = vcards.get(jid);
        if (currentVcard != null) {
            return currentVcard;
        }
        // if not in memory
        VCard vcard = loadFromFileSystem(jid);
        if (vcard == null) {
            addToQueue(jid);
            // Create temp vcard.
            return emptyVcard(jid);
        }
        return vcard;
    }

	/**
	 * Returns the VCard. You should always use useCachedVCards. VCardManager
	 * will keep the VCards up to date. If you want to force a network reload
	 * of the VCard you can set useCachedVCards to false. That means that you
	 * have to wait for the vcard response. The method will block until the
	 * result is available or a timeout occurs (like reloadVCard(String jid)).
	 * If there is no response from server this method a dummy vcard with an
	 * error. Use getVCard(String jid) to get a dummy VCard if there is
	 * currently no VCard. If you get a vCard with an error you may wait some
	 * seconds. Sometimes vCards could not be loaded within smack timeout but we
	 * are still listening for vCards that are too late. Be patient for some
	 * seconds and try again, maybe we will get it.
	 * 
	 * @param useCachedVCards
	 *            true to check in cache and hdd, otherwise false will do a new
	 *            network vcard operation.
	 * @return the VCard.
	 */
    private VCard getVCard(EntityBareJid jid, boolean useCachedVCards) {
        if (useCachedVCards)
        {
        	return getVCardFromMemory(jid);
        } else {
        	return reloadVCard(jid);
        }
    }

	/**
	 * Forces a reload of a <code>VCard</code>. To load a VCard you should use
	 * getVCard(String jid) instead. This method will perform a network lookup
	 * which could take some time. If you're having problems with request
	 * timeout you should also use getVCard(String jid). Use addToQueue(String
	 * jid) if you want VCardManager to update the VCard by the given jid. The
	 * method will block until the result is available or a timeout occurs.
	 * 
	 * @return the new network vCard or a vCard with an error
	 */
    public VCard reloadVCard(EntityBareJid jid) {
        try {
            VCard vcard = org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(SparkManager.getConnection()).loadVCard(jid);
            vcard.setJabberId(jid.toString());
            if (vcard.getNickName() != null && !vcard.getNickName().isEmpty())
            {
            	// update nickname.
            	ContactItem item = SparkManager.getWorkspace().getContactList().getContactItemByJID(jid);
            	item.setNickname(vcard.getNickName());
            	// TODO: this doesn't work if someone removes his nickname. If we remove it in that case, it will cause problems with people using another way to manage their nicknames.
            }
            addVCard(jid, vcard);
            persistVCard(jid, vcard);
            return vcard;
        }
        catch (XMPPException | SmackException | InterruptedException e) {
            StanzaError.Condition condition = resource_constraint;
            if (e instanceof XMPPErrorException) {
                condition = ((XMPPErrorException) e).getStanzaError().getCondition();
                if (condition != item_not_found) {
                    Log.warning("Unable to reload vCard for " + jid, e);
                }
            }

            StanzaError stanzaError = StanzaError.getBuilder(condition).build();
            VCard vcard = new VCard();
            vcard.setError(stanzaError);
        	vcard.setJabberId(jid.toString());
            delayedContacts.add(jid);
        	return vcard;
        }
    }
    
    /**
     * Adds a new vCard to the cache.
     *
     * @param jid   the jid of the user.
     * @param vcard the users vcard to cache.
     */
    public void addVCard(EntityBareJid jid, VCard vcard) {
        if (vcard == null)
        	return; 
        vcard.setJabberId(jid.toString());
        VCard currentVcard = vcards.get(jid);
        if (currentVcard != null && currentVcard.getError() == null && vcard.getError()!= null)
        {
        	return;
        }
        vcards.put(jid, vcard);
    }

    /**
     * Scales an image to the preferred avatar size.
     *
     * @param icon the icon to scale.
     * @return the scaled version of the image.
     */
    public static ImageIcon scale(ImageIcon icon) {
        Image avatarImage = icon.getImage();
        if (icon.getIconHeight() > Sizes.Avatar.VCARD_VIEW || icon.getIconWidth() > Sizes.Avatar.VCARD_VIEW) {
            avatarImage = avatarImage.getScaledInstance(-1, Sizes.Avatar.VCARD_VIEW, Image.SCALE_SMOOTH);
        }
        return new ImageIcon(avatarImage);
    }

    /**
     * Returns the URL of the avatar image associated with the users JID.
     *
     * @param jid the jid of the user.
     * @return the URL of the image. If not image is found, a default avatar is returned.
     */
    public URL getAvatar(BareJid jid) {
        ContactItem item = SparkManager.getWorkspace().getContactList().getContactItemByJID(jid);
        URL avatarURL = null;
        if (item != null) {
            try {
                avatarURL = item.getAvatarURL();
            }
            catch (MalformedURLException e) {
                Log.error(e);
            }
        }

        if (avatarURL == null) {
            return SparkRes.getURL(SparkRes.DUMMY_CONTACT_IMAGE);
        }

        return avatarURL;
    }

    /**
     * Searches all vCards for a specified phone number.
     *
     * @param phoneNumber the phoneNumber.
     * @return the vCard which contains the phone number.
     */
    public VCard searchPhoneNumber(String phoneNumber) {
        for (VCard vcard : vcards.values()) {
            String homePhone = getNumbersFromPhone(vcard.getPhoneHome("VOICE"));
            String workPhone = getNumbersFromPhone(vcard.getPhoneWork("VOICE"));
            String cellPhone = getNumbersFromPhone(vcard.getPhoneWork("CELL"));

            String query = getNumbersFromPhone(phoneNumber);
            if ((homePhone != null && homePhone.endsWith(query)) ||
                (workPhone != null && workPhone.endsWith(query)) ||
                (cellPhone != null && cellPhone.endsWith(query))) {
                return vcard;
            }
        }

        return null;
    }

    /**
     * Parses out the numbers only from a phone number.
     *
     * @param number the full phone number.
     * @return the phone number only (5551212)
     */
    public static String getNumbersFromPhone(String number) {
        return replaceChars(number, "()- ", "");
    }

    /**
     * Sets the personal vcard of the user.
     */
    public void setPersonalVCard(VCard vcard) {
        this.personalVCard = vcard;
    }

    public URL getAvatarURL(BareJid jid) {
        VCard vcard = getVCard(jid);
        if (vcard != null) {
            String hash = vcard.getAvatarHash();
            if (isBlank(hash)) {
                return null;
            }
            final File avatarFile = new File(SparkManager.getContactsDir(), hash);
            try {
                return avatarFile.toURI().toURL();
            }
            catch (MalformedURLException e) {
                Log.error(e);
            }
        }
        return null;
    }
    
	/**
	 * Get URL for avatar from vcard. If there is no vcard available we will try
	 * to get it from the server and return null.
	 */
	public URL getAvatarURLIfAvailable(BareJid bareJid) {
        EntityBareJid jid = bareJid.asEntityBareJidIfPossible();
        if (jid == null) {
            return null;
        }
		if (getVCard(jid) != null) {
			return getAvatarURL(jid);
		} else {
			addToQueue(jid);
			return null;
		}
	}

    /**
     * Persist vCard information out for caching.
     *
     * @param jid   the users jid.
     * @param vcard the users vcard.
     */
    private void persistVCard(BareJid jid, VCard vcard) {
        if (jid == null || vcard == null) {
        	return;
        }
        String fileName = Base64.getEncoder().encodeToString(jid.toString().getBytes());
        byte[] bytes = vcard.getAvatar();
        if (bytes != null && bytes.length > 0) {
            vcard.setAvatar(bytes);
            try {
                String hash = vcard.getAvatarHash();
                final File avatarFile = new File(SparkManager.getContactsDir(), hash);
                ImageIcon icon = new ImageIcon(bytes);
                icon = VCardManager.scale(icon);
                if (icon != null && icon.getIconWidth() != -1) {
                    BufferedImage image = GraphicUtils.convert(icon.getImage());
                    if (image == null) {
                        Log.warning("Unable to write out avatar for " + jid);
                    }
                    else {
						if (writingQueue.contains(jid)) {
							writeAvatarSync(image, avatarFile);
						} else {
							writingQueue.add(jid);
							ImageIO.write(image, "PNG", avatarFile);
							writingQueue.remove(jid);
						}
                    }
                }
            }
            catch (Exception e) {
                Log.error("Unable to update avatar in Contact Item.", e);
            }
        }

        // Set timestamp
        vcard.setField("timestamp", Long.toString(System.currentTimeMillis()));
        final String xml = vcard.toXML().toString();
        File vcardFile = new File(vcardStorageDirectory, fileName);
        // write xml to file
        try {
            Files.write(vcardFile.toPath(), xml.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            Log.error(e);
        }
    }

    private synchronized void writeAvatarSync(BufferedImage image, File avatarFile) throws IOException {
    	ImageIO.write(image, "PNG", avatarFile);
	}

	/**
     * Attempts to load
     *
     * @param jid the jid of the user.
     * @return the VCard if found, otherwise null.
     */
    private VCard loadFromFileSystem(EntityBareJid jid) {
    	if (jid == null) {
    		return null;
    	}
        // Unescape JID
        String fileName = Base64.getEncoder().encodeToString(jid.toString().getBytes());
        final File vcardFile = new File(vcardStorageDirectory, fileName);
        if (!vcardFile.exists()) {
            return null;
        }

        final VCard vcard;
        try ( final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(vcardFile), StandardCharsets.UTF_8)) )
        {
            // Otherwise load from file system.
            XmlPullParser parser = SmackXmlParser.newXmlParser(in);
            // Skip forward until we're at <vCard xmlns='vcard-temp'>
            while ( !( parser.getEventType() == XmlPullParser.Event.START_ELEMENT && VCard.ELEMENT.equals( parser.getName() ) && VCard.NAMESPACE.equals( parser.getNamespace() ) ) )
            {
                parser.next();
            }
            VCardProvider provider = new VCardProvider();
            vcard = provider.parse( parser, null );
        }
        catch (Exception e) {
            Log.warning("Unable to load vCard for " + jid, e);
            //noinspection ResultOfMethodCallIgnored
            vcardFile.delete();
            return null;
        }

        addVCard(jid, vcard);

        // Check to see if the file is older 60 minutes. If so, reload.
        final String timestamp = vcard.getField( "timestamp" );
        if ( timestamp != null )
        {
            final Duration duration = Duration.between( Instant.ofEpochMilli( Long.parseLong( timestamp ) ), Instant.now() );
            if ( duration.toMinutes() >= 60 )
            {
                addToQueue( jid );
            }
        }

        return vcard;
    }

    public void addVCardListener(VCardListener listener) {
        listeners.addIfAbsent(listener);
    }

    public void removeVCardListener(VCardListener listener) {
        listeners.remove(listener);
    }

    protected void notifyVCardListeners() {
        for (VCardListener listener : listeners) {
            listener.vcardChanged(personalVCard);
        }
    }

}
