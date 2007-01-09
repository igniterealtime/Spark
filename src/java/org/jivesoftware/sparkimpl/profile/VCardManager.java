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
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.profile.ext.JabberAvatarExtension;
import org.jivesoftware.sparkimpl.profile.ext.VCardUpdateExtension;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VCardManager {
    private BusinessPanel businessPanel;
    private PersonalPanel personalPanel;
    private HomePanel homePanel;
    private AvatarPanel avatarPanel;
    private JLabel avatarLabel;
    private VCard vcard = new VCard();

    private Map<String, VCard> vcardMap = new HashMap<String, VCard>();
    private boolean vcardLoaded;

    final private File imageFile = new File(SparkManager.getUserDirectory(), "personal.png");


    public VCardManager() {
        initialize();

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
    }

    public void showProfile(JComponent parent) {
        final JTabbedPane tabbedPane = new JTabbedPane();

        personalPanel = new PersonalPanel();
        personalPanel.showJID(false);

        tabbedPane.addTab(Res.getString("tab.personal"), personalPanel);

        businessPanel = new BusinessPanel();
        tabbedPane.addTab(Res.getString("tab.business"), businessPanel);

        homePanel = new HomePanel();
        tabbedPane.addTab(Res.getString("tab.home"), homePanel);

        avatarPanel = new AvatarPanel();
        tabbedPane.addTab(Res.getString("tab.avatar"), avatarPanel);

        loadVCard(SparkManager.getSessionManager().getJID());

        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        ImageIcon icon = getAvatarIcon();
        if (icon == null) {
            icon = SparkRes.getImageIcon(SparkRes.BLANK_24x24);
        }

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.edit.profile"), Res.getString("message.save.profile"), icon, true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("save"), Res.getString("cancel")};
        pane = new JOptionPane(tabbedPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, Res.getString("title.profile.information"));
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(600, 400);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if (Res.getString("cancel").equals(value)) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
                else if (Res.getString("save").equals(value)) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                    saveVCard();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        personalPanel.focus();
    }

    public void viewProfile(final String jid, final JComponent parent) {
        SwingWorker worker = new SwingWorker() {
            VCard userVCard = new VCard();

            public Object construct() {
                userVCard = getVCard(jid);
                return vcard;
            }

            public void finished() {
                if (userVCard.getError() != null || userVCard == null) {
                    // Show vcard not found
                    JOptionPane.showMessageDialog(parent, Res.getString("message.unable.to.load.profile", jid), Res.getString("title.profile.not.found"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else {
                    showUserProfile(jid, userVCard, parent);
                }
            }
        };

        worker.start();

    }

    private void showUserProfile(String jid, VCard vcard, JComponent parent) {
        final JTabbedPane tabbedPane = new JTabbedPane();

        personalPanel = new PersonalPanel();
        personalPanel.showJID(true);

        tabbedPane.addTab(Res.getString("tab.personal"), personalPanel);

        businessPanel = new BusinessPanel();
        tabbedPane.addTab(Res.getString("tab.business"), businessPanel);

        homePanel = new HomePanel();
        tabbedPane.addTab(Res.getString("tab.home"), homePanel);

        avatarPanel = new AvatarPanel();
        avatarPanel.setEditable(false);

        personalPanel.allowEditing(false);
        businessPanel.allowEditing(false);
        homePanel.allowEditing(false);

        final JOptionPane pane;
        final JFrame dlg;

        avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(JButton.RIGHT);
        avatarLabel.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(avatarLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("close")};
        pane = new JOptionPane(tabbedPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));

        dlg = new JFrame(Res.getString("title.view.profile.for", jid));
        dlg.setIconImage(SparkRes.getImageIcon(SparkRes.PROFILE_IMAGE_16x16).getImage());

        dlg.pack();
        dlg.setSize(500, 400);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (pane.getValue() instanceof Integer) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                    return;
                }
                String value = (String)pane.getValue();
                if (Res.getString("close").equals(value)) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    dlg.dispose();
                }
            }
        });

        createVCardUI(vcard);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
    }

    public void initialize() {
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
                            vcard.load(SparkManager.getConnection());
                        }
                        catch (XMPPException e) {
                            Log.error("Error loading vcard information.", e);
                        }
                        return true;
                    }

                    public void finished() {
                        showProfile(SparkManager.getWorkspace());
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

    public void shutdown() {

    }

    public boolean canShutDown() {
        return true;
    }

    private void loadVCard(final String jid) {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return getVCard(jid);
            }

            public void finished() {
                VCard vcard = (VCard)get();
                if (vcard.getError() == null) {
                    createVCardUI(vcard);
                }
            }
        };

        worker.start();
    }


    private void saveVCard() {
        // Save personal info
        vcard.setFirstName(personalPanel.getFirstName());
        vcard.setLastName(personalPanel.getLastName());
        vcard.setMiddleName(personalPanel.getMiddleName());
        vcard.setEmailHome(personalPanel.getEmailAddress());
        vcard.setNickName(personalPanel.getNickname());

        // Save business info
        vcard.setOrganization(businessPanel.getCompany());
        vcard.setAddressFieldWork("STREET", businessPanel.getStreetAddress());
        vcard.setAddressFieldWork("LOCALITY", businessPanel.getCity());
        vcard.setAddressFieldWork("REGION", businessPanel.getState());
        vcard.setAddressFieldWork("PCODE", businessPanel.getZipCode());
        vcard.setAddressFieldWork("CTRY", businessPanel.getCountry());
        vcard.setField("TITLE", businessPanel.getJobTitle());
        vcard.setOrganizationUnit(businessPanel.getDepartment());
        vcard.setPhoneWork("VOICE", businessPanel.getPhone());
        vcard.setPhoneWork("FAX", businessPanel.getFax());
        vcard.setPhoneWork("PAGER", businessPanel.getPager());
        vcard.setPhoneWork("CELL", businessPanel.getMobile());
        vcard.setField("URL", businessPanel.getWebPage());

        // Save Home Info
        vcard.setAddressFieldHome("STREET", homePanel.getStreetAddress());
        vcard.setAddressFieldHome("LOCALITY", homePanel.getCity());
        vcard.setAddressFieldHome("REGION", homePanel.getState());
        vcard.setAddressFieldHome("PCODE", homePanel.getZipCode());
        vcard.setAddressFieldHome("CTRY", homePanel.getCountry());
        vcard.setPhoneHome("VOICE", homePanel.getPhone());
        vcard.setPhoneHome("FAX", homePanel.getFax());
        vcard.setPhoneHome("PAGER", homePanel.getPager());
        vcard.setPhoneHome("CELL", homePanel.getMobile());


        final SwingWorker worker = new SwingWorker() {
            boolean saved = false;

            public Object construct() {

                // Save Avatar
                final File avatarFile = avatarPanel.getAvatarFile();
                if (avatarFile != null) {
                    try {
                        // Make it 48x48
                        ImageIcon icon = new ImageIcon(avatarFile.toURL());
                        Image image = icon.getImage();
                        image = image.getScaledInstance(-1, 48, Image.SCALE_SMOOTH);
                        byte[] imageBytes = GraphicUtils.getBytesFromImage(image);
                        vcard.setAvatar(imageBytes);
                    }
                    catch (MalformedURLException e) {
                        Log.error("Unable to set avatar.", e);
                    }
                }
                else if (avatarPanel.getAvatarBytes() != null) {
                    vcard.setAvatar(avatarPanel.getAvatarBytes());
                }
                try {
                    vcard.save(SparkManager.getConnection());
                    saved = true;

                    byte[] avatarBytes = vcard.getAvatar();

                    // Notify users.
                    if (avatarFile != null || avatarBytes != null) {
                        Presence presence = SparkManager.getWorkspace().getStatusBar().getPresence();
                        Presence newPresence = new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());

                        // Change my own presence
                        SparkManager.getSessionManager().changePresence(newPresence);
                    }
                    else {
                        String firstName = vcard.getFirstName();
                        String lastName = vcard.getLastName();
                        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
                        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
                            statusBar.setNickname(firstName + " " + lastName);
                        }
                        else if (ModelUtil.hasLength(firstName)) {
                            statusBar.setNickname(firstName);
                        }
                    }
                }
                catch (XMPPException e) {
                    Log.error(e);
                }

                return new Boolean(saved);
            }

            public void finished() {
                if (!saved) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.vcard.not.supported"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else {

                }
            }
        };

        worker.start();


    }

    private void createVCardUI(VCard vcard) {
        personalPanel.setFirstName(vcard.getFirstName());
        personalPanel.setMiddleName(vcard.getMiddleName());
        personalPanel.setLastName(vcard.getLastName());
        personalPanel.setEmailAddress(vcard.getEmailHome());
        personalPanel.setNickname(vcard.getNickName());
        personalPanel.setJID(vcard.getJabberId());

        businessPanel.setCompany(vcard.getOrganization());
        businessPanel.setDepartment(vcard.getOrganizationUnit());
        businessPanel.setStreetAddress(vcard.getAddressFieldWork("STREET"));
        businessPanel.setCity(vcard.getAddressFieldWork("LOCALITY"));
        businessPanel.setState(vcard.getAddressFieldWork("REGION"));
        businessPanel.setZipCode(vcard.getAddressFieldWork("PCODE"));
        businessPanel.setCountry(vcard.getAddressFieldWork("CTRY"));
        businessPanel.setJobTitle(vcard.getField("TITLE"));
        businessPanel.setPhone(vcard.getPhoneWork("VOICE"));
        businessPanel.setFax(vcard.getPhoneWork("FAX"));
        businessPanel.setPager(vcard.getPhoneWork("PAGER"));
        businessPanel.setMobile(vcard.getPhoneWork("CELL"));
        businessPanel.setWebPage(vcard.getField("URL"));

        // Load Home Info
        homePanel.setStreetAddress(vcard.getAddressFieldHome("STREET"));
        homePanel.setCity(vcard.getAddressFieldHome("LOCALITY"));
        homePanel.setState(vcard.getAddressFieldHome("REGION"));
        homePanel.setZipCode(vcard.getAddressFieldHome("PCODE"));
        homePanel.setCountry(vcard.getAddressFieldHome("CTRY"));
        homePanel.setPhone(vcard.getPhoneHome("VOICE"));
        homePanel.setFax(vcard.getPhoneHome("FAX"));
        homePanel.setPager(vcard.getPhoneHome("PAGER"));
        homePanel.setMobile(vcard.getPhoneHome("CELL"));

        // Set avatar
        byte[] bytes = vcard.getAvatar();
        if (bytes != null) {
            ImageIcon icon = new ImageIcon(bytes);
            avatarPanel.setAvatar(icon);
            avatarPanel.setAvatarBytes(bytes);
            if (avatarLabel != null) {
                icon = GraphicUtils.scaleImageIcon(icon, 48, 48);

                avatarLabel.setIcon(icon);
            }
        }
    }

    public VCard getVCard() {
        if (!vcardLoaded) {
            try {
                vcard.load(SparkManager.getConnection());

                // If VCard is loaded, then save the avatar to the personal folder.
                byte[] bytes = vcard.getAvatar();
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
            }
            vcardLoaded = true;
        }
        return vcard;
    }

    private ImageIcon getAvatarIcon() {
        // Set avatar
        byte[] bytes = vcard.getAvatar();
        if (bytes != null) {
            ImageIcon icon = new ImageIcon(bytes);
            return GraphicUtils.scaleImageIcon(icon, 40, 40);
        }
        return null;
    }

    /**
     * Returns the VCard associated with a jid.
     *
     * @param jid the jid.
     * @return the VCard.
     */
    public VCard getVCard(String jid) {
        if (!vcardMap.containsKey(jid)) {
            VCard vcard = new VCard();
            vcard.setJabberId(jid);
            try {
                vcard.load(SparkManager.getConnection(), jid);
                vcardMap.put(jid, vcard);
            }
            catch (XMPPException e) {
                Log.warning("Unable to load vcard for " + jid, e);
                vcard.setError(new XMPPError(409));
                return vcard;
            }
        }
        return (VCard)vcardMap.get(jid);
    }

    public void addVCard(String jid, VCard vcard) {
        vcardMap.put(jid, vcard);
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
                    e.printStackTrace();
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
                e.printStackTrace();
            }
        }

        if (avatarURL == null) {
            return SparkRes.getURL(SparkRes.DUMMY_CONTACT_IMAGE);
        }

        return avatarURL;
    }

    public VCard searchPhoneNumber(String phoneNumber) {
        for (VCard vcard : vcardMap.values()) {
            String homePhone = getNumbersFromPhone(vcard.getPhoneHome("VOICE"));
            String workPhone = getNumbersFromPhone(vcard.getPhoneWork("VOICE"));

            String query = getNumbersFromPhone(phoneNumber);
            if ((homePhone != null && homePhone.contains(query)) || (workPhone != null && workPhone.contains(query))) {
                return vcard;
            }
        }

        return null;
    }

    public static String getNumbersFromPhone(String number) {
        if (number == null) {
            return null;
        }

        number = number.replace("-", "");
        number = number.replace("(", "");
        number = number.replace(")", "");
        if (number.startsWith("1")) {
            number = number.substring(1);
        }
        return number;
    }

}
