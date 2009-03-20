package org.jivesoftware.spellchecker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.dts.spell.swing.JTextComponentSpellChecker;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.util.GraphicUtils;

public class SpellcheckChatRoomDecorator  implements ActionListener, ChatRoomClosingListener
{
	private JTextComponentSpellChecker sc;
	private RolloverButton spellingButton;
	private ChatRoom room;	

	public SpellcheckChatRoomDecorator(ChatRoom room)
	{
		this.room = room;
		
		SpellcheckerPreference preference = (SpellcheckerPreference) SparkManager.getPreferenceManager().getPreference(SpellcheckerPreference.NAMESPACE);
		if (preference.getPreferences().isSpellCheckerEnabled()) {
			sc = new JTextComponentSpellChecker(SpellcheckManager.getInstance().getSpellChecker());
			
			if (preference.getPreferences().isAutoSpellCheckerEnabled()) {
				sc.startRealtimeMarkErrors(room.getChatInputEditor());
			}
			
	        ClassLoader cl = getClass().getClassLoader();
	        ImageIcon spellingIcon = new ImageIcon(cl.getResource("text_ok.png"));
	        spellingButton = new RolloverButton(spellingIcon);
	        spellingButton.setToolTipText(GraphicUtils.createToolTip("Check Spelling"));
	        spellingButton.addActionListener(this);
	        room.getEditorBar().add(spellingButton);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {	
		if (sc.spellCheck(room.getChatInputEditor())) {
			JOptionPane.showMessageDialog(room.getChatInputEditor(), "Text is OK") ;
			room.getChatInputEditor().requestFocusInWindow();
		}
	}
	
	@Override
	public void closing() {

	}


	


}
