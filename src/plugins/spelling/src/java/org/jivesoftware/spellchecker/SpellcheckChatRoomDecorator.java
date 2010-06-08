/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
