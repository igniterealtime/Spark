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
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.XmlElement;
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
import org.jivesoftware.sparkimpl.profile.ext.JabberAvatarExtension;
import org.jivesoftware.sparkimpl.profile.ext.VCardUpdateExtension;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * VCardManager handles all VCard loading/caching within Spark.
 *
 * @author Derek DeMoro
 */
public class VCardManager {

    private VCard personalVCard;
    private transient byte[] personalVCardAvatar; // lazy loaded cache of avatar binary data.
    private transient String personalVCardHash; // lazy loaded cache of avatar hash.

    private final Map<BareJid, VCard> vcards = Collections.synchronizedMap( new HashMap<>());

    private final Set<BareJid> delayedContacts = Collections.synchronizedSet( new HashSet<>());
    
    private boolean vcardLoaded;

    private final File imageFile;

    private final VCardEditor editor;

    private final File vcardStorageDirectory;

    private final LinkedBlockingQueue<BareJid> queue = new LinkedBlockingQueue<>();
    
    private final File contactsDir;

    private final List<VCardListener> listeners = new ArrayList<>();

	private final List<BareJid> writingQueue = Collections.synchronizedList( new ArrayList<>());

    /**
     * Initialize VCardManager.
     */
    public VCardManager() {

        // Register providers
        ProviderManager.addExtensionProvider( JabberAvatarExtension.ELEMENT_NAME, JabberAvatarExtension.NAMESPACE, new JabberAvatarExtension.Provider() );
        ProviderManager.addExtensionProvider( VCardUpdateExtension.ELEMENT_NAME, VCardUpdateExtension.NAMESPACE, new VCardUpdateExtension.Provider() );

        imageFile = new File(SparkManager.getUserDirectory(), "personal.png");

        // Initialize vCard.
        personalVCard = new VCard();
        personalVCardAvatar = null;
        personalVCardHash = null;

        // Set VCard Storage
        vcardStorageDirectory = new File(SparkManager.getUserDirectory(), "vcards");
        vcardStorageDirectory.mkdirs();

        // Set the current user directory.
        contactsDir = new File(SparkManager.getUserDirectory(), "contacts");
        contactsDir.mkdirs();

        initializeUI();

        // Intercept all presence packets being sent and append vcard information.
        StanzaFilter presenceFilter = new StanzaTypeFilter(Presence.class);
        SparkManager.getConnection().addAsyncStanzaListener( stanza -> {
            Presence newPresence = (Presence)stanza;
            VCardUpdateExtension update = new VCardUpdateExtension();
            JabberAvatarExtension jax = new JabberAvatarExtension();

            XmlElement updateExt = newPresence.getExtensionElement(update.getElementName(), update.getNamespace());
            XmlElement jabberExt = newPresence.getExtensionElement(jax.getElementName(), jax.getNamespace());

            if (updateExt != null) {
                newPresence.removeExtension(updateExt.getElementName(), updateExt.getNamespace());
            }

            if (jabberExt != null) {
                newPresence.removeExtension(jabberExt.getElementName(), jabberExt.getNamespace());
            }

            if (personalVCard != null) {

                if ( personalVCardAvatar == null )
                {
                    personalVCardAvatar = personalVCard.getAvatar();
                }
                if (personalVCardAvatar != null && personalVCardAvatar.length > 0) {
                    if ( personalVCardHash == null )
                    {
                        personalVCardHash = personalVCard.getAvatarHash();
                    }
                    update.setPhotoHash(personalVCardHash);
                    jax.setPhotoHash(personalVCardHash);

                    newPresence.addExtension(update);
                    newPresence.addExtension(jax);
                }
            }
        }, presenceFilter);

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
                   BareJid jid = queue.take();
                   reloadVCard(jid);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
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
                BareJid jid = VCardpacket.getFrom().asBareJid();
                if (VCardpacket.getType().equals(IQ.Type.result) && delayedContacts.contains(jid))
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
     *
     * @param jid the jid to lookup.
     */
    public void addToQueue(BareJid jid) {
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

        JMenuItem editProfileMenu = new JMenuItem(SparkRes.getImageIcon(SparkRes.SMALL_BUSINESS_MAN_VIEW));
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
                    personalVCardAvatar = null;
                    personalVCardHash = null;
                }
            };
            vcardLoaderWorker.start();
        } );

        JMenuItem viewProfileMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.FIND_TEXT_IMAGE));
        ResourceUtils.resButton(viewProfileMenu, Res.getString("menuitem.lookup.profile"));
        contactsMenu.insert(viewProfileMenu, size > 0 ? size - 3 : 0);
        viewProfileMenu.addActionListener( e -> {
            String jidToView = JOptionPane.showInputDialog(SparkManager.getMainWindow(), Res.getString("message.enter.jabber.id") + ":", Res.getString("title.lookup.profile"), JOptionPane.QUESTION_MESSAGE);
            if (ModelUtil.hasLength(jidToView) && jidToView.contains( "@" ) && ModelUtil.hasLength( XmppStringUtils.parseDomain(jidToView))) {
                BareJid bareJid;
                try {
                    bareJid = JidCreate.bareFrom(jidToView);
                } catch (XmppStringprepException e1) {
                    throw new IllegalStateException(e1);
                }
                viewProfile(bareJid, SparkManager.getWorkspace());
            }
            else if (ModelUtil.hasLength(jidToView)) {
                UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.invalid.jabber.id"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
            }
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
            VCard vcard = new VCard();

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
                }
                else {
                    editor.displayProfile(jid, vcard, parent);
                }
            }
        };

        vcardThread.start();

    }

    /**
     * Displays the full profile for a particular JID.
     *
     * @param jid    the jid of the user to display.
     * @param parent the parent component to use for displaying dialog.
     */
    public void viewFullProfile(final BareJid jid, final JComponent parent) {
        final SwingWorker vcardThread = new SwingWorker() {
            VCard vcard = new VCard();

            @Override
			public Object construct() {
                vcard = getVCard(jid);
                return vcard;
            }

            @Override
			public void finished() {
                if (vcard.getError() != null || vcard == null) {
                    // Show vcard not found
                	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    JOptionPane.showMessageDialog(parent, Res.getString("message.unable.to.load.profile", jid), Res.getString("title.profile.not.found"), JOptionPane.ERROR_MESSAGE);
                }
                else {
                    editor.viewFullProfile(vcard, parent);
                }
            }
        };

        vcardThread.start();

    }

    /**
     * Returns the VCard for this Spark user. This information will be cached after loading.
     *
     * @return this users VCard.
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
     * @return this users VCard.
     */    
	public void reloadPersonalVCard() {
		try {
            personalVCard = org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(SparkManager.getConnection()).loadVCard();
            personalVCardAvatar = personalVCard.getAvatar();
            personalVCardHash = null; // reload lazy later, when need

            // If VCard is loaded, then save the avatar to the personal folder.
			if (personalVCardAvatar != null && personalVCardAvatar.length > 0) {
				ImageIcon icon = new ImageIcon(personalVCardAvatar);
				icon = VCardManager.scale(icon);
				if (icon.getIconWidth() != -1) {
					BufferedImage image = GraphicUtils.convert(icon.getImage());
					ImageIO.write(image, "PNG", imageFile);
				}
			}
		}
		catch (Exception e) {
            StanzaError.Builder errorBuilder = StanzaError.getBuilder(StanzaError.Condition.conflict);
			personalVCard.setError(errorBuilder);
            personalVCardAvatar = null;
            personalVCardHash = null;
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
            return GraphicUtils.scaleImageIcon(icon, 40, 40);
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
        return getVCard(jid, true);
    }

	/**
	 * Loads the vCard from memory. If no vCard is found in memory, will add it
	 * to a loading queue for future loading. Users of this method should only
	 * use it if the correct vCard is not important the first time around. You
	 * will get a dummy vCard if there is currently no vCard in memory.
	 * 
	 * @param jid
	 *            the users jid.
	 * @return the users VCard or an empty VCard.
	 */
    public VCard getVCardFromMemory(BareJid jid) {
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
            vcard = new VCard();
            vcard.setJabberId(jid.toString());
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
	 * @param jid
	 *            the users jid.
	 * @param useCachedVCards
	 *            true to check in cache and hdd, otherwise false will do a new
	 *            network vcard operation.
	 * @return the VCard.
	 */
    public VCard getVCard(BareJid jid, boolean useCachedVCards) {
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
	 * @param jidString the JID of the user.
	 * 
	 * @return the new network vCard or a vCard with an error 
	 */
    public VCard reloadVCard(BareJid jidString) {
        EntityBareJid jid;
		try {
			jid = JidCreate.entityBareFrom(jidString);
		} catch (XmppStringprepException e) {
			throw new IllegalStateException(e);
		}
        VCard vcard = new VCard();
        try {
            vcard = org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(SparkManager.getConnection()).loadVCard( jid );
            vcard.setJabberId(jid.toString());
            if (vcard.getNickName() != null && vcard.getNickName().length() > 0)
            {
            	// update nickname.
            	ContactItem item = SparkManager.getWorkspace().getContactList().getContactItemByJID(jid.toString());
            	item.setNickname(vcard.getNickName());
            	// TODO: this doesn't work if someone removes his nickname. If we remove it in that case, it will cause problems with people using another way to manage their nicknames.
            }
            addVCard(jid, vcard);
            persistVCard(jid, vcard);
        }
        catch (XMPPException | SmackException | InterruptedException e) {
        	////System.out.println(jid+" Fehler in reloadVCard ----> null");
            StanzaError.Builder errorBuilder = StanzaError.getBuilder(StanzaError.Condition.resource_constraint);
        	vcard.setError(errorBuilder);
        	vcard.setJabberId(jid.toString());
            delayedContacts.add(jid);
        	return vcard;
        	//We dont want cards with error
           // vcard.setError(new StanzaError(XMPPError.Condition.request_timeout));
           //addVCard(jid, vcard);
        }

        // Persist XML
        

        return vcard;
    }

    
    /**
     * Adds a new vCard to the cache.
     *
     * @param jid   the jid of the user.
     * @param vcard the users vcard to cache.
     */
    public void addVCard(BareJid jid, VCard vcard) {
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
        if (icon.getIconHeight() > 64 || icon.getIconWidth() > 64) {
            avatarImage = avatarImage.getScaledInstance(-1, 64, Image.SCALE_SMOOTH);
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
        // Handle own avatar file.
        if (jid != null && SparkManager.getSessionManager().getJID().asBareJid().equals(jid)) {
            if (imageFile.exists()) {
                try {
                    return imageFile.toURI().toURL();
                }
                catch (MalformedURLException e) {
                    Log.error(e);
                }
            }
            else {
                return SparkRes.getURL(SparkRes.DUMMY_CONTACT_IMAGE);
            }
        }

        // Handle other users JID
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
        if (number == null) {
            return null;
        }

        number = number.replace("-", "");
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace(" ", "");

        return number;
    }

    /**
     * Sets the personal vcard of the user.
     *
     * @param vcard the users vCard.
     */
    public void setPersonalVCard(VCard vcard) {
        this.personalVCard = vcard;
        this.personalVCardHash = null;
        this.personalVCardAvatar = null;
    }

    public URL getAvatarURL(BareJid jid) {
        VCard vcard = getVCard(jid);
        if (vcard != null) {
            String hash = vcard.getAvatarHash();
            if (!ModelUtil.hasLength(hash)) {
                return null;
            }

            final File avatarFile = new File(contactsDir, hash);
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
	 * 
	 * @param jid
	 *            the users jid
	 * @return the vcard if there is already one, otherwise null and we try to
	 *         load vcard in background
	 * 
	 */
	public URL getAvatarURLIfAvailable(BareJid jid) {
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
        // remove tab
        fileName   = fileName.replaceAll("\t", "");
        // remove new line (Unix)
        fileName          = fileName.replaceAll("\n", "");
        // remove new line (Windows)
        fileName          = fileName.replaceAll("\r", "");

        byte[] bytes = vcard.getAvatar();
        if (bytes != null && bytes.length > 0) {
            vcard.setAvatar(bytes);
            try {
                String hash = vcard.getAvatarHash();
                final File avatarFile = new File(contactsDir, hash);
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
    private VCard loadFromFileSystem(BareJid jid) {
    	if (jid == null) {
    		return null;
    	}
    	
        // Unescape JID
        String fileName = Base64.getEncoder().encodeToString(jid.toString().getBytes());

        // remove tab
        fileName   = fileName.replaceAll("\t", "");
        // remove new line (Unix)
        fileName          = fileName.replaceAll("\n", "");
        // remove new line (Windows)
        fileName          = fileName.replaceAll("\r", "");
        
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
            vcard = provider.parse( parser );
        }
        catch (Exception e) {
            Log.warning("Unable to load vCard for " + jid, e);
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


    /**
     * Add <code>VCardListener</code>. Listens to the personalVCard.
     *
     * @param listener the listener to add.
     */
    public void addVCardListener(VCardListener listener) {
        listeners.add(listener); 
    }

    /**
     * Remove <code>VCardListener</code>.
     *
     * @param listener the listener to remove.
     */
    public void removeVCardListener(VCardListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all <code>VCardListener</code> implementations.
     */
    protected void notifyVCardListeners() {
        for (VCardListener listener : listeners) {
            listener.vcardChanged(personalVCard);
        }
    }

}
