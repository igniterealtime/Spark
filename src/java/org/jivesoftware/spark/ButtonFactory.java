package org.jivesoftware.spark;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.sparkimpl.plugin.emoticons.Emoticon;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;

public class ButtonFactory {

	private static ButtonFactory instance = new ButtonFactory();

	protected ButtonFactory() {
		// nothing
	}

	public static ButtonFactory getInstance() {
		return instance;
	}

	public ChatRoomButton createChatTranscriptButton() {
		return new ChatRoomButton(SparkRes.getImageIcon(SparkRes.HISTORY_24x24_IMAGE));
	}

	public ChatRoomButton createSendFileButton() {
		return new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.SEND_FILE_24x24));
	}

	public ChatRoomButton createScreenshotButton() {
		return new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.PHOTO_IMAGE));
	}

	public ChatRoomButton createInviteConferenceButton() {
		return new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_24x24));
	}

	public RolloverButton createBuzzButton() {
		return new RolloverButton(SparkRes.getImageIcon(SparkRes.BUZZ_IMAGE));
	}

	public RolloverButton createEmoticonButton() {
		final EmoticonManager emoticonManager = EmoticonManager.getInstance();
		final String activeEmoticonSetName = emoticonManager.getActiveEmoticonSetName();
		final Emoticon smileEmoticon = emoticonManager.getEmoticon(activeEmoticonSetName, ":)");
		URL smileURL = emoticonManager.getEmoticonURL(smileEmoticon);
		ImageIcon icon = new ImageIcon(smileURL);

		return new RolloverButton(icon);
	}

	public JLabel createDivider() {
		return new JLabel(SparkRes.getImageIcon("DIVIDER_IMAGE"));
	}
}
