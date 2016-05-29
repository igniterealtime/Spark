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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.gui.DefaultGuiManager;

import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkplugin.ui.components.JavaMixer;

/**
 *
 */
public class PhoneControl extends JPanel {

	private static final long serialVersionUID = 5275596365479885608L;
	private JLabel stateLabel = new JLabel(PhoneRes.getIString("phone.connected")+":");
    private JLabel callerIDLabel = new JLabel();
    private JLabel phoneNumberLabel = new JLabel();

    final private InterlocutorUI interlocutorUI;

    private RolloverButton chatButton;
    private JToggleButton muteButton;
    private RolloverButton transferButton;
    private RolloverButton endCallButton;

    private RolloverButton dialButton;

    private RolloverButton speakerButton;
    private RolloverButton micButton;

    private JavaMixer javaMixer = new JavaMixer(); 

    //private PhonePad phonePad = new PhonePad();

    public PhoneControl(final InterlocutorUI interlocutorUI, final DefaultGuiManager defaultGuiManager) {
        setLayout(new GridBagLayout());

        this.interlocutorUI = interlocutorUI;

        callerIDLabel.setText(interlocutorUI.getName());
        phoneNumberLabel.setText(interlocutorUI.getAddress());

        chatButton = new RolloverButton(PhoneRes.getImageIcon("CHAT_IMAGE"));
        muteButton = new JToggleButton(PhoneRes.getImageIcon("ON_HOLD_IMAGE"));
        transferButton = new RolloverButton(PhoneRes.getImageIcon("TRANSFER_IMAGE"));
        endCallButton = new RolloverButton(PhoneRes.getImageIcon("HANG_UP_PHONE_IMAGE"));
        dialButton = new RolloverButton(PhoneRes.getImageIcon("TELEPHONE_IMAGE"));
        speakerButton = new RolloverButton(PhoneRes.getImageIcon("VOLUME_IMAGE"));
        micButton = new RolloverButton(PhoneRes.getImageIcon("MICROPHONE_IMAGE"));

        add(stateLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
        add(callerIDLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
        add(dialButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

        add(phoneNumberLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

        // Add Button panel
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(chatButton);
        buttonPanel.add(muteButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(endCallButton);
        buttonPanel.add(micButton);
        buttonPanel.add(speakerButton);

        add(buttonPanel, new GridBagConstraints(0, 2, 3, 1, 1.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
        add(javaMixer.getPrefferedMasterVolume(), new GridBagConstraints(3, 0, 1, 3, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1, 1, 1, 1), 0, 0));
        add(javaMixer.getPrefferedInputVolume(), new GridBagConstraints(4, 0, 1, 3, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1, 1, 1, 1), 0, 0));

        dialButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
              //  phonePad.showDialpad(null, dialButton, mouseEvent);
            }
        });

        speakerButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                final JPopupMenu menu = new JPopupMenu();
                menu.add(javaMixer.getPrefferedMasterVolume());
                menu.show(speakerButton, mouseEvent.getX(), mouseEvent.getY());
            }
        });

        endCallButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                defaultGuiManager.hangup(interlocutorUI);
            }
        });

        muteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                defaultGuiManager.mute(interlocutorUI,!muteButton.isSelected());
            }
        });

        setOpaque(false);       
        buttonPanel.setOpaque(false);
    }

    public InterlocutorUI getInterlocutorUI() {
        return interlocutorUI;
    }

    public static void main(String args[]) {
        try {
            String classname = UIManager.getSystemLookAndFeelClassName();

            if (classname.indexOf("Windows") != -1) {
                UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
            } else if (classname.indexOf("mac") != -1 || classname.indexOf("apple") != -1) {
                UIManager.setLookAndFeel(classname);
            } else {
                UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.Plastic3DLookAndFeel());
            }

        }
        catch (Exception e) {
            Log.error(e);
        }

        final JFrame f = new JFrame();

        JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);
        //pane.addTab("Matt Tucker", PhoneRes.getImageIcon("ON_HOLD_IMAGE"), new PhoneControl("Matt Tucker", "503-972-6134"));
        //pane.addTab("Derek DeMoro", PhoneRes.getImageIcon("ON_PHONE_IMAGE"), new PhoneControl("Derek DeMoro", "503-972-6133"));
        //pane.addTab("Gato", PhoneRes.getImageIcon("ON_HOLD_IMAGE"), new PhoneControl("Gaston Dombiak", "503-976-LOVE"));

        f.getContentPane().add(pane);
        f.pack();
        f.setSize(400, 400);

        GraphicUtils.centerWindowOnScreen(f);
        f.setVisible(true);
    }


    public Dimension getPreferredSize() {
        final Dimension dim = super.getPreferredSize();
        dim.height = 75;
        return dim;
    }
}
