/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.ui;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.sparkplugin.ui.components.JavaMixer;
import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.gui.DefaultGuiManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class PhoneControl extends JPanel {

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

    private PhonePad phonePad = new PhonePad();

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
