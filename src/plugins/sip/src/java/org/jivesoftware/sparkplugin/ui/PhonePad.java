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
package org.jivesoftware.sparkplugin.ui;

import org.jivesoftware.sparkplugin.components.DialButton;
import net.java.sipmack.common.DialSoundManager;
import net.java.sipmack.softphone.SoftPhoneManager;
import org.jivesoftware.spark.component.BackgroundPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

/**
 *
 */
public class PhonePad extends BackgroundPanel {
	private static final long serialVersionUID = 1059979750263197335L;

	private TelephoneTextField callField;

    final List<DialButton> list = new ArrayList<DialButton>();

    private JPopupMenu menu;

    private DialSoundManager dialSoundManager;
    
    public PhonePad() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        // Create First Row
        DialButton oneButton = new DialButton("", new DigitAction("1"));
        DialButton twoButton = new DialButton("ABC", new DigitAction("2"));
        DialButton threeButton = new DialButton("DEF", new DigitAction("3"));

        add(oneButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(twoButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(threeButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        // Create Second Row
        DialButton fourButton = new DialButton("GHI", new DigitAction("4"));
        DialButton fiveButton = new DialButton("JKL", new DigitAction("5"));
        DialButton sixButton = new DialButton("MNO", new DigitAction("6"));

        add(fourButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(fiveButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(sixButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        // Create Third Row
        DialButton sevenButton = new DialButton("PQRS", new DigitAction("7"));
        DialButton eightButton = new DialButton("TUV", new DigitAction("8"));
        DialButton nineButton = new DialButton("WXYZ", new DigitAction("9"));

        add(sevenButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(eightButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(nineButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        // Create fourth Row
        DialButton asterisksButton = new DialButton("", new DigitAction("*"));
        DialButton zeroButton = new DialButton("", new DigitAction("0"));
        DialButton poundButton = new DialButton("", new DigitAction("#"));

        add(asterisksButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(zeroButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(poundButton, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


        list.add(zeroButton);
        list.add(oneButton);
        list.add(twoButton);
        list.add(threeButton);
        list.add(fourButton);
        list.add(fiveButton);
        list.add(sixButton);
        list.add(sevenButton);
        list.add(eightButton);
        list.add(nineButton);
        list.add(asterisksButton);
        list.add(poundButton);


        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230)));


        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    menu.setVisible(false);
                }
                else {
                    numberEntered(keyEvent.getKeyChar());
                }
            }
        });

        this.dialSoundManager = SoftPhoneManager.getInstance().getDTMFSounds();
    }

    public String getNumber() {
        return callField.getText();
    }

    public void setNumber(String number) {
        callField.setText(number);
    }

    public void numberEntered(char ch) {
        String number = String.valueOf(ch);

        for (DialButton button : list) {
            String name = button.getNumber();
            if (name.equals(number)) {
              // TH: trying to improve responsiveness of the DTMF playback.
               button.setBlock(true);
              dialSoundManager.enqueue(name);
               button.doClick();
               button.setBlock(false);
            }
        }
    }

    /**
     * Private Class that handles the Digit Button actions
     */
    private class DigitAction extends AbstractAction {
		private static final long serialVersionUID = -6295794038544034901L;

		public DigitAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            final String number = getValue(Action.NAME).toString();

            // TH: trying to improve responsiveness of DTMF playback.
            dialSoundManager.enqueue(number);

            if (callField != null) {
                callField.appendNumber(number);
            }

            SoftPhoneManager.getInstance().getDefaultGuiManager().sendDTMF(number);
        }
    }


    public void showDialpad(TelephoneTextField callField) {
        menu = new JPopupMenu();
        menu.setFocusable(false);
        menu.add(this);
        menu.pack();

        this.callField = callField;
        menu.show(callField, 0, callField.getHeight());
    }

    public void showDialpad(Component comp, boolean rightAligned) {
        menu = new JPopupMenu();
        menu.setFocusable(false);
        menu.add(this);
        menu.pack();

        if (rightAligned) {
            int width = (int)menu.getPreferredSize().getWidth();
            menu.show(comp, -width + comp.getWidth(), comp.getHeight());
        }
        else {
            menu.show(comp, 0, comp.getHeight());
        }

        this.requestFocus();
    }

    public void hide(){
        if(menu != null){
            menu.setVisible(false);
        }
    }


}
