package org.jivesoftware.spark.otrplug.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.otrplug.util.OTRResources;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

/**
 * Shows StyledDocuments in transcript window for info if the session
 * established successfully or not
 * 
 * @author Bergunde Holger
 */
public class OTRConnectionPanel {

    private ChatRoomImpl _chatRoom;
    private JLabel _label;
    private ImageIcon _icon;
    private JPanel _conPanel;
    private int _i;
    private JButton _retry;
    private boolean _successful = false;
    private boolean _waiting = false;

    public OTRConnectionPanel(ChatRoomImpl chatroom) {
        _chatRoom = chatroom;
        _retry = new JButton(OTRResources.getString("otr.retry"));
        _retry.addActionListener(e -> {
            OTRManager.getInstance().startOtrWithUser(_chatRoom.getParticipantJID().toString());
        });
        _retry.setVisible(false);
    }

    /**
     * Indicates that OTR is trying to establish an OTR session. This will inject a styledDocument.
     * You have 10 seconds to approve that the connection was successful using method successfullyCon()
     */
    public void tryToStart() {
        if (_successful || _waiting) {
            return;
        }
        renewPanel();
        _icon.setImage(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE).getImage());
        _conPanel.setVisible(true);
        _i = 10;
        _label.setText(OTRResources.getString("otr.try.to.connect.for.seconds", _i));

        Timer t = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                if (_i > 0 && !_successful) {
                    _waiting = true;
                    _label.setText(OTRResources.getString("otr.try.to.connect.for.seconds", _i));
                    decI();
                } else if (!_successful) {
                    _waiting = true;
                    _icon.setImage(SparkRes.getImageIcon(SparkRes.SMALL_DELETE).getImage());
                    _label.setText(OTRResources.getString("otr.failed.to.establish", _i));
                    _retry.setVisible(true);
                    this.cancel();
                } else {
                    this.cancel();
                }
            }
        };
        t.scheduleAtFixedRate(task, 0, 1000);
    }

    private void renewPanel() {
        _conPanel = new JPanel(new GridBagLayout());
        _label = new JLabel();
        _icon = new ImageIcon();

        _conPanel.add(new JLabel(_icon), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        _conPanel.add(_label, new GridBagConstraints(1, 0, 1, 1, 0.7, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 15, 0, 0), 0, 0));
        _conPanel.add(_retry, new GridBagConstraints(2, 0, 1, 1, 2.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));

        _chatRoom.getTranscriptWindow().addComponent(_conPanel);
        _chatRoom.scrollToBottom();
    }

    /**
     * Indicates in the transcript window, that the OTR session has been closed
     */
    public void connectionClosed() {
        if (!_successful) {
            return;
        }
        renewPanel();
        _successful = false;
        _label.setText(OTRResources.getString("otr.disconnected"));
        _icon.setImage(SparkRes.getImageIcon(SparkRes.SMALL_STOP).getImage());
        _conPanel.setVisible(true);
    }

    private void decI() {
        --_i;
    }

    /**
     * Should be called after you called tryToStart(). It will indicate that the
     * session is now encrypted
     */
    public void successfullyCon() {
        if (_successful) {
            return;
        }
        if (!_waiting) {
            renewPanel();
        }
        _retry.setVisible(false);
        _conPanel.setVisible(true);
        _successful = true;
        _waiting = false;
        _icon.setImage(SparkRes.getImageIcon(SparkRes.SMALL_CHECK).getImage());
        _label.setText(OTRResources.getString("otr.successfull"));
    }

}
