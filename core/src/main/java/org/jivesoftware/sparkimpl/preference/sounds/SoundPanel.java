package org.jivesoftware.sparkimpl.preference.sounds;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.awt.GridBagConstraints.*;

class SoundPanel extends JPanel {
    private final JCheckBox incomingMessageBox = new JCheckBox();
    private final JTextField incomingMessageSound = new JTextField();
    private final JButton incomingBrowseButton = new JButton("..");

    private final JCheckBox outgoingMessageBox = new JCheckBox();
    private final JTextField outgoingMessageSound = new JTextField();
    private final JButton outgoingBrowseButton = new JButton("..");

    private final JCheckBox userOfflineCheckbox = new JCheckBox();
    private final JTextField userOfflineField = new JTextField();
    private final JButton offlineBrowseButton = new JButton("..");

    private final JCheckBox incomingInvitationBox = new JCheckBox();
    private final JTextField incomingInvitationField = new JTextField();
    private final JButton incomingInvitationBrowseButton = new JButton("..");

    private final JCheckBox chatRequestBox = new JCheckBox();
    private final JTextField chatRequestSound = new JTextField();
    private final JButton chatRequestBrowseButton = new JButton("..");

    public SoundPanel() {
        setLayout(new GridBagLayout());

        setBorder(BorderFactory.createTitledBorder(Res.getString("title.sound.preferences")));
        // Add ResourceUtils
        ResourceUtils.resButton(incomingMessageBox, Res.getString("checkbox.play.sound.on.new.message"));
        ResourceUtils.resButton(outgoingMessageBox, Res.getString("checkbox.play.sound.on.outgoing.message"));
        ResourceUtils.resButton(userOfflineCheckbox, Res.getString("checkbox.play.sound.when.offline"));
        ResourceUtils.resButton(incomingInvitationBox, Res.getString("checkbox.play.sound.on.invitation"));
        ResourceUtils.resButton(chatRequestBox, Res.getString("checkbox.play.sound.chat_request"));

        Insets padding = new Insets(5, 5, 5, 5);
        // Handle incoming sounds
        add(incomingMessageBox, new GridBagConstraints(0, 0, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));
        add(incomingMessageSound, new GridBagConstraints(0, 1, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, padding, 0, 0));
        add(incomingBrowseButton, new GridBagConstraints(1, 1, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));

        // Handle sending sounds
        add(outgoingMessageBox, new GridBagConstraints(0, 2, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));
        add(outgoingMessageSound, new GridBagConstraints(0, 3, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, padding, 0, 0));
        add(outgoingBrowseButton, new GridBagConstraints(1, 3, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));

        // Handle User Online Sound
        add(userOfflineCheckbox, new GridBagConstraints(0, 4, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));
        add(userOfflineField, new GridBagConstraints(0, 5, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, padding, 0, 0));
        add(offlineBrowseButton, new GridBagConstraints(1, 5, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));

        // Handle Invitation Sound
        add(incomingInvitationBox, new GridBagConstraints(0, 6, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));
        add(incomingInvitationField, new GridBagConstraints(0, 7, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, padding, 0, 0));
        add(incomingInvitationBrowseButton, new GridBagConstraints(1, 7, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));
        // Handle Chat Request Sound
        add(chatRequestBox, new GridBagConstraints(0, 8, 1, 1, 0, 0, NORTHWEST, NONE, padding, 0, 0));
        add(chatRequestSound, new GridBagConstraints(0, 9, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, padding, 0, 0));
        add(chatRequestBrowseButton, new GridBagConstraints(1, 9, 1, 1, 0, 1, NORTHWEST, NONE, padding, 0, 0));

        incomingBrowseButton.addActionListener(e -> pickFile(Res.getString("title.choose.incoming.sound"), incomingMessageSound));
        outgoingBrowseButton.addActionListener(e -> pickFile(Res.getString("title.choose.outgoing.sound"), outgoingMessageSound));
        offlineBrowseButton.addActionListener(e -> pickFile(Res.getString("title.choose.offline.sound"), userOfflineField));
        incomingInvitationBrowseButton.addActionListener(e -> pickFile(Res.getString("title.choose.incoming.sound"), incomingInvitationField));
        chatRequestBrowseButton.addActionListener(e -> pickFile(Res.getString("title.choose.chat_request.sound"), chatRequestSound));
    }

    public void setIncomingMessageSound(String path) {
        incomingMessageSound.setText(path);
    }

    public void setOutgoingMessageSound(String path) {
        outgoingMessageSound.setText(path);
    }

    public void setOfflineSound(String path) {
        userOfflineField.setText(path);
    }

    public void playIncomingSound(boolean play) {
        incomingMessageBox.setSelected(play);
    }

    public void playOutgoingSound(boolean play) {
        outgoingMessageBox.setSelected(play);
    }

    public void playOfflineSound(boolean play) {
        userOfflineCheckbox.setSelected(play);
    }

    public String getIncomingSound() {
        return incomingMessageSound.getText();
    }

    public boolean playIncomingSound() {
        return incomingMessageBox.isSelected();
    }

    public boolean playOutgoingSound() {
        return outgoingMessageBox.isSelected();
    }

    public String getOutgoingSound() {
        return outgoingMessageSound.getText();
    }

    public boolean playOfflineSound() {
        return userOfflineCheckbox.isSelected();
    }

    public String getOfflineSound() {
        return userOfflineField.getText();
    }

    public void setInvitationSound(String invitationSound) {
        incomingInvitationField.setText(invitationSound);
    }

    public String getInvitationSound() {
        return incomingInvitationField.getText();
    }

    public void setPlayInvitationSound(boolean play) {
        incomingInvitationBox.setSelected(play);
    }

    public boolean playInvitationSound() {
        return incomingInvitationBox.isSelected();
    }

    public String getChatRequestSound() {
        return chatRequestSound.getText();
    }

    public void setChatRequestSound(String sound) {
        chatRequestSound.setText(sound);
    }

    public boolean isPlayChatRequestSound() {
        return chatRequestBox.isSelected();
    }

    public void setPlayChatRequestSound(boolean play) {
        chatRequestBox.setSelected(play);
    }

    private void pickFile(String title, JTextField field) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(Res.getString("file.type.sound"), "wav"));
        if (Spark.isWindows()) {
            fc.setFileSystemView(new WindowsFileSystemView());
        }
        fc.setDialogTitle(title);
        if (!field.getText().isEmpty()) {
            fc.setSelectedFile(new File(field.getText()));
        }

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                field.setText(file.getCanonicalPath());
            } catch (IOException e) {
                Log.error(e);
            }
        }
    }
}
