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
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.VCardViewer;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Handles the UI for viewing and editing of VCard information.
 */
public class VCardEditor {

    private BusinessPanel businessPanel;
    private PersonalPanel personalPanel;
    private HomePanel homePanel;
    private AvatarPanel avatarPanel;
    private JLabel avatarLabel;

    /**
     * Displays the VCard for an individual.
     *
     * @param vCard  the users vcard.
     * @param parent the parent component, used for location.
     */
    public void editProfile(final VCard vCard, JComponent parent) {
        final JTabbedPane tabbedPane = new JTabbedPane();

        // Initialize Panels
        personalPanel = new PersonalPanel();
        personalPanel.showJID(false);

        tabbedPane.addTab(Res.getString("tab.personal"), personalPanel);

        businessPanel = new BusinessPanel();
        tabbedPane.addTab(Res.getString("tab.business"), businessPanel);

        homePanel = new HomePanel();
        tabbedPane.addTab(Res.getString("tab.home"), homePanel);

        avatarPanel = new AvatarPanel();
        tabbedPane.addTab(Res.getString("tab.avatar"), avatarPanel);

        // Build the UI
        buildUI(vCard);

        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        ImageIcon icon = VCardManager.getAvatarIcon(vCard);
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
        avatarPanel.setParentDialog(dlg);
        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        personalPanel.focus();
    }

    /**
     * Displays the VCard for an individual.
     *
     * @param vCard  the users vcard.
     * @param parent the parent component, used for location.
     */
    public void viewFullProfile(final VCard vCard, JComponent parent) {
        final JTabbedPane tabbedPane = new JTabbedPane();

        // Initialize Panels
        personalPanel = new PersonalPanel();
        personalPanel.allowEditing(false);
        personalPanel.showJID(false);

        tabbedPane.addTab(Res.getString("tab.personal"), personalPanel);

        businessPanel = new BusinessPanel();
        businessPanel.allowEditing(false);
        tabbedPane.addTab(Res.getString("tab.business"), businessPanel);

        homePanel = new HomePanel();
        homePanel.allowEditing(false);
        tabbedPane.addTab(Res.getString("tab.home"), homePanel);

        avatarPanel = new AvatarPanel();
        avatarPanel.allowEditing(false);
        tabbedPane.addTab(Res.getString("tab.avatar"), avatarPanel);

        // Build the UI
        buildUI(vCard);

        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        ImageIcon icon = VCardManager.getAvatarIcon(vCard);
        if (icon == null) {
            icon = SparkRes.getImageIcon(SparkRes.BLANK_24x24);
        }

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.profile.information"), "", icon, true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("close")};
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
                Object o = pane.getValue();
                if (o instanceof Integer) {
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

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        personalPanel.focus();
    }

    /**
     * Displays a users profile.
     *
     * @param jid    the jid of the user.
     * @param vcard  the users vcard.
     * @param parent the parent component, used for location handling.
     */
    public void displayProfile(final String jid, VCard vcard, JComponent parent) {
        VCardViewer viewer = new VCardViewer(jid);

        final JFrame dlg = new JFrame(Res.getString("title.view.profile.for", jid));


        avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(JButton.RIGHT);
        avatarLabel.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
        
        // The user should only be able to close this dialog.
        Object[] options = { Res.getString("button.view.profile"), Res.getString("close")};
        final JOptionPane pane = new JOptionPane(viewer, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        //  mainPanel.add(pane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));


        dlg.setIconImage(SparkRes.getImageIcon(SparkRes.PROFILE_IMAGE_16x16).getImage());

        dlg.pack();
        dlg.setSize(350, 250);
        dlg.setResizable(true);
        dlg.setContentPane(pane);
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
                else if (Res.getString("button.view.profile").equals(value)) {
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    SparkManager.getVCardManager().viewFullProfile(jid, pane);
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

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
    }


    /**
     * Builds the UI based on a VCard.
     *
     * @param vcard the vcard used to build the UI.
     */
    private void buildUI(VCard vcard) {
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
        if (bytes != null && bytes.length > 0) {
            ImageIcon icon = new ImageIcon(bytes);
            avatarPanel.setAvatar(icon);
            avatarPanel.setAvatarBytes(bytes);
            if (avatarLabel != null) {
                icon = GraphicUtils.scaleImageIcon(icon, 48, 48);

                avatarLabel.setIcon(icon);
            }
        }
    }

    /**
     * Saves the VCard.
     */
    private void saveVCard() {
        final VCard vcard = new VCard();

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

        // Save Avatar
        final File avatarFile = avatarPanel.getAvatarFile();
        byte[] avatarBytes = avatarPanel.getAvatarBytes();

        if (avatarFile != null) {
            try {
                // Make it 48x48
                ImageIcon icon = new ImageIcon(avatarFile.toURI().toURL());
                Image image = icon.getImage();
                image = image.getScaledInstance(-1, 48, Image.SCALE_SMOOTH);
                avatarBytes = GraphicUtils.getBytesFromImage(image);
            }
            catch (MalformedURLException e) {
                Log.error("Unable to set avatar.", e);
            }
        }

        // If avatar bytes, persist as vcard.
        if (avatarBytes != null) {
            vcard.setAvatar(avatarBytes);
        }

        try {
            final VCardManager vcardManager = SparkManager.getVCardManager();
            vcardManager.setPersonalVCard(vcard);

            vcard.save(SparkManager.getConnection());

            // Notify users.
            if (avatarFile != null || avatarBytes != null) {
                Presence presence = SparkManager.getWorkspace().getStatusBar().getPresence();
                Presence newPresence = new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());

                // Change my own presence
                SparkManager.getSessionManager().changePresence(newPresence);

                // Chnage avatar in status bar.
                StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
                statusBar.setAvatar(new ImageIcon(vcard.getAvatar()));
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

                statusBar.setAvatar(null);
            }

            // Notify listenres
            SparkManager.getVCardManager().notifyVCardListeners();
        }
        catch (XMPPException e) {
            Log.error(e);
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.vcard.not.supported"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
        }
    }


}


