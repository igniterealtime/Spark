package org.jivesoftware.spark.ui.rooms;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.XEP0392Utils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.Sizes;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.EntityBareJid;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

/**
 * UI for display the conference room header with subject, avatar and controls.
 */
public class SubjectPanel extends JPanel
{
    private final JLabel avatarImage;
    private final JLabel roomJIDLabel;
    private final JLabel subjectLabel;

    public SubjectPanel(EntityBareJid roomJid, String roomName, String subject, byte[] avatarBytes)
    {
        setLayout(new GridBagLayout());
        setOpaque(false);
        avatarImage = new JLabel();
        add(avatarImage, new GridBagConstraints(0, 0, 1, 3, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
        ImageIcon icon = null;

        byte[] bytes = avatarBytes;
        if (bytes != null && bytes.length > 0) {
            try {
                icon = new ImageIcon(bytes);
                Image newImage = icon.getImage();
                if (icon.getIconHeight() > Sizes.Avatar.VCARD || icon.getIconWidth() > Sizes.Avatar.VCARD) {
                    newImage = newImage.getScaledInstance(-1, Sizes.Avatar.VCARD, Image.SCALE_SMOOTH);
                }
                icon = new ImageIcon(newImage);
            }
            catch (Exception e) {
                Log.error("Unable to fetch image in vcard!", e);
            }
        }
        else {
            icon = SparkRes.getImageIcon(SparkRes.Icon.DEFAULT_AVATAR_64x64_IMAGE);
        }

        if (icon != null && icon.getIconWidth() > 0) {
            avatarImage.setIcon(icon);
            avatarImage.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
        }

        roomJIDLabel = new JLabel(roomName);
        roomJIDLabel.setFont( roomJIDLabel.getFont().deriveFont( Font.BOLD ) );
        if (SettingsManager.getLocalPreferences().isMucRandomColors()) {
            roomJIDLabel.setForeground(XEP0392Utils.colorOfMuc(roomJid));
        }
        roomJIDLabel.setToolTipText("xmpp:" + roomJid + "?join");

        subjectLabel = new JLabel(subject);

        add( roomJIDLabel,
                new GridBagConstraints( 1, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.HORIZONTAL, new Insets( 2, 2, 0,
                        2 ), 0, 0 ) );
        add( subjectLabel,
                new GridBagConstraints( 1, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.HORIZONTAL, new Insets( 0, 2, 0,
                        2 ), 0, 0 ) );


    }

    public void setSubject( String subject )
    {
        subjectLabel.setText( subject );
        this.setToolTipText( subject );
    }

}
