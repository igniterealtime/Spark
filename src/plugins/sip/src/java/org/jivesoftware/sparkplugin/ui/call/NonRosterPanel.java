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
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.java.sipmack.sip.Call;
import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.SoftPhoneManager.CallRoomState;

import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkplugin.components.CallPanelButton;
import org.jivesoftware.sparkplugin.components.EndCallButton;
import org.jivesoftware.sparkplugin.components.RedialButton;
import org.jivesoftware.sparkplugin.ui.PhonePad;
import org.jivesoftware.sparkplugin.ui.components.ControlPanel;
import org.jivesoftware.sparkplugin.ui.components.JavaMixer;
import org.jivesoftware.sparkplugin.ui.transfer.TransferManager;


/**
 * The UI to represent a phone call.
 *
 * @author Derek DeMoro
 */
public class NonRosterPanel extends PhonePanel {

	private static final long	serialVersionUID	= -1826003278845440442L;
	private JLabel connectedLabel;
    private String phoneNumber;

    private PreviousConversationPanel historyPanel;

    private boolean onHold;
    private boolean muted;

    private CallPanelButton muteButton;
    private CallPanelButton holdButton;
    private CallPanelButton transferButton;

    private EndCallButton hangUpButton;
    private RedialButton redialButton;

    private SoftPhoneManager softPhone;

    private String CONNECTED = PhoneRes.getIString("phone.connected");

    private InterlocutorUI activeCall;

    private CallManager callManager;

    private boolean callWasTransferred;

    private boolean uiBuilt;

    private JavaMixer mixer = new JavaMixer();

