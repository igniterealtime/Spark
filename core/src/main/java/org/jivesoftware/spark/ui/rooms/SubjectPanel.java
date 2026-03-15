package org.jivesoftware.spark.ui.rooms;

import org.jivesoftware.spark.util.XEP0392Utils;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.EntityBareJid;

import javax.swing.*;
import java.awt.*;

/**
 * A UI implementation for display subjects within the conference room.
 */
public class SubjectPanel extends JPanel
{
    private final JLabel roomJIDLabel;
    private final JLabel subjectLabel;

    public SubjectPanel( GroupChatRoom groupChatRoom )
    {
        setLayout( new GridBagLayout() );
        EntityBareJid roomJid = groupChatRoom.getBareJid();
        roomJIDLabel = new JLabel( "<" + roomJid + ">" );
        roomJIDLabel.setFont( roomJIDLabel.getFont().deriveFont( Font.BOLD ) );
        if (SettingsManager.getLocalPreferences().isMucRandomColors()) {
            roomJIDLabel.setForeground(XEP0392Utils.colorOfMuc(roomJid));
        }

        subjectLabel = new JLabel( groupChatRoom.getMultiUserChat().getSubject() );

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

        setOpaque( false );
    }

    public void setSubject( String subject )
    {
        subjectLabel.setText( subject );
        this.setToolTipText( subject );
    }

    public void setRoomLabel( String label )
    {
        roomJIDLabel.setText( label );
    }
}
