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
package org.jivesoftware.spark.ui;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.spark.ChatAreaSendField;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * The base implementation of all ChatRoom conversations. You would implement this class to have most types of Chat.
 */
public abstract class ChatRoom extends BackgroundPanel implements ActionListener, StanzaListener, DocumentListener, ConnectionListener, FocusListener, ContextMenuListener, ChatFrameToFrontListener {
	private static final long serialVersionUID = 7981019929515888299L;
	private final JPanel chatPanel;
    private final JSplitPane splitPane;
    private JSplitPane verticalSplit;

    private final JLabel notificationLabel;
    private final TranscriptWindow transcriptWindow;
    private final ChatAreaSendField chatAreaButton;
    private final ChatToolBar toolbar;
    private final JScrollPane textScroller;
    private final JPanel bottomPanel;

    private final JPanel editorWrapperBar;
    private final JPanel editorBarRight;
    private final JPanel editorBarLeft;
    private JPanel chatWindowPanel;

    private int unreadMessageCount;

    private boolean mousePressed;

    private List<ChatRoomClosingListener> closingListeners = new ArrayList<>();


    private ChatRoomTransferHandler transferHandler;

    private final List<String> packetIDList;
    private final List<MessageListener> messageListeners;
    private List<Message> transcript;
    private List<FileDropListener> fileDropListeners;

    private MouseAdapter transcriptWindowMouseListener;

    private KeyAdapter chatEditorKeyListener;
    private ChatFrame _chatFrame;
    private RolloverButton _alwaysOnTopItem;
    private boolean _isAlwaysOnTopActive;

    // Chat state
    private TimerTask typingTimerTask;
    private long lastNotificationSentTime;
    private ChatState lastNotificationSent;
    private long pauseTimePeriod = 2000;
    private long inactiveTimePeriod = 120000;

