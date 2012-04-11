/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *
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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.Base64;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.profile.ext.JabberAvatarExtension;
import org.jivesoftware.sparkimpl.profile.ext.VCardUpdateExtension;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * VCardManager handles all VCard loading/caching within Spark.
 *
 * @author Derek DeMoro
 */
public class VCardManager {

    private VCard personalVCard;

    private Map<String, VCard> vcards = Collections.synchronizedMap(new HashMap<String, VCard>());

    private Set<String> delayedContacts = Collections.synchronizedSet(new HashSet<String>());
    
    private boolean vcardLoaded;

    private File imageFile;

    private final VCardEditor editor;

    private File vcardStorageDirectory;

    final MXParser parser;

    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    
    private File contactsDir;

    private List<VCardListener> listeners = new ArrayList<VCardListener>();

	private List<String> writingQueue = Collections.synchronizedList(new ArrayList<String>());

    /**
     * Initialize VCardManager.
     */
    public VCardManager() {
        // Initialize parser
        parser = new MXParser();

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        }
        catch (XmlPullParserException e) {
            Log.error(e);
        }

        imageFile = new File(SparkManager.getUserDirectory(), "personal.png");

        // Initialize vCard.
        personalVCard = new VCard();

        // Set VCard Storage
        vcardStorageDirectory = new File(SparkManager.getUserDirectory(), "vcards");
        vcardStorageDirectory.mkdirs();

        // Set the current user directory.
        contactsDir = new File(SparkManager.getUserDirectory(), "contacts");
        contactsDir.mkdirs();

        initializeUI();

