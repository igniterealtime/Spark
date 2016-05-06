/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
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
package org.jivesoftware.sparkimpl.plugin.emoticons;

import org.jivesoftware.spark.component.RolloverButton;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.Container;
import java.awt.Dimension;

import java.net.URL;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import javax.swing.ScrollPaneConstants;


public class EmoticonUI extends JPanel {
	private static final long serialVersionUID = 2360054381356167669L;
	private EmoticonPickListener listener;

	public EmoticonUI() {
		setBackground(Color.white);

		final EmoticonManager manager = EmoticonManager.getInstance();

		Collection<Emoticon> emoticons = manager.getActiveEmoticonSet();

		if (emoticons != null) {

			int no = emoticons.size();

                        // Emoticons per row
                        int cntInRow = 6;
                        
                        // Count rows of Emoticons
			int rows = no / cntInRow + ((no % cntInRow == 0) ? 0 : 1);

                        Container gridContainer = new Container();
                        GridLayout grid = new GridLayout(0, cntInRow);
                        JScrollPane scrollPane = new JScrollPane(gridContainer);
                        
                 	scrollPane.getViewport().setBackground(Color.WHITE);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
                        
                        gridContainer.setLayout(grid);

                        // Show only vertical scrollbar if it needed
                        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                        int scrollBarWidth = scrollPane.getVerticalScrollBar().getPreferredSize().width;

                        // Add ScrollPane to Panel
                        add(scrollPane);

//                      setIgnoreRepaint(true);

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
				emotButton.addActionListener( e -> listener.emoticonPicked(text) );

                                gridContainer.add(emotButton);
			}
                        
                        // Set up parameters of vertical scrollbar
                        scrollPane.getVerticalScrollBar().setMaximum(rows);
                        scrollPane.getVerticalScrollBar().setUnitIncrement(55);

                        // Change width of ScrollPane if it needed
                        if (gridContainer.getPreferredSize().getHeight() > gridContainer.getPreferredSize().getWidth()) {
                            scrollPane.setPreferredSize(new Dimension(
                                (int) gridContainer.getPreferredSize().getWidth() + 2 * scrollBarWidth,
                                (int) gridContainer.getPreferredSize().getWidth() * 2 / 3
                            ));
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