    /**
     * Initializes the base layout and base background color.
     */
    protected ChatRoom() {
        chatPanel = new JPanel(new GridBagLayout());
        transcriptWindow = UIComponentRegistry.createTranscriptWindow();
        splitPane = new JSplitPane();
        packetIDList = new ArrayList<>();
        notificationLabel = new JLabel();
        toolbar = new ChatToolBar();
        bottomPanel = new JPanel();

        messageListeners = new ArrayList<>();
        transcript = new ArrayList<>();

        editorWrapperBar = new JPanel(new BorderLayout());
        editorBarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
        editorBarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 1));
        editorWrapperBar.add(editorBarLeft, BorderLayout.WEST);
        editorWrapperBar.add(editorBarRight, BorderLayout.EAST);
        fileDropListeners = new ArrayList<>();

        transcriptWindowMouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

        	if(e.getClickCount()!=2){
                getChatInputEditor().requestFocus();
        	}
            }

            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                if (transcriptWindow.getSelectedText() == null) {
                    getChatInputEditor().requestFocus();
                }
            }

            public void mousePressed(MouseEvent e) {
                mousePressed = true;
            }
        };

        transcriptWindow.addMouseListener(transcriptWindowMouseListener);

        chatAreaButton = new ChatAreaSendField(Res.getString("button.send"));
        textScroller = new JScrollPane(transcriptWindow);
        textScroller.setBackground(transcriptWindow.getBackground());
        textScroller.getViewport().setBackground(Color.white);
        transcriptWindow.setBackground(Color.white);

        getChatInputEditor().setSelectedTextColor((Color)UIManager.get("ChatInput.SelectedTextColor"));
        getChatInputEditor().setSelectionColor((Color)UIManager.get("ChatInput.SelectionColor"));

        setLayout(new GridBagLayout());

        // Remove Default Beveled Borders
        splitPane.setBorder(null);
        splitPane.setOneTouchExpandable(false);

        // Add Vertical Split Pane
        verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(verticalSplit, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        verticalSplit.setBorder(null);
        verticalSplit.setOneTouchExpandable(false);

        verticalSplit.setTopComponent(splitPane);

        textScroller.setAutoscrolls(true);

        // For the first 5*150ms we wait for transcript to load and move
        // scrollpane to max postion if size of scrollpane changed
        textScroller.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            private boolean scrollAtStart = false;

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {

                if (!scrollAtStart) {
                    scrollAtStart = true;
                    SwingWorker thread = new SwingWorker() {

                        @Override
                        public Object construct() {
                            int start = textScroller.getVerticalScrollBar().getMaximum();
                            int second;
                            int i = 0;
                            do {
                                try {

                                    Thread.sleep(150);
                                    second = textScroller.getVerticalScrollBar().getMaximum();
                                    if (start == second) {
                                        ++i;
                                    } else {
                                        scrollToBottom();
                                        getTranscriptWindow().repaint();
                                    }
                                    start = second;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } while (i < 5);
                            return null;
                        }
                    };
                    thread.start();
                }
            }
        });


        // Speed up scrolling. It was way too slow.
        textScroller.getVerticalScrollBar().setBlockIncrement(200);
        textScroller.getVerticalScrollBar().setUnitIncrement(20);

        chatWindowPanel = new JPanel();
        chatWindowPanel.setLayout(new GridBagLayout());
        chatWindowPanel.add(textScroller, new GridBagConstraints(0, 10, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        chatWindowPanel.setOpaque(false);

        // Layout Components
        chatPanel.add(chatWindowPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, getChatPanelInsets(), 0, 0));

        // Add Chat Panel to Split Pane
        splitPane.setLeftComponent(chatPanel);

        // Add edit buttons to Chat Room
        editorBarLeft.setOpaque(false);
        chatPanel.setOpaque(false);


        bottomPanel.setOpaque(false);
        splitPane.setOpaque(false);
        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.add(chatAreaButton, new GridBagConstraints(0, 1, 5, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, getChatAreaInsets(), 0, 35));
        bottomPanel.add(editorWrapperBar, new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, getEditorWrapperInsets(), 0, 0));

        // Set bottom panel border
        bottomPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(197, 213, 230)));
        verticalSplit.setOpaque(false);

        verticalSplit.setBottomComponent(bottomPanel);
        verticalSplit.setResizeWeight(1.0);
        verticalSplit.setDividerSize(2);

        // Add listener to send button
        chatAreaButton.getButton().addActionListener(this);

        // Add Key Listener to Send Field
        getChatInputEditor().getDocument().addDocumentListener(this);

        // Add Key Listener to Send Field
        chatEditorKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                checkForEnter(e);
            }
        };

        getChatInputEditor().addKeyListener(chatEditorKeyListener);

        getChatInputEditor().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl F4"), "closeTheRoom");
        getChatInputEditor().getActionMap().put("closeTheRoom", new AbstractAction("closeTheRoom") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent evt) {
                // Leave this chat.
                closeChatRoom();
            }
        });

        getChatInputEditor().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl SPACE"), "handleCompletion");
        getChatInputEditor().getActionMap().put("handleCompletion", new AbstractAction("handleCompletion") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent evt) {
                // handle name completion.
                try {
                    handleNickNameCompletion();
                } catch (ChatRoomNotFoundException e) {
                    Log.error("ctlr-space nickname find", e);
                }
            }
        });

        _isAlwaysOnTopActive = SettingsManager.getLocalPreferences().isChatWindowAlwaysOnTop();
        _alwaysOnTopItem = UIComponentRegistry.getButtonFactory().createAlwaysOnTop(_isAlwaysOnTopActive);

        _alwaysOnTopItem.addActionListener( actionEvent -> {
            if (!_isAlwaysOnTopActive)
            {
                SettingsManager.getLocalPreferences().setChatWindowAlwaysOnTop(true);
                _chatFrame.setWindowAlwaysOnTop(true);
                _isAlwaysOnTopActive = true;
                _alwaysOnTopItem.setIcon(SparkRes.getImageIcon("FRAME_ALWAYS_ON_TOP_ACTIVE"));

            }
            else
            {
                SettingsManager.getLocalPreferences().setChatWindowAlwaysOnTop(false);
                _chatFrame.setWindowAlwaysOnTop(false);
                _isAlwaysOnTopActive = false;
                _alwaysOnTopItem.setIcon(SparkRes.getImageIcon("FRAME_ALWAYS_ON_TOP_DEACTIVE"));
            }
        } );

        editorBarRight.add(_alwaysOnTopItem);

        // Initially, set the right pane to null to keep it empty.
        getSplitPane().setRightComponent(null);

        notificationLabel.setIcon(SparkRes.getImageIcon(SparkRes.BLANK_IMAGE));


        getTranscriptWindow().addContextMenuListener(this);

        transferHandler = new ChatRoomTransferHandler(this);

        getTranscriptWindow().setTransferHandler(transferHandler);
        getChatInputEditor().setTransferHandler(transferHandler);

        addToolbar();

        // Add Connection Listener
        SparkManager.getConnection().addConnectionListener(this);

        // Add Focus Listener
        addFocusListener(this);

        setChatState( ChatState.active );
        createChatStateTimerTask();

        scrollToBottom();
    }

    protected void createChatStateTimerTask() {
        typingTimerTask = new TimerTask() {
            public void run() {
                final long lastUpdate = System.currentTimeMillis() - lastNotificationSentTime;
                switch ( lastNotificationSent ) {
                    case paused:
                    case active:
                        if ( lastUpdate > inactiveTimePeriod ) {
                            setChatState( ChatState.inactive );
                        }
                        break;

                    case composing:
                        if ( lastUpdate > pauseTimePeriod ) {
                            setChatState( ChatState.paused );
                        }
                        break;
                }
            }
        };
        TaskEngine.getInstance().scheduleAtFixedRate(typingTimerTask, pauseTimePeriod /2, pauseTimePeriod / 2);
    }

    /**
     * Sends a chat state to all peers.
     *
     * @param state the chat state.
     */
    protected abstract void sendChatState( ChatState state ) throws SmackException.NotConnectedException;

    /**
     * Sets the chat state, causing an update to be sent to all peers if the new state warrants an update.
     *
     * @param state the chat state (never null).
     */
    public final void setChatState(ChatState state)
    {
        if ( state == null ) {
            throw new IllegalArgumentException( "Argument 'state' cannot be null." );
        }

        // Only sent out a chat state notification when it is different from the last one that was transmitted...
        final boolean isDifferentState = lastNotificationSent != state;

        // ... unless it's 'composing' - that can be repeated every so many seconds.
        final boolean isStillComposing = state == ChatState.composing && System.currentTimeMillis() - lastNotificationSentTime > 2000;

        final long now = System.currentTimeMillis();
        if ( isDifferentState || isStillComposing )
        {
            try
            {
                sendChatState( state );
            } catch ( SmackException.NotConnectedException e ) {
                Log.warning( "Unable to update the chat state to " + state, e );
            }
            lastNotificationSent = state;
            lastNotificationSentTime = now;
        }
    }

    /**
     * Handles the Nickname Completion dialog, when Pressing CTRL + SPACE<br>
     * it searches for matches in the current GroupchatList and also in the
     * Roster
     *
     * @throws ChatRoomNotFoundException
     *             when for some reason the GroupChatRoom cannot be found, this
     *             should <u>not</u> happen, since we retrieve it from the
     *             ActiveWindowTab and thus <u>can be ignored</u>
     * @author wolf.posdorfer
     */
    private void handleNickNameCompletion() throws ChatRoomNotFoundException {

	String name = getChatInputEditor().getText();
	if (name.length() < 1)
	    return;
	else if (name.contains(" ")) {
	    if (name.substring(name.lastIndexOf(" ") + 1).length() > 0) {
		name = name.substring(name.lastIndexOf(" ") + 1);
	    }
	}

	Collection<String> groupchatlist = new ArrayList<>();
	Collection<RosterEntry> rosterlist = Roster.getInstanceFor( SparkManager.getConnection() ).getEntries();

	if(SparkManager.getChatManager().getChatContainer().getActiveChatRoom() instanceof GroupChatRoom)
	{
	    groupchatlist  =((GroupChatRoom) SparkManager.getChatManager().getChatContainer().getActiveChatRoom()).getParticipants();
	}
	String newname = null;
	ArrayList<String> namelist = new ArrayList<>();

	for (String lol : groupchatlist) {
	    lol = lol.substring(lol.lastIndexOf("/") + 1);
	    if (lol.toLowerCase().startsWith(name.toLowerCase())) {
		if (newname == null) {
		    newname = lol;
		}
		namelist.add(lol);
	    }
	}

	for (RosterEntry re : rosterlist) {
	    try {
		if (re.getName().toLowerCase().startsWith(name.toLowerCase()) && !namelist.contains(re.getName())) {
		    if (newname == null) {
			newname = re.getName();
		    }
		    namelist.add(re.getName());
		}
	    } catch (NullPointerException npe) {
		// AWESOME!!!
		// happens on shared rosters
		// or when no vcard is set
	    }
	}

	if (newname == null) {
	    newname = "";
	} else {
	    newname = newname.substring(name.length());
	}

	if (namelist.size() <= 1) {
	    // If we only have 1 match, use newname
	    getChatInputEditor().setText(newname);
	} else {
	    // create Popupmenu creating all other matches
	    final JPopupMenu popup = new JPopupMenu();
	    final String namefinal = name;
	    for (final String s : namelist) {
		    JMenuItem temp = new JMenuItem(s);
		    popup.add(temp);
		    temp.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 168886428519741638L;

			@Override
			public void actionPerformed(ActionEvent e) {
			    getChatInputEditor().setText(
				    s.substring(namefinal.length()));
			    popup.setVisible(false);

			}
		    });

	    }
	    popup.show(SparkManager.getChatManager()
		    .getChatContainer(), getChatInputEditor().getCaret()
		    .getMagicCaretPosition().x, SparkManager.getChatManager()
		    .getChatContainer().getHeight() - 20);
	}

    }

    // I would normally use the command pattern, but
    // have no real use when dealing with just a couple options.
    public void actionPerformed(ActionEvent e) {
        sendMessage();

        // Clear send field and disable send button
        getChatInputEditor().clear();
        chatAreaButton.getButton().setEnabled(false);
    }

    /**
     * Creates and sends a message object from the text in
     * the Send Field, using the default nickname specified in your
     * Chat Preferences.
     */
    protected abstract void sendMessage();

    /**
     * Creates a Message object from the given text and delegates to the room
     * for sending.
     *
     * @param text the text to send.
     */
    protected abstract void sendMessage(String text);

    /**
     * Sends the current message.
     *
     * @param message - the message to send.
     */
    public abstract void sendMessage(Message message);

    /**
     * Returns the nickname of the current agent as specified in Chat
     * Preferences.
     *
     * @return the nickname of the agent.
     */
    public String getNickname() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        return pref.getNickname();
    }


    /**
     * The main entry point when receiving any messages. This will
     * either handle a message from a customer or delegate itself
     * as an agent handler.
     *
     * @param message - the message receieved.
     */
    public void insertMessage(Message message) {
        // Fire Message Filters

        SparkManager.getChatManager().filterIncomingMessage(this, message);

        SparkManager.getChatManager().fireGlobalMessageReceievedListeners(this, message);

        addToTranscript(message, true);

        fireMessageReceived(message);

        SparkManager.getWorkspace().getTranscriptPlugin().persistChatRoom(this);
    }


    /**
     * Add a <code>ChatResponse</chat> to the current discussion chat area.
     *
     * @param message    the message to add to the transcript list
     * @param updateDate true if you wish the date label to be updated with the
     *                   date and time the message was received.
     */
    public void addToTranscript(Message message, boolean updateDate) {
        // Create message to persist.
        final Message newMessage = new Message();
        newMessage.setTo(message.getTo());
        newMessage.setFrom(message.getFrom());
        newMessage.setBody(message.getBody());
        final Map<String, Object> properties = new HashMap<>();
        properties.put( "date", new Date() );
        newMessage.addExtension( new JivePropertiesExtension( properties ) );

        transcript.add(newMessage);

        // Add current date if this is the current agent
        if (updateDate && transcriptWindow.getLastUpdated() != null) {
            // Set new label date
            notificationLabel.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_ABOUT_IMAGE));
            notificationLabel.setText(Res.getString("message.last.message.received", SparkManager.DATE_SECOND_FORMATTER.format(transcriptWindow.getLastUpdated())));
        }

        scrollToBottom();
    }

    /**
     * Adds a new message to the transcript history.
     *
     * @param to   who the message is to.
     * @param from who the message was from.
     * @param body the body of the message.
     * @param date when the message was received.
     */
    public void addToTranscript(String to, String from, String body, Date date) {
        final Message newMessage = new Message();
        newMessage.setTo(to);
        newMessage.setFrom(from);
        newMessage.setBody(body);
        final Map<String, Object> properties = new HashMap<>();
        properties.put( "date", new Date() );
        newMessage.addExtension( new JivePropertiesExtension( properties ) );
        transcript.add(newMessage);
    }

    /**
     * Scrolls the chat window to the bottom.
     */
    public void scrollToBottom() {
        if (mousePressed) {
            return;
        }

        int lengthOfChat = transcriptWindow.getDocument().getLength();
        transcriptWindow.setCaretPosition(lengthOfChat);

        try {
            final JScrollBar scrollBar = textScroller.getVerticalScrollBar();
            EventQueue.invokeLater( () -> scrollBar.setValue(scrollBar.getMaximum()) );


        }
        catch (Exception e) {
            Log.error(e);
        }
    }


    /**
     * Checks to see if the Send button should be enabled.
     *
     * @param e - the documentevent to react to.
     */
    protected void checkForText(DocumentEvent e) {
        final int length = e.getDocument().getLength();
        if (length > 0) {
            chatAreaButton.getButton().setEnabled(true);
        }
        else {
            chatAreaButton.getButton().setEnabled(false);
        }
    }

    /**
     * Requests valid focus to the SendField.
     */
    public void positionCursor() {
        getChatInputEditor().setCaretPosition(getChatInputEditor().getCaretPosition());
        chatAreaButton.getChatInputArea().requestFocusInWindow();
    }


    /**
     * Disable the chat room. This is called when a chat has been either transfered over or
     * the customer has left the chat room.
     */
    public abstract void leaveChatRoom();


    /**
     * Process incoming packets.
     *
     * @param stanza - the packet to process
     */
    public void processPacket(Stanza stanza) {
    }


    /**
     * Returns the SendField component.
     *
     * @return the SendField ChatSendField.
     */
    public ChatInputEditor getChatInputEditor() {
        return chatAreaButton.getChatInputArea();
    }

    /**
     * Returns the chatWindow components.
     *
     * @return the ChatWindow component.
     */
    public TranscriptWindow getTranscriptWindow() {
        return transcriptWindow;
    }


    /**
     * Checks to see if enter was pressed and validates room.
     *
     * @param e the KeyEvent
     */
    private void checkForEnter(KeyEvent e) {
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
        if (!keyStroke.equals(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK)) &&
                e.getKeyChar() == KeyEvent.VK_ENTER) {
            e.consume();
            sendMessage();
            getChatInputEditor().setText("");
            getChatInputEditor().setCaretPosition(0);

            SparkManager.getWorkspace().getTranscriptPlugin().persistChatRoom(this);
        }
        else if (keyStroke.equals(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK))) {
            final Document document = getChatInputEditor().getDocument();
            try {
                document.insertString(getChatInputEditor().getCaretPosition(), "\n", null);
                getChatInputEditor().requestFocusInWindow();
                chatAreaButton.getButton().setEnabled(true);
            }
            catch (BadLocationException badLoc) {
                Log.error("Error when checking for enter:", badLoc);
            }
        }
    }

    /**
     * Add a {@link MessageListener} to the current ChatRoom.
     *
     * @param listener - the MessageListener to add to the current ChatRoom.
     */
    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    /**
     * Remove the specified {@link MessageListener } from the current ChatRoom.
     *
     * @param listener - the MessageListener to remove from the current ChatRoom.
     */
    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    /**
     * Notifies all message listeners that
     *
     * @param message the message received.
     */
    private void fireMessageReceived( Message message )
    {
        for ( MessageListener listener : messageListeners )
        {
            try
            {
                listener.messageReceived( this, message );
            }
            catch ( Exception e )
            {
                Log.error( "A MessageListener (" + listener + ") threw an exception while processing a 'message received' event for message: " + message, e );
            }
        }
    }

    /**
     * Notifies all <code>MessageListener</code> that a message has been sent.
     *
     * @param message the message sent.
     */
    protected void fireMessageSent( Message message )
    {
        for ( MessageListener listener : messageListeners )
        {
            try
            {
                listener.messageSent( this, message );
            }
            catch ( Exception e )
            {
                Log.error( "A MessageListener (" + listener + ") threw an exception while processing a 'message sent' event for message: " + message, e );
            }
        }
    }

    /**
     * Returns a map of the current Chat Transcript which is a list of all
     * ChatResponses and their order. You should retrieve this map to get
     * any current chat transcript state.
     *
     * @return - the map of current chat responses.
     */
    public List<Message> getTranscripts() {
        return transcript;
    }

    /**
     * Disables the ChatRoom toolbar.
     */
    public void disableToolbar() {
        final int count = editorBarLeft.getComponentCount();
        for (int i = 0; i < count; i++) {
            final Object o = editorBarLeft.getComponent(i);
            if (o instanceof RolloverButton) {
                final RolloverButton rb = (RolloverButton)o;
                rb.setEnabled(false);
            }
        }
    }

    /**
     * Enable the ChatRoom toolbar.
     */
    public void enableToolbar() {
        final int count = editorBarLeft.getComponentCount();
        for (int i = 0; i < count; i++) {
            final Object o = editorBarLeft.getComponent(i);
            if (o instanceof RolloverButton) {
                final RolloverButton rb = (RolloverButton)o;
                rb.setEnabled(true);
            }
        }
    }


    /**
     * Checks to see if the Send Button should be enabled depending on the
     * current update in SendField.
     *
     * @param event the DocumentEvent from the sendField.
     */
    public void removeUpdate(DocumentEvent event) {
        checkForText(event);
    }

    /**
     * Checks to see if the Send button should be enabled.
     *
     * @param docEvent the document event.
     */
    public void changedUpdate(DocumentEvent docEvent) {
        // Do nothing.
    }

    /**
     * Return the splitpane used in this chat room.
     *
     * @return the splitpane used in this chat room.
     */
    public JSplitPane getSplitPane() {
        return splitPane;
    }

    /**
     * Returns the ChatPanel that contains the ChatWindow and SendField.
     *
     * @return the ChatPanel.
     */
    public JPanel getChatPanel() {
        return chatPanel;
    }

    /**
     * Close the ChatRoom.
     */
    public void closeChatRoom() {
        fireClosingListeners();

        setChatState(ChatState.gone);

        if (typingTimerTask != null) {
            TaskEngine.getInstance().cancelScheduledTask(typingTimerTask);
            typingTimerTask = null;
        }

        getTranscriptWindow().removeContextMenuListener(this);
        getTranscriptWindow().removeMouseListener(transcriptWindowMouseListener);
        getChatInputEditor().removeKeyListener(chatEditorKeyListener);
        this.removeAll();

        textScroller.getViewport().remove(transcriptWindow);

        // Remove Connection Listener
        SparkManager.getConnection().removeConnectionListener(this);
        getTranscriptWindow().setTransferHandler(null);
        getChatInputEditor().setTransferHandler(null);

        transferHandler = null;

        packetIDList.clear();
        messageListeners.clear();
        fileDropListeners.clear();
        getChatInputEditor().close();

        getChatInputEditor().getActionMap().remove("closeTheRoom");
        chatAreaButton.getButton().removeActionListener(this);
        bottomPanel.remove(chatAreaButton);
        _chatFrame.removeWindowToFrontListener(this);

    }

    /**
     * Get the <code>Icon</code> to be used in the tab holding
     * this ChatRoom.
     *
     * @return - <code>Icon</code> to use
     */
    public abstract Icon getTabIcon();

    /**
     * Get the roomname to use for this ChatRoom.
     *
     * @return - the Roomname of this ChatRoom.
     */
    public abstract String getRoomname();

    /**
     * Get the title to use in the tab holding this ChatRoom.
     *
     * @return - the title to use.
     */
    public abstract String getTabTitle();

    /**
     * Returns the title of this room to use. The title
     * will be used in the title bar of the ChatRoom.
     *
     * @return - the title of this ChatRoom.
     */
    public abstract String getRoomTitle();

    /**
     * Returns the <code>Message.Type</code> specific to this
     * chat room.
     * GroupChat is Message.Type.groupchat
     * Normal Chat is Message.TYPE.NORMAL
     *
     * @return the ChatRooms Message.TYPE
     */
    public abstract Message.Type getChatType();


    /**
     * Returns whether or not this ChatRoom is active. Note: carrying
     * a conversation rather than being disabled, as it would be
     * transcript mode.
     *
     * @return true if the chat room is active.
     */
    public abstract boolean isActive();


    /**
     * Returns the notification label. The notification label notifies the
     * user of chat room activity, such as the date of the last message
     * and typing notifications.
     *
     * @return the notification label.
     */
    public JLabel getNotificationLabel() {
        return notificationLabel;
    }

    /**
     * Adds a packetID to the packedIDList. The packetIDLlist
     * keeps track of all messages coming into the chatroom.
     *
     * @param packetID the packetID to add.
     */
    public void addPacketID(String packetID) {
        packetIDList.add(packetID);
    }

    /**
     * Checks if the packetID has already been used.
     *
     * @param packetID the packetID to check for.
     * @return true if the packetID already exists.
     */
    public boolean packetIDExists(String packetID) {
        return packetIDList.contains(packetID);
    }

    /**
     * Returns this instance of the chatroom.
     *
     * @return the current ChatRoom instance.
     */
    public ChatRoom getChatRoom() {
        return this;
    }

    /**
     * Returns the toolbar used on top of the chat room.
     *
     * @return the toolbar used on top of this chat room.
     */
    public ChatToolBar getToolBar() {
        return toolbar;
    }

    protected void addToolbar() {
        add(toolbar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }


    public void insertUpdate(DocumentEvent e) {
        // Meant to be overriden
        checkForText(e);

        setChatState( ChatState.composing );
    }


    /**
     * Override to save transcript in preferred room style.
     */
    public void saveTranscript() {
        getTranscriptWindow().saveTranscript(getTabTitle() + ".html", getTranscripts(), null);
    }


    /**
     * Used for the top toolbar.
     */
    public class ChatToolBar extends JPanel {
		private static final long serialVersionUID = 5926527530611601841L;
		private JPanel buttonPanel;


        /**
         * Default Constructor.
         */
        public ChatToolBar() {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));

            // Set Layout
            setLayout(new GridBagLayout());

            buttonPanel.setOpaque(false);
            add(buttonPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            setOpaque(false);
        }

        /**
         * Adds a new ChatRoomButton the CommandBar.
         *
         * @param button the button.
         */
        public void addChatRoomButton(ChatRoomButton button) {
            buttonPanel.add(button);

            // Make all JButtons the same size
            Component[] comps = buttonPanel.getComponents();
            final int no = comps != null ? comps.length : 0;

            final List<Component> buttons = new ArrayList<>();
            for (int i = 0; i < no; i++) {
                try {
                    Component component = comps[i];
                    if (component instanceof JButton) {
                        buttons.add(component);
                    }
                }
                catch (NullPointerException e) {
                    Log.error(e);
                }
            }

            GraphicUtils.makeSameSize(buttons.toArray(new JComponent[buttons.size()]));
        }

        /**
         * Removes the ChatRoomButton from the CommandBar.
         *
         * @param button the button.
         */
        public void removeChatRoomButton(ChatRoomButton button) {
            buttonPanel.remove(button);
        }
    }

    /**
     * Returns the number of unread messages in this ChatRoom.
     *
     * @return the number of unread messages.
     */
    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    /**
     * Increases the number of unread messages by 1.
     */
    public void increaseUnreadMessageCount() {
        unreadMessageCount++;
    }

    /**
     * Resets the number of unread messages.
     */
    public void clearUnreadMessageCount() {
        unreadMessageCount = 0;
    }

    /**
     * Returns the bottom panel used in the ChatRoom.
     *
     * @return the bottomPane;
     */
    public JPanel getBottomPanel() {
        return bottomPanel;
    }

    /**
     * Returns the Container which holds the ChatWindow.
     *
     * @return the Container.
     */
    public JPanel getChatWindowPanel() {
        return chatWindowPanel;
    }

    /**
     * Adds a new <code>FileDropListener</code> to allow for Drag and Drop notifications
     * of objects onto the ChatWindow.
     *
     * @param listener the listener.
     */
    public void addFileDropListener(FileDropListener listener) {
        fileDropListeners.add(listener);
    }

    /**
     * Remove the <code>FileDropListener</code> from ChatRoom.
     *
     * @param listener the listener.
     */
    public void removeFileDropListener(FileDropListener listener) {
        fileDropListeners.remove(listener);
    }

    /**
     * Notify all users that a collection of files has been dropped onto the ChatRoom.
     *
     * @param files the files dropped.
     */
    public void fireFileDropListeners( Collection<File> files )
    {
        for ( FileDropListener listener : fileDropListeners )
        {
            try
            {
                listener.filesDropped( files, this );
            }
            catch ( Exception e )
            {
                Log.error( "A FileDropListener (" + listener + ") threw an exception while processing a 'files dropped' event.", e );
            }
        }
    }

    /**
     * Returns the panel which contains the toolbar items, such as spell checker.
     *
     * @return the panel which contains the lower toolbar items.
     */
    public JPanel getEditorBar() {
        return editorBarLeft;
    }

    /**
     * Returns the panel next to the editor bar<br>
     * for use with system buttons, like room controlling or toggle stay-on-top
     *
     * @return
     */
    public JPanel getRoomControllerBar() {
	return editorBarRight;
    }

    /**
     * Adds a <code>ChatRoomClosingListener</code> to this ChatRoom. A ChatRoomClosingListener
     * is notified whenever this room is closing.
     *
     * @param listener the ChatRoomClosingListener.
     */
    public void addClosingListener(ChatRoomClosingListener listener)
    {
        closingListeners.add( listener );
    }

    /**
     * Removes a <code>ChatRoomClosingListener</code> from this ChatRoom.
     *
     * @param listener the ChatRoomClosingListener.
     */
    public void removeClosingListener(ChatRoomClosingListener listener)
    {
        closingListeners.remove( listener );
    }

    /**
     * Notifies all <code>ChatRoomClosingListener</code> that this ChatRoom is closing.
     */
    private void fireClosingListeners()
    {
        for ( final ChatRoomClosingListener listener : new ArrayList<>( closingListeners ) ) // Listener can call #removeClosingListener. Prevent ConcurrentModificationException by using a clone.
        {
            try
            {
                listener.closing();
            }
            catch ( Exception e )
            {
                Log.error( "A ChatRoomClosingListener (" + listener + ") threw an exception while processing a 'closing' event.", e );
            }
        }
        closingListeners.clear();
    }

    /**
     * Returns the ScrollPane that contains the TranscriptWindow.
     *
     * @return the <code>TranscriptWindow</code> ScrollPane.
     */
    public JScrollPane getScrollPaneForTranscriptWindow() {
        return textScroller;
    }

    /**
     * Return the "Send" button.
     *
     * @return the send button.
     */
    public JButton getSendButton() {
        return chatAreaButton.getButton();
    }

    /**
     * Returns the VerticalSplitPane used in this ChatRoom.
     *
     * @return the VerticalSplitPane.
     */
    public JSplitPane getVerticalSlipPane() {
        return verticalSplit;
    }


    public void focusGained(FocusEvent focusEvent) {
        validate();
        invalidate();
        repaint();

        if(focusEvent.getComponent().equals(getChatInputEditor())) {
            setChatState( ChatState.active );
        }

    }

    public void poppingUp(Object component, JPopupMenu popup) {
        Action saveAction = new AbstractAction() {
			private static final long serialVersionUID = -3582301239832606653L;

			public void actionPerformed(ActionEvent actionEvent) {
                saveTranscript();
            }
        };
        saveAction.putValue(Action.NAME, Res.getString("action.save"));
        saveAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SAVE_AS_16x16));


        popup.add(saveAction);
    }

    public void poppingDown(JPopupMenu popup) {

    }

    public boolean handleDefaultAction(MouseEvent e) {
        return false;
    }


    public void focusLost(FocusEvent focusEvent) {
        if(focusEvent.getComponent().equals(getChatInputEditor())) {
            setChatState( ChatState.inactive );
        }
    }


    /**
     * Implementation of this method should return the last time this chat room
     * sent or recieved a message.
     *
     * @return the last time (in system milliseconds) that the room last recieved a message.
     */
    public abstract long getLastActivity();


    public void connectionClosed() {
    }

    public void connectionClosedOnError(Exception e) {
    }

    public void reconnectingIn(int seconds) {
    }

    public void reconnectionSuccessful() {
    }

    public void reconnectionFailed(Exception e) {
    }


    public void updateStatus(boolean active)
    {
	_alwaysOnTopItem.setSelected(active);
    }

    public void registeredToFrame(ChatFrame chatframe)
    {
	this._chatFrame = chatframe;
	_chatFrame.addWindowToFronListener(this);
    }

    protected JPanel getEditorWrapperBar() {
        return editorWrapperBar;
    }

    protected JPanel getEditorBarRight() {
        return editorBarRight;
    }

    protected JPanel getEditorBarLeft() {
        return editorBarLeft;
    }

    protected JScrollPane getTextScroller() {
        return textScroller;
    }

    protected Insets getChatPanelInsets() {
        return new Insets(0, 5, 0, 5);
    }

    protected Insets getChatAreaInsets() {
        return new Insets(0, 5, 5, 5);
    }

    protected Insets getEditorWrapperInsets() {
        return new Insets(0, 5, 0, 5);
    }

    public void addChatRoomComponent(JComponent component) {
        editorBarLeft.add(component);
    }

    public void addChatRoomButton(ChatRoomButton button) {
        addChatRoomButton(button, false);
    }

    public void addChatRoomButton(ChatRoomButton button, boolean forceRepaint) {
        toolbar.addChatRoomButton(button);
        if (forceRepaint) {
            toolbar.invalidate();
            toolbar.repaint();
        }
    }

    public void showToolbar() {
        toolbar.setVisible(true);
    }

    public void hideToolbar() {
        toolbar.setVisible(false);
    }

    public void addEditorComponent(JComponent component) {
        editorBarLeft.add(component);
    }

    public void removeEditorComponent(JComponent component) {
        editorBarLeft.remove(component);
    }

    public void addControllerButton(RolloverButton button) {
        editorBarRight.add(button, 0);
    }            
}


