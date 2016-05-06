package org.jivesoftware.spark.ui.rooms;

import javax.swing.*;
import java.awt.*;

/**
 * A UI implementation for display subjects within the conference room.
 */
public class SubjectPanel extends JPanel
{
    private JLabel roomJIDLabel;
    private JLabel subjectLabel;

    public SubjectPanel( GroupChatRoom groupChatRoom )
    {
        setLayout( new GridBagLayout() );

        roomJIDLabel = new JLabel( "<" + groupChatRoom.getMultiUserChat().getRoom() + ">" );
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
