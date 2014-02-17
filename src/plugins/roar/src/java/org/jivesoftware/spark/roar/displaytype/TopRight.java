package org.jivesoftware.spark.roar.displaytype;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.roar.RoarProperties;
import org.jivesoftware.spark.roar.RoarResources;
import org.jivesoftware.spark.roar.gui.RoarPanel;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;

/**
 * handles Popups in the upper right corner and stacking downwards
 * 
 * @author wolf.posdorfer
 * 
 */
public class TopRight implements RoarDisplayType {

    private int _lastusedXpos;
    private int _lastusedYpos;
    private Dimension _screensize;

    private int _amount;

    private final int WIDTH = RoarPanel.WIDTH;
    private final int HEIGHT = RoarPanel.HEIGHT;

    private Action _customaction;

    public TopRight() {
	_screensize = Toolkit.getDefaultToolkit().getScreenSize();

	_lastusedXpos = _screensize.width - 5;
	_lastusedYpos = 5;
	_amount = 0;

	_customaction = new AbstractAction() {
	    private static final long serialVersionUID = -7237306342417462544L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		ChatFrame chatFrame = SparkManager.getChatManager()
			.getChatContainer().getChatFrame();
		chatFrame.setState(Frame.NORMAL);
		chatFrame.setVisible(true);
	    }
	};
    }

    @Override
    public void messageReceived(ChatRoom room, Message message) {
	RoarProperties props = RoarProperties.getInstance();

	if (props.getShowingPopups()
		&& (_amount < props.getMaximumPopups() || props
			.getMaximumPopups() == 0)) {

	    ImageIcon icon = SparkRes.getImageIcon(SparkRes.SPARK_IMAGE_32x32);

	    String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom());
	    if(room.getChatType() == Message.Type.groupchat)
	    {
		nickname = StringUtils.parseResource(nickname);
	    }

	    RoarPanel.popupWindow(this, icon, nickname, message.getBody(),
		    _lastusedXpos, _lastusedYpos, props.getDuration(),
		    props.getBackgroundColor(), props.getHeaderColor(),
		    props.getTextColor(), _customaction);

	    ++_amount;
	    _lastusedYpos += HEIGHT + 5;

	    if (_lastusedYpos >= _screensize.height - 90) {
		_lastusedXpos -= WIDTH + 5;
		_lastusedYpos = 5;
	    }
	}

    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
	// dont care
    }

    @Override
    public void closingRoarPanel(int x, int y) {
	if (_lastusedYpos > (y - 5)) {
	    _lastusedYpos = y - 5;
	}

	if (_lastusedXpos < (x + 5)) {
	    _lastusedXpos = x + WIDTH + 5;
	}

	--_amount;
	
	if (_amount == 0) {
	    _lastusedXpos = _screensize.width - 5;
	    _lastusedYpos = 5;
	}

    }

    @Override
    public String toString() {
	return "TopRight";
    }

    public static String getName() {
	return "TopRight";
    }

    public static String getLocalizedName() {
	return RoarResources.getString("roar.display.topright");
    }

}
