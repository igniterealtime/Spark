package org.jivesoftware.spark.otrplug.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.otrplug.util.OTRResources;
import org.jivesoftware.spark.ui.TranscriptWindow;
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
    private StyledDocument _doc;
    private TranscriptWindow _transcriptWindow;
    private JButton _retry;
    private boolean _succesfull = false;
    private boolean _waiting = false;

    public OTRConnectionPanel(ChatRoomImpl chatroom) {
        _chatRoom = chatroom;
        _transcriptWindow = _chatRoom.getTranscriptWindow();
        _doc = _transcriptWindow.getStyledDocument();
        _retry = new JButton(OTRResources.getString("otr.retry"));
        _retry.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OTRManager.getInstance().startOtrWithUser(_chatRoom.getParticipantJID());

            }
        });

        _retry.setVisible(false);

    }

    /**
     * Indicates that OTR is trying to establish an OTR session. This will
     * inject a styledDocument. You have 10 seconds to approve that the
     * connection was successful using method successfullyCon()
     * 
     */
    public void tryToStart() {
        if (!_succesfull && !_waiting) {
            renewPanel();
            _icon.setImage(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE).getImage());

            _conPanel.setVisible(true);

            _i = 10;
            _label.setText(OTRResources.getString("otr.try.to.connect.for.seconds", _i));

            Timer t = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {

                    if (_i > 0 && !_succesfull) {
                        _waiting = true;
                        _label.setText(OTRResources.getString("otr.try.to.connect.for.seconds", _i));
                        decI();
                    } else if (!_succesfull) {
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
    }

    private void renewPanel() {
        _conPanel = new JPanel(new GridBagLayout());
        _label = new JLabel();
        _icon = new ImageIcon();

        _conPanel.add(new JLabel(_icon), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        _conPanel.add(_label, new GridBagConstraints(1, 0, 1, 1, 0.7, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 15, 0, 0), 0, 0));
        _conPanel.add(_retry, new GridBagConstraints(2, 0, 1, 1, 2.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));

        _transcriptWindow.addComponent(_conPanel);
        _chatRoom.scrollToBottom();
    }

    /**
     * Indicates in the transcript window, that the OTR session has been closed
     */
    public void connectionClosed() {
        if (_succesfull) {
            renewPanel();
            _succesfull = false;
            _label.setText(OTRResources.getString("otr.disconnected"));
            _icon.setImage(SparkRes.getImageIcon(SparkRes.SMALL_STOP).getImage());
            _conPanel.setVisible(true);
        }
    }

    private void decI() {
        --_i;
    }

    /**
     * Should be called after you called tryToStart(). It will indicate that the
     * session is now encrypted
     */
    public void successfullyCon() {
        if (!_succesfull) {
            if (!_waiting)
                renewPanel();
            _retry.setVisible(false);
            _conPanel.setVisible(true);
            _succesfull = true;
            _waiting = false;
            _icon.setImage(SparkRes.getImageIcon(SparkRes.SMALL_CHECK).getImage());
            _label.setText(OTRResources.getString("otr.successfull"));
        }
    }

}