    public NonRosterPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.lightGray));

        callManager = CallManager.getInstance();

        // Initilize mixer.
        softPhone = SoftPhoneManager.getInstance();
    }

    public JPanel buildTopPanel() {
        final JLabel avatarLabel = new JLabel(PhoneRes.getImageIcon("LARGE_PHONE_ICON"));

        final JLabel nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.BOLD, 19));
        nameLabel.setForeground(new Color(64, 103, 162));
        String remoteName = getActiveCall().getCall().getRemoteName();
        nameLabel.setText(remoteName);

        final JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new GridBagLayout());

        // Add Connected Label
        connectedLabel = new JLabel(CONNECTED);
        connectedLabel.setFont(new Font("Arial", Font.BOLD, 16));
        connectedLabel.setForeground(greenColor);

        // Add All Items to Top Panel
        topPanel.add(avatarLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        topPanel.add(nameLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        topPanel.add(connectedLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));

        return topPanel;
    }

    /**
     * Builds the Control Panel.
     *
     * @return the control panel.
     */
    private JPanel buildMiddlePanel() {
        // Add Control Panel
        final JPanel mainPanel = new JPanel(new GridBagLayout()) {

			private static final long	serialVersionUID	= 1571929852761037052L;

				public Dimension getPreferredSize() {
                final Dimension dim = super.getPreferredSize();
                dim.height = 100;
                return dim;
            }
        };

        mainPanel.setOpaque(false);

        // Add Input Volume To Control Panel
        try {
            final ControlPanel inputPanel = new ControlPanel(new GridBagLayout());

            final JLabel inputIcon = new JLabel(PhoneRes.getImageIcon("MICROPHONE_IMAGE"));
            inputPanel.add(mixer.getPrefferedInputVolume(), new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));
            inputPanel.add(inputIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

            mainPanel.add(inputPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 1, 2, 1), 0, 0));
        }
        catch (Exception e) {
            Log.error(e);
        }

        try {
            // Add Output Volume To Control Panel
            final ControlPanel outputPanel = new ControlPanel(new GridBagLayout());

            final JLabel outputIcon = new JLabel(PhoneRes.getImageIcon("SPEAKER_IMAGE"));
            Component component = mixer.getPrefferedMasterVolume();
            if (component != null)
            	outputPanel.add(component, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));
            outputPanel.add(outputIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
            mainPanel.add(outputPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 1, 2, 5), 0, 0));
        }
        catch (Exception e) {
            Log.error(e);
        }

        // Build ControlPanel List
        final JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setOpaque(false);
        muteButton = new CallPanelButton(PhoneRes.getImageIcon("MUTE_IMAGE").getImage(), PhoneRes.getIString("phone.mute"));
        muteButton.setToolTipText(PhoneRes.getIString("phone.tips.mute"));
        controlPanel.add(muteButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

        holdButton = new CallPanelButton(PhoneRes.getImageIcon("ON_HOLD_IMAGE").getImage(), PhoneRes.getIString("phone.hold"));
        holdButton.setToolTipText(PhoneRes.getIString("phone.tips.hold"));
        controlPanel.add(holdButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

        transferButton = new CallPanelButton(PhoneRes.getImageIcon("TRANSFER_IMAGE").getImage(), PhoneRes.getIString("phone.transfer"));
        transferButton.setToolTipText(PhoneRes.getIString("phone.tips.transfer"));
        controlPanel.add(transferButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

        // Add End Call button
        hangUpButton = new EndCallButton();
        redialButton = new RedialButton();
        redialButton.setVisible(false);

        // Add Components to Main Panel
        mainPanel.add(controlPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 1, 2, 1), 0, 0));
        mainPanel.add(hangUpButton, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 1, 2, 1), 0, 0));
        mainPanel.add(redialButton, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 1, 2, 1), 0, 0));

        return mainPanel;
    }


    public void setInterlocutorUI(final InterlocutorUI interlocutorUI) {
        this.activeCall = interlocutorUI;

        // Set defaults
        muted = false;
        onHold = false;

        this.phoneNumber = interlocutorUI.getCall().getNumber();

        if (!uiBuilt) {
            buildDefaultUI();
        }

        callStarted();
    }

    private void buildDefaultUI() {
        // Add Top Panel
        final JPanel topPanel = buildTopPanel();
        add(topPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // Build Control Panel
        final JPanel middlePanel = buildMiddlePanel();
        add(middlePanel, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Build Bottom Panel
        final PhonePad phonePad = new PhonePad();
        historyPanel = new PreviousConversationPanel();

        add(phonePad, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.9, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(historyPanel, new GridBagConstraints(1, 8, 1, 1, 1.0, 0.9, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Setup default settings
        setupDefaults();

        uiBuilt = true;
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
                    setStatus(PhoneRes.getIString("phone.transferring")+"...", blueColor);
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
                            setStatus(PhoneRes.getIString("phone.transfered"), blueColor);
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
                muteButton.setButtonSelected(false);
                holdButton.setButtonSelected(false);
            }
        });

        redialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                manager.getDefaultGuiManager().dial(activeCall.getCall().getNumber());
                redialButton.setVisible(false);
                hangUpButton.setVisible(true);
            }
        });
    }


    /**
     * Called when a new call is established.
     */
    private void callStarted() {
        redialButton.setVisible(false);
        hangUpButton.setVisible(true);
        hangUpButton.setEnabled(true);
        muteButton.setEnabled(true);
        holdButton.setEnabled(true);
        transferButton.setEnabled(true);
        setStatus(CONNECTED, false);

        // Show History
        historyPanel.removeAll();
        historyPanel.addPreviousConversations(phoneNumber);

        // Add notification to ChatRoom if one exists.
        final ChatRoom chatRoom = callManager.getAssociatedChatRoom(this);
        if (chatRoom != null) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(new Date());

            chatRoom.getTranscriptWindow().insertNotificationMessage(PhoneRes.getIString("phone.callstartedat")+ " " + time, ChatManager.NOTIFICATION_COLOR);
        }
    }

    /**
     * Called when the call is ended. This does basic container cleanup.
     */
    public void callEnded() {
        if (!callWasTransferred) {
            historyPanel.callEnded();
            setStatus(PhoneRes.getIString("phone.callended"), new Color(211, 0, 0));
        }

        hangUpButton.setVisible(false);
        redialButton.setVisible(true);
        muteButton.setEnabled(false);
        holdButton.setEnabled(false);
        transferButton.setEnabled(false);

        // Add notification to ChatRoom if one exists.
        final ChatRoom chatRoom = callManager.getAssociatedChatRoom(this);
        if (chatRoom != null) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(new Date());

            chatRoom.getTranscriptWindow().insertNotificationMessage(PhoneRes.getIString("phone.callendedat")+" " + time, ChatManager.NOTIFICATION_COLOR);
        }


        changeState(CallRoomState.callWasEnded);
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
            muteButton.setToolTipText(PhoneRes.getIString("phone.mute"));
            muteButton.setButtonSelected(false);
            setStatus(CONNECTED, false);

            // Change the state.
            changeState(CallRoomState.inCall);
        }
        else {
            muted = true;
            muteButton.setToolTipText(PhoneRes.getIString("phone.unmute"));
            muteButton.setButtonSelected(true);
            setStatus("Muted", true);

            // Change the state.
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
            holdButton.setToolTipText(PhoneRes.getIString("phone.hold"));
            holdButton.setButtonSelected(false);
            setStatus(CONNECTED, false);

            // Change the state.
            changeState(CallRoomState.inCall);
        }
        else {
            onHold = true;
            holdButton.setButtonSelected(true);
            holdButton.setToolTipText("Unhold");
            setStatus(PhoneRes.getIString("phone.onhold"), true);

            // Change the state.
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
        if (activeCall.getCallState() == Call.CONNECTED) {
            return PhoneRes.getIString("phone.onphonewith")+" " + phoneNumber;
        }
        else {
            return PhoneRes.getIString("phone.callendedwith")+" " + phoneNumber;
        }

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

    private void changeState(CallRoomState state) {
        softPhone.addCallSession(this, state);
        SparkManager.getChatManager().notifySparkTabHandlers(this);
    }

}
