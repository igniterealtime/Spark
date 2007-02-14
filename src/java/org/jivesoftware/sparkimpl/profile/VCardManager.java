/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.profile;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.profile.ext.JabberAvatarExtension;
import org.jivesoftware.sparkimpl.profile.ext.VCardUpdateExtension;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * VCardManager handles all VCard loading/caching within Spark.
 *
 * @author Derek DeMoro
 */
public class VCardManager {

    private VCard personalVCard = new VCard();

    private Map<String, VCard> vcards = new HashMap<String, VCard>();

    private boolean vcardLoaded;

    final private File imageFile = new File(SparkManager.getUserDirectory(), "personal.png");

    private final VCardEditor editor;


    /**
     * Initialize VCardManager.
     */
    public VCardManager() {
        initializeUI();

        // Intercept all presence packets being sent and append vcard information.
        PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);
        SparkManager.getConnection().addPacketWriterInterceptor(new PacketInterceptor() {
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

                byte[] bytes = getVCard().getAvatar();
                if (bytes != null) {
                    String hash = org.jivesoftware.spark.util.StringUtils.hash(bytes);
                    update.setPhotoHash(hash);
                    jax.setPhotoHash(hash);

                    newPresence.addExtension(update);
                    newPresence.addExtension(jax);
                }

            }
        }, presenceFilter);

        editor = new VCardEditor();
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
        contactsMenu.insert(viewProfileMenu, size - 1);
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
                if (vcard.getError() != null || vcard == null) {
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
     * Returns the VCard for this Spark user. This information will be cached after loading.
     *
     * @return this users VCard.
     */
    public VCard getVCard() {
        if (!vcardLoaded) {
            try {
                personalVCard.load(SparkManager.getConnection());

                // If VCard is loaded, then save the avatar to the personal folder.
                byte[] bytes = personalVCard.getAvatar();
                if (bytes != null) {
                    ImageIcon icon = new ImageIcon(bytes);
                    icon = VCardManager.scale(icon);
                    if (icon != null && icon.getIconWidth() != -1) {
                        BufferedImage image = GraphicUtils.convert(icon.getImage());
                        ImageIO.write(image, "PNG", imageFile);
                    }
                }
            }
            catch (Exception e) {
                Log.error(e);
            }
            vcardLoaded = true;
        }
        return personalVCard;
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
        if (bytes != null) {
            ImageIcon icon = new ImageIcon(bytes);
            return GraphicUtils.scaleImageIcon(icon, 40, 40);
        }
        return null;
    }

    /**
     * Returns the VCard. Will first look in VCard cache and only do a network
     * operation if no vcard is found.
     *
     * @param jid the users jid.
     * @return the VCard.
     */
    public VCard getVCard(String jid) {
        return getVCard(jid, true);
    }


    /**
     * Returns the VCard.
     *
     * @param jid      the users jid.
     * @param useCache true to check in cache, otherwise false will do a new network vcard operation.
     * @return the VCard.
     */
    public VCard getVCard(String jid, boolean useCache) {
        if (!vcards.containsKey(jid) || !useCache) {
            VCard vcard = new VCard();
            try {
                vcard.load(SparkManager.getConnection(), jid);
                vcard.setJabberId(jid);
                vcards.put(jid, vcard);
            }
            catch (XMPPException e) {
                Log.warning("Unable to load vcard for " + jid, e);
                vcard.setError(new XMPPError(409));
                return vcard;
            }
        }
        return vcards.get(jid);
    }

    /**
     * Adds a new vCard to the cache.
     *
     * @param jid   the jid of the user.
     * @param vcard the users vcard to cache.
     */
    public void addVCard(String jid, VCard vcard) {
        vcard.setJabberId(jid);
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
                    return imageFile.toURL();
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

            String query = getNumbersFromPhone(phoneNumber);
            if ((homePhone != null && homePhone.contains(query)) || (workPhone != null && workPhone.contains(query))) {
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
        if (number.startsWith("1")) {
            number = number.substring(1);
        }

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

}
