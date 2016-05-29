package org.jivesoftware.spark.roar.displaytype;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.roar.RoarResources;
import org.jivesoftware.spark.roar.gui.SparkToaster;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;

/**
 * Handles Popups looking like Toasts in lower right corner going upwards
 * 
 * @author wolf.posdorfer
 * 
 */
public class SparkToasterHandler implements RoarDisplayType {

    public SparkToasterHandler() {

    }

    @Override
    public void messageReceived(ChatRoom room, Message message, PropertyBundle property) {


	SparkToaster toaster = new SparkToaster();
	toaster.setDisplayTime(property.duration);
	toaster.setBorder(BorderFactory.createBevelBorder(0));
	toaster.setTitle(room.getTabTitle());

	toaster.setCustomAction(new AbstractAction() {
	    private static final long serialVersionUID = 8327372636443172019L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		ChatFrame chatFrame = SparkManager.getChatManager()
			.getChatContainer().getChatFrame();
		chatFrame.setState(Frame.NORMAL);
		chatFrame.setVisible(true);

	    }
	});

	toaster.showToaster(room.getTabIcon(), message.getBody());

    }

    @Override
    public void messageSent(ChatRoom room, Message message) {
	// i dont care

    }

    @Override
    public void closingRoarPanel(int x, int y) {
	// i dont care

    }

    @Override
    public String toString() {
        return "SparkToaster";
    }

    public String getName() {
        return "SparkToaster";
    }

    public String getLocalizedName() {
        return "SparkToaster";
    }

    @Override
    public String getWarningMessage() {
        return RoarResources.getString("roar.warning.toaster");
    }

}