        // Intercept all presence packets being sent and append vcard information.
        PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);
        SparkManager.getConnection().addPacketInterceptor(new PacketInterceptor() {
            public void interceptPacket(Packet packet) {
                Presence newPresence = (Presence)packet;
                VCardUpdateExtension update = new VCardUpdateExtension();
                JabberAvatarExtension jax = new JabberAvatarExtension();

                PacketExtension updateExt = newPresence.getExtension(update.getElementName(), update.getNamespace());
                PacketExtension jabberExt = newPresence.getExtension(jax.getElementName(), jax.getNamespace());

                if (updateExt != null) {
                    newPresence.removeExtension(updateExt);
                }

                if (jabberExt != null) {
                    newPresence.removeExtension(jabberExt);
                }

                if (personalVCard != null) {
                    byte[] bytes = personalVCard.getAvatar();
                    if (bytes != null && bytes.length > 0) {
                        update.setPhotoHash(personalVCard.getAvatarHash());
                        jax.setPhotoHash(personalVCard.getAvatarHash());

                        newPresence.addExtension(update);
                        newPresence.addExtension(jax);
                    }
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
    	final Runnable queueListener = new Runnable() {
            public void run() {
                while (true) {
                    try {
                       String jid = queue.take();
                       reloadVCard(jid);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };

        TaskEngine.getInstance().submit(queueListener);
        
        PacketFilter filter = new PacketTypeFilter(VCard.class);
        PacketListener myListener = new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				if (packet instanceof VCard)
				{
					VCard VCardpacket = (VCard)packet;
					String jid = VCardpacket.getFrom();
					if (VCardpacket.getType().equals(IQ.Type.RESULT) && jid != null && delayedContacts.contains(jid))
					{
						delayedContacts.remove(jid);
						addVCard(jid, VCardpacket);
						persistVCard(jid, VCardpacket);
					}
					
				}
			}
		};
        	
		SparkManager.getConnection().addPacketListener(myListener, filter);

    }

    /**
     * Adds a jid to lookup vCard.
     *
     * @param jid the jid to lookup.
     */
    public void addToQueue(String jid) {
        if (!queue.contains(jid)) {
            queue.add(jid);
        }

    }

    /**
     * Adds VCard capabilities to menus and other components in Spark.
     */
    private void initializeUI() {
        boolean enabled = Enterprise.containsFeature(Enterprise.VCARD_FEATURE);
        if (!enabled) {
            return;
        }

        // Add Actions Menu
        final JMenu contactsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.contacts"));
        final JMenu communicatorMenu = SparkManager.getMainWindow().getJMenuBar().getMenu(0);

        JMenuItem editProfileMenu = new JMenuItem(SparkRes.getImageIcon(SparkRes.SMALL_BUSINESS_MAN_VIEW));
        ResourceUtils.resButton(editProfileMenu, Res.getString("menuitem.edit.my.profile"));

        int size = contactsMenu.getMenuComponentCount();

        communicatorMenu.insert(editProfileMenu, 1);
        editProfileMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingWorker vcardLoaderWorker = new SwingWorker() {
                    public Object construct() {
                        try {
                            personalVCard.load(SparkManager.getConnection());
                        }
                        catch (XMPPException e) {
                            Log.error("Error loading vcard information.", e);
                        }
                        return true;
                    }

                    public void finished() {
                        editor.editProfile(personalVCard, SparkManager.getWorkspace());
                    }
                };
                vcardLoaderWorker.start();
            }
        });

        JMenuItem viewProfileMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.FIND_TEXT_IMAGE));
        ResourceUtils.resButton(viewProfileMenu, Res.getString("menuitem.lookup.profile"));
        contactsMenu.insert(viewProfileMenu, size > 0 ? size - 1 : 0);
        viewProfileMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String jidToView = JOptionPane.showInputDialog(SparkManager.getMainWindow(), Res.getString("message.enter.jabber.id") + ":", Res.getString("title.lookup.profile"), JOptionPane.QUESTION_MESSAGE);
                if (ModelUtil.hasLength(jidToView) && jidToView.indexOf("@") != -1 && ModelUtil.hasLength(StringUtils.parseServer(jidToView))) {
                    viewProfile(jidToView, SparkManager.getWorkspace());
                }
                else if (ModelUtil.hasLength(jidToView)) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.invalid.jabber.id"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    /**
     * Displays <code>VCardViewer</code> for a particular JID.
     *
     * @param jid    the jid of the user to display.
     * @param parent the parent component to use for displaying dialog.
     */
    public void viewProfile(final String jid, final JComponent parent) {
        final SwingWorker vcardThread = new SwingWorker() {
            VCard vcard = new VCard();

            public Object construct() {
                vcard = getVCard(jid);
                return vcard;
            }

            public void finished() {
                if (vcard == null) {
                    // Show vcard not found
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
    public void viewFullProfile(final String jid, final JComponent parent) {
        final SwingWorker vcardThread = new SwingWorker() {
            VCard vcard = new VCard();

            public Object construct() {
                vcard = getVCard(jid);
                return vcard;
            }

            public void finished() {
                if (vcard.getError() != null || vcard == null) {
                    // Show vcard not found
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
			personalVCard.load(SparkManager.getConnection());
			// If VCard is loaded, then save the avatar to the personal folder.
			byte[] bytes = personalVCard.getAvatar();
			if (bytes != null && bytes.length > 0) {
				ImageIcon icon = new ImageIcon(bytes);
				icon = VCardManager.scale(icon);
				if (icon != null && icon.getIconWidth() != -1) {
					BufferedImage image = GraphicUtils.convert(icon.getImage());
					ImageIO.write(image, "PNG", imageFile);
				}
			}
		}
		catch (Exception e) {
			personalVCard.setError(new XMPPError(XMPPError.Condition.conflict));
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
    public VCard getVCard(String jid) {
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
    public VCard getVCardFromMemory(String jid) {
        // Check in memory first.
        if (vcards.containsKey(jid)) {
            return vcards.get(jid);
        }

        // if not in memory
        VCard vcard = loadFromFileSystem(jid);
        if (vcard == null) {
            addToQueue(jid);

            // Create temp vcard.
            vcard = new VCard();
            vcard.setJabberId(jid);
        } else {
        	//System.out.println(jid+"  HDD ---------->");
        }
        

        return vcard;
    }

	/**
	 * Returns the VCard. You should always use useChachedVCards. VCardManager
	 * will keep the VCards up to date. If you wan't to force a network reload
	 * of the VCard you can set useChachedVCards to false. That means that you
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
	 * @param useCache
	 *            true to check in cache and hdd, otherwise false will do a new
	 *            network vcard operation.
	 * @return the VCard.
	 */
    public VCard getVCard(String jid, boolean useCachedVCards) {
        jid = StringUtils.parseBareAddress(jid);
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
	 * @param jid
	 *            the jid of the user.
	 * 
	 * @return the new network vCard or a vCard with an error 
	 */
    public VCard reloadVCard(String jid) {
        jid = StringUtils.parseBareAddress(jid);
        VCard vcard = new VCard();
        try {
        	vcard.setJabberId(jid);
            vcard.load(SparkManager.getConnection(), jid);
            if (vcard.getNickName() != null && vcard.getNickName().length() > 0)
            {
            	// update nickname.
            	ContactItem item = SparkManager.getWorkspace().getContactList().getContactItemByJID(jid);
            	item.setNickname(vcard.getNickName());
            	// TODO: this doesn't work if someone removes his nickname. If we remove it in that case, it will cause problems with people using another way to manage their nicknames.
            }
            addVCard(jid, vcard);
            persistVCard(jid, vcard);
        }
        catch (XMPPException e) {
        	////System.out.println(jid+" Fehler in reloadVCard ----> null");
        	vcard.setError(new XMPPError(XMPPError.Condition.request_timeout));
        	vcard.setJabberId(jid);
        	delayedContacts.add(jid);
        	return vcard;
        	//We dont want cards with error
           // vcard.setError(new XMPPError(XMPPError.Condition.request_timeout));
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
    public void addVCard(String jid, VCard vcard) {
        if (vcard == null)
        	return; 
        vcard.setJabberId(jid);
        if (vcards.containsKey(jid) && vcards.get(jid).getError() == null && vcard.getError()!= null)
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
    public URL getAvatar(String jid) {
        // Handle own avatar file.
        if (jid != null && StringUtils.parseBareAddress(SparkManager.getSessionManager().getJID()).equals(StringUtils.parseBareAddress(jid))) {
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
    }

    public URL getAvatarURL(String jid) {
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
	public URL getAvatarURLIfAvailable(String jid) {
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
    private void persistVCard(String jid, VCard vcard) {
        if (jid == null || jid.trim().isEmpty() || vcard == null) {
        	return;
        }
        
        
        String fileName = Base64.encodeBytes(jid.getBytes());
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

        final String xml = vcard.toString();

        File vcardFile = new File(vcardStorageDirectory, fileName);

        // write xml to file
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vcardFile), "UTF-8"));
            out.write(xml);
            out.close();
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
    private VCard loadFromFileSystem(String jid) {
    	if (jid == null || jid.trim().isEmpty()) {
    		return null;
    	}
    	
        // Unescape JID
        String fileName = Base64.encodeBytes(jid.getBytes());

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

        try {
            // Otherwise load from file system.
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(vcardFile), "UTF-8"));
            VCardProvider provider = new VCardProvider();
            parser.setInput(in);
            VCard vcard = (VCard)provider.parseIQ(parser);

            // Check to see if the file is older 10 minutes. If so, reload.
            String timestamp = vcard.getField("timestamp");
            if (timestamp != null) {
                long time = Long.parseLong(timestamp);
                long now = System.currentTimeMillis();


                long hour = (1000 * 60) * 60;
                if (now - time > hour) {
                    addToQueue(jid);
                }
            }

            addVCard(jid, vcard);
            return vcard;
        }
        catch (Exception e) {
            Log.warning("Unable to load vCard for " + jid, e);
        }

        return null;
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
