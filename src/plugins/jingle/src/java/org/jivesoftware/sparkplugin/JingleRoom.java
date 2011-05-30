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
package org.jivesoftware.sparkplugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.media.JingleMediaManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.log.Log;


/**
 * The UI for calls with Roster members.
 *
 * @author Derek DeMoro
 */
public class JingleRoom extends JPanel {

	private static final long serialVersionUID = 2910998210426650565L;
	private JLabel connectedLabel;
    private String phoneNumber;
    private JLabel phoneLabel;
    private PreviousConversationPanel historyPanel;

    private boolean transmitting;

    private CallPanelButton muteButton;

    private RolloverButton hangUpButton;

    private static String CONNECTED = "Connected";

    protected final Color greenColor = new Color(91, 175, 41);
    protected final Color orangeColor = new Color(229, 139, 11);
    protected final Color blueColor = new Color(64, 103, 162);
    protected final Color redColor = new Color(211, 0, 0);

    private boolean callWasTransferred;

    private ChatRoom chatRoom;

    private JingleSession session;

    private JavaMixer mixer = new JavaMixer();

    public JingleRoom(JingleSession session, ChatRoom chatRoom) {
        this.session = session;
        this.chatRoom = chatRoom;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.lightGray));

        // Build Top Layer
        final JPanel topPanel = buildTopPanel();
        add(topPanel, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        // Build Control Panel
        final JPanel controlPanel = buildControlPanel();
        add(controlPanel, new GridBagConstraints(1, 6, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

        // Add Previous Conversation
        historyPanel = new PreviousConversationPanel();
        historyPanel.addPreviousConversations("");
        //  add(historyPanel, new GridBagConstraints(1, 8, 1, 1, 0.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 100));

        // Setup default settings
        setupDefaults();
    }


    /**
     * Builds the information block.
     *
     * @return the UI representing the Information Block.
     */
    private JPanel buildTopPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        // Add phone label
        phoneLabel = new JLabel();
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 13));
        phoneLabel.setForeground(new Color(64, 103, 162));
        panel.add(phoneLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));

        // Add Connected Label
        connectedLabel = new JLabel(CONNECTED);
        connectedLabel.setFont(new Font("Arial", Font.BOLD, 13));
        connectedLabel.setHorizontalTextPosition(JLabel.CENTER);
        connectedLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(connectedLabel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        return panel;
    }

    /**
     * Builds the Control Panel.
     *
     * @return the control panel.
     */
    private JPanel buildControlPanel() {
        // Add Control Panel
        final JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        // Add Volume Control
        try {
            final ControlPanel inputPanel = new ControlPanel(new GridBagLayout());
            Component inputComp = mixer.getPrefferedInputVolume();
            if (inputComp != null) {
                final JLabel inputIcon = new JLabel(JinglePhoneRes.getImageIcon("MICROPHONE_IMAGE"));
                inputPanel.add(inputComp, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));
                inputPanel.add(inputIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
                mainPanel.add(inputPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.2, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2, 1, 2, 1), 0, 50));
            }
        }
        catch (Exception e) {
            Log.error(e);
        }

        // Add master volume control.
        try {
            final ControlPanel outputPanel = new ControlPanel(new GridBagLayout());
            Component outputControl = mixer.getPrefferedMasterVolume();
            if (outputControl!=null) {
                final JLabel outputIcon = new JLabel(JinglePhoneRes.getImageIcon("SPEAKER_IMAGE"));
                outputPanel.add(outputControl, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));
                outputPanel.add(outputIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
                mainPanel.add(outputPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.2, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2, 1, 2, 1), 0, 50));
            }
        }
        catch (Exception e) {
            Log.error(e);
        }

        // Build ControlPanel List

        muteButton = new CallPanelButton(JinglePhoneRes.getImageIcon("MUTE_IMAGE").getImage(), "Mute");
        muteButton.setToolTipText(JingleResources.getString("label.mute.call"));

        // Add Components to Main Panel
        mainPanel.add(muteButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 1, 2, 1), 0, 0));

        // Add End Call button
        hangUpButton = new RolloverButton("     End Call", JinglePhoneRes.getImageIcon("HANG_UP_PHONE_77x24_IMAGE"));
        hangUpButton.setHorizontalTextPosition(JLabel.CENTER);
        hangUpButton.setFont(new Font("Dialog", Font.BOLD, 11));
        hangUpButton.setForeground(new Color(153, 32, 10));
        hangUpButton.setMargin(new Insets(0, 0, 0, 0));
        mainPanel.add(hangUpButton, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.8, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));


        return mainPanel;
    }


    public void setupDefaults() {
        muteButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                toggleMute();
            }

        });


        hangUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                hangUpButton.setEnabled(false);
                try {
                    session.terminate();
                }
                catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Called when a new call is established.
     */
    //TODO REMOVE
    @SuppressWarnings("unused")
    private void callStarted() {
        // Show History
        historyPanel.removeAll();
        historyPanel.addPreviousConversations(phoneNumber);

        hangUpButton.setEnabled(true);
        muteButton.setEnabled(true);
        setStatus(CONNECTED, false);

        // Add notification to ChatRoom if one exists.
        if (chatRoom != null) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(new Date());

            chatRoom.getTranscriptWindow().insertNotificationMessage("Call started at " + time, ChatManager.NOTIFICATION_COLOR);
        }
    }

    /**
     * Called when the call is ended. This does basic container cleanup.
     */
    public void callEnded() {
        if (!callWasTransferred) {
            historyPanel.callEnded();
            setStatus("Call Ended", redColor);
        }

        hangUpButton.setEnabled(false);
        hangUpButton.setOpaque(false);

        muteButton.setEnabled(false);
        muteButton.setOpaque(false);

        setStatus("Call Ended", redColor);

        // Add notification to ChatRoom if one exists.
        if (chatRoom != null) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(new Date());

            chatRoom.getTranscriptWindow().insertNotificationMessage("Call ended at " + time, ChatManager.NOTIFICATION_COLOR);
        }
    }

    private void setStatus(String status, boolean alert) {
        if (alert) {
            connectedLabel.setForeground(orangeColor);
        } else {
            connectedLabel.setForeground(greenColor);
        }
        connectedLabel.setText(status);
    }

    private void setStatus(String status, Color color) {
        connectedLabel.setForeground(color);
        connectedLabel.setText(status);
    }


    private void toggleMute() {
        if (transmitting) {
            transmitting = false;
            muteButton.setToolTipText(JingleResources.getString("label.mute"));
            muteButton.setButtonSelected(false);
            setStatus(CONNECTED, false);

            // Change state
            JingleStateManager.getInstance().addJingleSession(chatRoom, JingleStateManager.JingleRoomState.inJingleCall);
        } else {
            transmitting = true;
            muteButton.setToolTipText(JingleResources.getString("label.unmute"));
            muteButton.setButtonSelected(true);
            setStatus("Muted", true);

            // Change state
            JingleStateManager.getInstance().addJingleSession(chatRoom, JingleStateManager.JingleRoomState.muted);
        }

        for (JingleMediaManager mediaManager : session.getMediaManagers()) {
            session.getMediaSession(mediaManager.getName()).setTrasmit(!transmitting);
        }

        muteButton.invalidate();
        muteButton.validate();
        muteButton.repaint();

        // Notify state change
        SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
    }


    public void actionPerformed(ActionEvent e) {

    }


    public void paintComponent(Graphics g) {
        BufferedImage cache = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = cache.createGraphics();

        GradientPaint paint = new GradientPaint(0, 0, new Color(241, 245, 250), 0, getHeight(), new Color(244, 250, 255), true);

        g2d.setPaint(paint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

        g.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
    }


    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.width = 200;
        return dim;
    }

    public SparkTab getSparkTab() {
        int index = SparkManager.getChatManager().getChatContainer().indexOfComponent(chatRoom);
        return SparkManager.getChatManager().getChatContainer().getTabAt(index);
    }


}