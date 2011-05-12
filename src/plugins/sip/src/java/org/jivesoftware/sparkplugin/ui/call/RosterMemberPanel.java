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

package org.jivesoftware.sparkplugin.ui.call;

import java.awt.Color;
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
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.SoftPhoneManager.CallRoomState;

import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkplugin.components.RosterMemberCallButton;
import org.jivesoftware.sparkplugin.ui.PhonePad;
import org.jivesoftware.sparkplugin.ui.components.ControlPanel;
import org.jivesoftware.sparkplugin.ui.components.JavaMixer;
import org.jivesoftware.sparkplugin.ui.transfer.TransferManager;

/**
 * The UI for calls with Roster members.
 *
 * @author Derek DeMoro
 */
public class RosterMemberPanel extends PhonePanel {
	private static final long	serialVersionUID	= -327742794852188962L;
	private JLabel connectedLabel;
    private String phoneNumber;
    private JLabel phoneLabel;
    private PreviousConversationPanel historyPanel;

    private boolean onHold;
    private boolean muted;

    private RosterMemberCallButton muteButton;
    private RosterMemberCallButton holdButton;
    private RosterMemberCallButton transferButton;

    private RolloverButton hangUpButton;

    private SoftPhoneManager softPhone;

    private static String CONNECTED = PhoneRes.getIString("phone.connected");

    private InterlocutorUI activeCall;

    private CallManager callManager;

    private final Color greenColor = new Color(91, 175, 41);
    private final Color orangeColor = new Color(229, 139, 11);

    private boolean callWasTransferred;

    private JavaMixer javaMixer = new JavaMixer();

