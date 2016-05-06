package org.jivesoftware.spark.roar.displaytype;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.roar.RoarProperties;
import org.jivesoftware.spark.roar.RoarResources;
import org.jivesoftware.spark.roar.gui.RoarPanel;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;

/**
 * handles Popups in the lower right corner and stacking upwards
 * 
 * @author wolf.posdorfer
 * 
 */
public class BottomRight implements RoarDisplayType {

    private int _lastusedXpos;
    private int _lastusedYpos;

    private int _defaultx;
    private int _defaulty;

    private int _amount;

    private final int WIDTH = RoarPanel.WIDTH;
    private final int HEIGHT = RoarPanel.HEIGHT;
    private AbstractAction _customaction;

    private static final int TASKBAR = 35;

    public BottomRight() {
        Dimension _screensize = Toolkit.getDefaultToolkit().getScreenSize();

        _lastusedXpos = _screensize.width - 5;
        _lastusedYpos = _screensize.height - HEIGHT - TASKBAR;

        _defaultx = _lastusedXpos;
        _defaulty = _lastusedYpos;

        _amount = 0;

        _customaction = new AbstractAction() {
            private static final long serialVersionUID = -7237306342417462544L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();
                chatFrame.setState(Frame.NORMAL);
                chatFrame.setVisible(true);
            }
        };
    }

    @Override
    public void messageReceived(ChatRoom room, Message message, PropertyBundle property) {
        RoarProperties props = RoarProperties.getInstance();

        if (props.getShowingPopups() && (_amount < props.getMaximumPopups() || props.getMaximumPopups() == 0)) {

            ImageIcon icon = SparkRes.getImageIcon(SparkRes.SPARK_IMAGE_32x32);

            String nickname = RoarPopupHelper.getNickname(room, message);
            RoarPanel.popupWindow(this, icon, nickname, message.getBody(), _lastusedXpos, _lastusedYpos,
                    property.duration, property.backgroundColor, property.headerColor, property.textColor,
                    _customaction);

            ++_amount;

            _lastusedYpos -= (HEIGHT + 5);

            if (_lastusedYpos <= HEIGHT + 5) {
                _lastusedXpos -= WIDTH + 5;
                _lastusedYpos = _defaulty;
            }
        }

    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
        // dont care
    }

    @Override
    public void closingRoarPanel(int x, int y) {

        if (_lastusedYpos < (y + 5 + TASKBAR)) {
            _lastusedYpos = y - 5;
        }
        if (_lastusedXpos < (x + 5)) {
            _lastusedXpos = x + WIDTH + 5;
        }

        if (_lastusedYpos > _defaulty) {
            _lastusedYpos = _defaulty;
        }

        --_amount;

        if (_amount <= 0) {
            _amount = 0;
            _lastusedXpos = _defaultx;
            _lastusedYpos = _defaulty;
        }

    }

    @Override
    public String toString() {
        return "BottomRight";
    }

    @Override
    public String getName() {
        return "BottomRight";
    }

    @Override
    public String getLocalizedName() {
        return RoarResources.getString("roar.display.bottomright");
    }

    @Override
    public String getWarningMessage() {
        return RoarResources.getString("roar.warning.bottomright");
    }

}
