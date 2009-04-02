/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.emoticons;

import org.jivesoftware.spark.component.RolloverButton;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class EmoticonUI extends JPanel {
	private static final long serialVersionUID = 2360054381356167669L;
	private EmoticonPickListener listener;

	public EmoticonUI() {
		setBackground(Color.white);

		final EmoticonManager manager = EmoticonManager.getInstance();

		Collection<Emoticon> emoticons = manager.getActiveEmoticonSet();

		if (emoticons != null) {

			int no = emoticons.size();

			int rows = no / 5;

			setLayout(new GridLayout(rows, 5));

			// Add Emoticons
			for (Emoticon emoticon : emoticons) {
				final String text = emoticon.getEquivalants().get(0);
				String name = manager.getActiveEmoticonSetName();

				final Emoticon smileEmoticon = manager.getEmoticon(name, text);
				URL smileURL = manager.getEmoticonURL(smileEmoticon);

				// Add Emoticon button
				ImageIcon icon = new ImageIcon(smileURL);

				RolloverButton emotButton = new RolloverButton();
				emotButton.setIcon(icon);
				emotButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						listener.emoticonPicked(text);
					}
				});
				add(emotButton);
			}
		}
	}

	public void setEmoticonPickListener(EmoticonPickListener listener) {
		this.listener = listener;
	}

	public interface EmoticonPickListener {
		void emoticonPicked(String emoticon);
	}
}