    public RosterMemberPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.lightGray));

        callManager = CallManager.getInstance();

        // Initilize mixer.
        softPhone = SoftPhoneManager.getInstance();

        // Build Top Layer
        final JPanel topPanel = buildTopPanel();
        add(topPanel, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        // Build Control Panel
        final JPanel controlPanel = buildControlPanel();
        add(controlPanel, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        // Add Previous Conversation
        historyPanel = new PreviousConversationPanel();
        add(historyPanel, new GridBagConstraints(1, 8, 1, 1, 0.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 100));

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

        // Add Dial Pad
        final RolloverButton dialPadButton = new RolloverButton(PhoneRes.getImageIcon("ICON_NUMBERPAD_IMAGE"));
        panel.add(dialPadButton, new GridBagConstraints(1, 0, 1, 3, 1.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));

        final PhonePad pad = new PhonePad();
        dialPadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                pad.showDialpad(dialPadButton, true);
            }
        });

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

        // Initialize Mixer.

        // Add Input Volume To Control Panel
        try {
            final ControlPanel inputPanel = new ControlPanel(new GridBagLayout());

            final JLabel inputIcon = new JLabel(PhoneRes.getImageIcon("SPEAKER_IMAGE"));
            inputPanel.add(javaMixer.getPrefferedMasterVolume(), new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));
            inputPanel.add(inputIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
            mainPanel.add(inputPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.2, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2, 1, 2, 1), 0, 0));
        }
        catch (Exception e) {
            Log.error(e);
        }

        // Add Output Volume To Control Panel
        try {
            final ControlPanel outputPanel = new ControlPanel(new GridBagLayout());

            final JLabel outputIcon = new JLabel(PhoneRes.getImageIcon("MICROPHONE_IMAGE"));
            outputPanel.add(javaMixer.getPrefferedInputVolume(), new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));
            outputPanel.add(outputIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
            mainPanel.add(outputPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.2, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2, 1, 2, 1), 0, 0));
        }
        catch (Exception e) {
            Log.error(e);
        }

        // Build ControlPanel List
        final ControlPanel controlPanel = new ControlPanel(new GridBagLayout());
        final JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setBackground(new Color(219, 228, 238));

        muteButton = new RosterMemberCallButton(PhoneRes.getImageIcon("MUTE_IMAGE").getImage(), PhoneRes.getIString("phone.mute"));
        muteButton.setToolTipText(PhoneRes.getIString("phone.tips.mute"));
        controlPanel.add(muteButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        controlPanel.add(sep, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        holdButton = new RosterMemberCallButton(PhoneRes.getImageIcon("ON_HOLD_IMAGE").getImage(), PhoneRes.getIString("phone.hold"));
        holdButton.setToolTipText(PhoneRes.getIString("phone.tips.hold"));
        controlPanel.add(holdButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));


        final JSeparator sep2 = new JSeparator(JSeparator.HORIZONTAL);
        sep2.setBackground(new Color(219, 228, 238));
        controlPanel.add(sep2, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        transferButton = new RosterMemberCallButton(PhoneRes.getImageIcon("TRANSFER_IMAGE").getImage(), PhoneRes.getIString("phone.transfer"));
        transferButton.setToolTipText(PhoneRes.getIString("phone.tips.transfer"));
        controlPanel.add(transferButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // Add Components to Main Panel
        mainPanel.add(controlPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 1, 2, 1), 0, 0));

        // Add End Call button
        hangUpButton = new RolloverButton("     "+PhoneRes.getIString("phone.hangup"), PhoneRes.getImageIcon("HANG_UP_PHONE_77x24_IMAGE"));
        hangUpButton.setHorizontalTextPosition(JLabel.CENTER);
        hangUpButton.setFont(new Font("Dialog", Font.BOLD, 11));
        hangUpButton.setForeground(new Color(153, 32, 10));
        hangUpButton.setMargin(new Insets(0, 0, 0, 0));
        mainPanel.add(hangUpButton, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.8, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));


        return mainPanel;
    }


    public void setInterlocutorUI(final InterlocutorUI interlocutorUI) {
        this.activeCall = interlocutorUI;

        // Set defaults
        muted = false;
        onHold = false;

        this.phoneNumber = interlocutorUI.getCall().getNumber();

        phoneLabel.setText(phoneNumber);

        callStarted();
    }

    public void setupDefaults() {
        holdButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                toggleHold();
            }
        });

        muteButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                toggleMute();
            }

        });

        transferButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                TransferManager ui = new TransferManager();
                final String number = ui.getNumber(SparkManager.getChatManager().getChatContainer().getChatFrame());
                if (ModelUtil.hasLength(number)) {
                    setStatus("Transferring...", blueColor);
                    historyPanel.transferring();
                    SwingWorker transferringThread = new SwingWorker() {
                        public Object construct() {
                            try {
                                Thread.sleep(2000);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }

                        public void finished() {
                            setStatus("Transferred", blueColor);
                            historyPanel.transfer(number);
                            callWasTransferred = true;
                            softPhone.handleTransfer(getActiveCall().getID(), number);
                            callEnded();
                        }

                    };
                    transferringThread.start();

                }
            }
        });

        final SoftPhoneManager manager = SoftPhoneManager.getInstance();
        hangUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                manager.getDefaultGuiManager().hangup(activeCall);
                hangUpButton.setEnabled(false);
            }
        });
    }


    /**
     * Called when a new call is established.
     */
    private void callStarted() {
        // Show History
        historyPanel.removeAll();
        historyPanel.addPreviousConversations(phoneNumber);

        hangUpButton.setEnabled(true);
        muteButton.setEnabled(true);
        holdButton.setEnabled(true);
        transferButton.setEnabled(true);
        setStatus(CONNECTED, false);

        // Add notification to ChatRoom if one exists.
        final ChatRoom chatRoom = callManager.getAssociatedChatRoom(this);
        if (chatRoom != null) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(new Date());

            chatRoom.getTranscriptWindow().insertNotificationMessage(PhoneRes.getIString("phone.callstartedat")+" " + time, ChatManager.NOTIFICATION_COLOR);
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

        holdButton.setEnabled(false);
        holdButton.setOpaque(false);

        transferButton.setEnabled(false);
        setStatus("Call Ended", redColor);

        // Add notification to ChatRoom if one exists.
        final ChatRoom chatRoom = callManager.getAssociatedChatRoom(this);
        if (chatRoom != null) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(new Date());

            chatRoom.getTranscriptWindow().insertNotificationMessage(PhoneRes.getIString("phone.callendedat")+" " + time, ChatManager.NOTIFICATION_COLOR);
        }

        // If this is a standalone phone call with no associated ChatRoom
        // gray out title and show off-phone icon.
        final ChatRoom room = callManager.getAssociatedChatRoom(this);

        softPhone.addCallSession(room, SoftPhoneManager.CallRoomState.callWasEnded);

        // Notify
        SparkManager.getChatManager().notifySparkTabHandlers(room);
    }

    private void setStatus(String status, boolean alert) {
        if (alert) {
            connectedLabel.setForeground(orangeColor);
        }
        else {
            connectedLabel.setForeground(greenColor);
        }
        connectedLabel.setText(status);
    }

    private void setStatus(String status, Color color) {
        connectedLabel.setForeground(color);
        connectedLabel.setText(status);
    }


    private void toggleMute() {
        if (onHold) {
            toggleHold();
        }

        if (muted) {
            muted = false;
            muteButton.setToolTipText("Mute");
            muteButton.setButtonSelected(false);
            setStatus(CONNECTED, false);

            // Change the current state.
            changeState(CallRoomState.inCall);
        }
        else {
            muted = true;
            muteButton.setToolTipText("Unmute");
            muteButton.setButtonSelected(true);
            setStatus("Muted", true);

            // Change the current state
            changeState(CallRoomState.muted);
        }

        muteButton.invalidate();
        muteButton.validate();
        muteButton.repaint();
        softPhone.getDefaultGuiManager().mute(activeCall, !muted);
    }


    private void toggleHold() {
        if (muted) {
            toggleMute();
        }

        if (onHold) {
            onHold = false;
            holdButton.setToolTipText("Hold");
            holdButton.setButtonSelected(false);
            setStatus(CONNECTED, false);

            // Change the current state
            changeState(CallRoomState.inCall);
        }
        else {
            onHold = true;
            holdButton.setToolTipText("Unhold");
            holdButton.setButtonSelected(true);
            setStatus("On Hold", true);

            // Change the current state
            changeState(CallRoomState.onHold);
        }

        softPhone.getDefaultGuiManager().hold(activeCall);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public String getTabTitle() {
        return phoneNumber;
    }

    public String getFrameTitle() {
        return PhoneRes.getIString("phone.onphonewith")+" " + phoneNumber;
    }

    public ImageIcon getTabIcon() {
        return PhoneRes.getImageIcon("RECEIVER2_IMAGE");
    }

    public JComponent getGUI() {
        return this;
    }

    public String getToolTipDescription() {
        return phoneNumber;
    }

    public boolean closing() {
        return true;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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
        dim.width = 0;
        return dim;
    }

    public InterlocutorUI getActiveCall() {
        return activeCall;
    }

    private void changeState(SoftPhoneManager.CallRoomState state) {
        final ChatRoom room = callManager.getAssociatedChatRoom(this);
        softPhone.addCallSession(room, state);
        SparkManager.getChatManager().notifySparkTabHandlers(room);
    }
}
