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
import org.jivesoftware.spark.component.borders.PartialLineBorder;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.profile.ext.JabberAvatarExtension;
import org.jivesoftware.sparkimpl.profile.ext.VCardUpdateExtension;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class VCardManager {
    private BusinessPanel businessPanel;
    private PersonalPanel personalPanel;
    private HomePanel homePanel;
    private AvatarPanel avatarPanel;
    private JLabel avatarLabel;
    private VCard vcard = new VCard();

    private Map vcardMap = new HashMap();
    private boolean vcardLoaded = false;

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
        tabbedPane.addTab("Personal", personalPanel);

        businessPanel = new BusinessPanel();
        tabbedPane.addTab("Business", businessPanel);

        homePanel = new HomePanel();
        tabbedPane.addTab("Home", homePanel);

        avatarPanel = new AvatarPanel();
        tabbedPane.addTab("Avatar", avatarPanel);

        loadVCard(SparkManager.getSessionManager().getJID());

        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        ImageIcon icon = getAvatarIcon();
        if (icon == null) {
            icon = SparkRes.getImageIcon(SparkRes.BLANK_24x24);
        }

        // Create the title panel for this dialog
        titlePanel = new TitlePanel("Edit Profile Information", "To save changes to your profile, click Save.", icon, true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {"Save", "Cancel"};
        pane = new JOptionPane(tabbedPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, "Profile Information");
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(600, 400);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if ("Cancel".equals(value)) {
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
                else if ("Save".equals(value)) {
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
                if (userVCard.getError() != null) {
                    // Show vcard not found
                    JOptionPane.showMessageDialog(parent, "Unable to locate a profile for " + jid, "Profile Not Found", JOptionPane.ERROR_MESSAGE);
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
        tabbedPane.addTab("Personal", personalPanel);

        businessPanel = new BusinessPanel();
        tabbedPane.addTab("Business", businessPanel);

        homePanel = new HomePanel();
        tabbedPane.addTab("Home", homePanel);

        avatarPanel = new AvatarPanel();
        avatarPanel.setEditable(false);

        personalPanel.allowEditing(false);
        businessPanel.allowEditing(false);
        homePanel.allowEditing(false);

        final JOptionPane pane;
        final JFrame dlg;

        avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(JButton.RIGHT);
        avatarLabel.setBorder(new PartialLineBorder(Color.gray, 1));

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(avatarLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));

        // The user should only be able to close this dialog.
        Object[] options = {"Close"};
        pane = new JOptionPane(tabbedPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));

        dlg = new JFrame("Viewing Profile For " + jid);
        dlg.setIconImage(SparkRes.getImageIcon(SparkRes.SMALL_PROFILE_IMAGE).getImage());

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
                if ("Close".equals(value)) {
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

        populateVCardUI(vcard);

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
        final JMenu contactsMenu = SparkManager.getMainWindow().getMenuByName("Contacts");
        final JMenu communicatorMenu = SparkManager.getMainWindow().getJMenuBar().getMenu(0);

        JMenuItem editProfileMenu = new JMenuItem("Edit Profile...", SparkRes.getImageIcon(SparkRes.SMALL_BUSINESS_MAN_VIEW));
        ResourceUtils.resButton(editProfileMenu, "&Edit My Profile...");

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
                        return "ok";
                    }

                    public void finished() {
                        showProfile(SparkManager.getWorkspace());
                    }
                };
                vcardLoaderWorker.start();
            }
        });

        JMenuItem viewProfileMenu = new JMenuItem("Lookup Profile...", SparkRes.getImageIcon(SparkRes.FIND_TEXT_IMAGE));
        ResourceUtils.resButton(viewProfileMenu, "&Lookup Profile...");
        contactsMenu.insert(viewProfileMenu, size - 1);
        viewProfileMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String jidToView = JOptionPane.showInputDialog(SparkManager.getMainWindow(), "Enter Jabber ID:", "Lookup Profile", JOptionPane.QUESTION_MESSAGE);
                if (ModelUtil.hasLength(jidToView) && jidToView.indexOf("@") != -1 && ModelUtil.hasLength(StringUtils.parseServer(jidToView))) {
                    viewProfile(jidToView, SparkManager.getWorkspace());
                }
                else if (ModelUtil.hasLength(jidToView)) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Not a valid Jabber ID", "Invalid JID", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return true;
    }

    private void loadVCard(String jid) {
        final String userJID = StringUtils.parseBareAddress(jid);
        final VCard userVCard = new VCard();

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    userVCard.load(SparkManager.getConnection(), userJID);
                }
                catch (XMPPException e) {
                }
                return "ok";
            }

            public void finished() {
                populateVCardUI(userVCard);
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
                        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
                            SparkManager.getMainWindow().setNickname(firstName + " " + lastName);
                        }
                        else if (ModelUtil.hasLength(firstName)) {
                            SparkManager.getMainWindow().setNickname(firstName);
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
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Server does not support VCards. Unable to save your VCard.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else {

                }
            }
        };

        worker.start();


    }

    private void populateVCardUI(VCard vcard) {
        personalPanel.setFirstName(vcard.getFirstName());
        personalPanel.setMiddleName(vcard.getMiddleName());
        personalPanel.setLastName(vcard.getLastName());
        personalPanel.setEmailAddress(vcard.getEmailHome());
        personalPanel.setNickname(vcard.getNickName());

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
            }
            catch (XMPPException e) {
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

    public VCard getVCard(String jid) {
        if (!vcardMap.containsKey(jid)) {
            VCard vcard = new VCard();
            try {
                vcard.load(SparkManager.getConnection(), jid);
                vcardMap.put(jid, vcard);
            }
            catch (XMPPException e) {
                Log.warning("Unable to load vcard for " + jid, e);
                vcard.setError(new XMPPError(409));
            }
        }
        return (VCard)vcardMap.get(jid);
    }

    public void addVCard(String jid, VCard vcard) {
        vcardMap.put(jid, vcard);
    }

    public static ImageIcon scale(ImageIcon icon) {
        Image avatarImage = icon.getImage();
        if (icon.getIconHeight() > 64 || icon.getIconWidth() > 64) {
            avatarImage = avatarImage.getScaledInstance(-1, 64, Image.SCALE_SMOOTH);
        }

        return new ImageIcon(avatarImage);
    }

}
