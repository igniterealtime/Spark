/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.filetransfer;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.filetransfer.preferences.FileTransferPreference;
import org.jivesoftware.spark.preference.PreferenceManager;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatInputEditor;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.FileDropListener;
import org.jivesoftware.spark.ui.ImageSelectionPanel;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.Downloads;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui.ReceiveMessage;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui.SendMessage;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Responsible for the handling of File Transfer within Spark. You would use the SparkManager
 * for sending of images, files, multiple files and adding your own transfer listeners for plugin work.
 *
 * @author Derek DeMoro
 */
public class SparkTransferManager {

    private List listeners = new ArrayList();
    private File defaultDirectory;

    private static SparkTransferManager singleton;
    private static final Object LOCK = new Object();
    private JFileChooser fc;
    private FileTransferManager transferManager;
    private Map waitMap = new HashMap();
    private BufferedImage bufferedImage;
    private ImageSelectionPanel selectionPanel;
    private Robot robot;

    /**
     * Returns the singleton instance of <CODE>SparkTransferManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>SparkTransferManager</CODE>
     */
    public static SparkTransferManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                SparkTransferManager controller = new SparkTransferManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private SparkTransferManager() {
        boolean enabled = Enterprise.containsFeature(Enterprise.FILE_TRANSFER_FEATURE);
        if (!enabled) {
            return;
        }

        SparkManager.getConnection().addConnectionListener(new ConnectionListener() {
            public void connectionClosed() {
            }

            public void connectionClosedOnError(Exception e) {
            }

            public void reconnectingIn(int seconds) {
            }

            public void reconnectionSuccessful() {
                // Re-create transfer manager.
                transferManager = new FileTransferManager(SparkManager.getConnection());
            }

            public void reconnectionFailed(Exception e) {
            }
        });

        try {
            robot = new Robot();
            selectionPanel = new ImageSelectionPanel();
        }
        catch (AWTException e) {
            Log.error(e);
        }

        // Register Preferences
        PreferenceManager prefManager = SparkManager.getPreferenceManager();
        prefManager.addPreference(new FileTransferPreference());

        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        JMenuItem downloadsMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.DOWNLOAD_16x16));
        ResourceUtils.resButton(downloadsMenu, Res.getString("menuitem.view.downloads"));
        actionsMenu.add(downloadsMenu);
        downloadsMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Downloads downloads = Downloads.getInstance();
                downloads.showDownloadsDirectory();
            }
        });

        // Create the file transfer manager
        transferManager = new FileTransferManager(SparkManager.getConnection());
        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        // Create the listener
        transferManager.addFileTransferListener(new org.jivesoftware.smackx.filetransfer.FileTransferListener() {
            public void fileTransferRequest(final FileTransferRequest request) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        handleTransferRequest(request, contactList);
                    }
                });
            }
        });

        // Add Send File to Chat Room
        addSendFileButton();


        contactList.addFileDropListener(new FileDropListener() {
            public void filesDropped(Collection files, Component component) {
                if (component instanceof ContactItem) {
                    ContactItem item = (ContactItem)component;

                    ChatRoom chatRoom = null;
                    Iterator iter = files.iterator();
                    while (iter.hasNext()) {
                        chatRoom = sendFile((File)iter.next(), item.getJID());
                    }

                    if (chatRoom != null) {
                        SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
                    }
                }
            }
        });

        if (defaultDirectory == null) {
            defaultDirectory = new File(System.getProperty("user.home"));
        }

        addPresenceListener();

        // Add View Downloads to Command Panel
        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        final JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel();

        RolloverButton viewDownloads = new RolloverButton(SparkRes.getImageIcon(SparkRes.DOWNLOAD_16x16));
        viewDownloads.setToolTipText(Res.getString("menuitem.view.downloads"));
        commandPanel.add(viewDownloads);
        viewDownloads.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Downloads downloads = Downloads.getInstance();
                downloads.showDownloadsDirectory();
            }
        });
    }

    private void handleTransferRequest(FileTransferRequest request, ContactList contactList) {
        // Check if a listener handled this request
        if (fireTransferListeners(request)) {
            return;
        }

        String requestor = request.getRequestor();
        String bareJID = StringUtils.parseBareAddress(requestor);


        ContactItem contactItem = contactList.getContactItemByJID(bareJID);

        ChatRoom chatRoom;
        if (contactItem != null) {
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, contactItem.getNickname(), contactItem.getNickname());
        }
        else {
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, bareJID, bareJID);
        }

        TranscriptWindow transcriptWindow = chatRoom.getTranscriptWindow();
        StyledDocument doc = (StyledDocument)transcriptWindow.getDocument();

        // The image must first be wrapped in a style
        Style style = doc.addStyle("StyleName", null);

        final ReceiveMessage receivingMessageUI = new ReceiveMessage();
        receivingMessageUI.acceptFileTransfer(request);

        chatRoom.addClosingListener(new ChatRoomClosingListener() {
            public void closing() {
                receivingMessageUI.cancelTransfer();
            }
        });

        StyleConstants.setComponent(style, receivingMessageUI);

        // Insert the image at the end of the text
        try {
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
        }
        catch (BadLocationException e) {
            Log.error(e);
        }

        chatRoom.increaseUnreadMessageCount();

        chatRoom.scrollToBottom();

        SparkManager.getChatManager().getChatContainer().fireNotifyOnMessage(chatRoom);
    }


    public void sendFileTo(ContactItem item) {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        getFileChooser().setDialogTitle(Res.getString("title.select.file.to.send"));
        int ok = getFileChooser().showOpenDialog(contactList);
        if (ok != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] files = getFileChooser().getSelectedFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists()) {
                defaultDirectory = file.getParentFile();
                sendFile(file, item.getJID());
            }
        }
    }

    private void addSendFileButton() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {

            FileDropListener fileDropListener;

            public void chatRoomOpened(final ChatRoom room) {
                if (!(room instanceof ChatRoomImpl)) {
                    return;
                }


                final ChatInputEditor chatSendField = room.getChatInputEditor();
                chatSendField.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent ke) {
                        if (ke.getKeyCode() == KeyEvent.VK_V) {
                            int i = ke.getModifiers();
                            if ((i & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                                Clipboard clb = Toolkit.getDefaultToolkit().getSystemClipboard();
                                Transferable contents = clb.getContents(ke.getSource());
                                if (contents != null && contents.getTransferDataFlavors().length == 1) {
                                    if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                                        sendImage(getClipboard(), room);
                                    }
                                }
                            }
                        }
                    }

                });

                fileDropListener = new FileDropListener() {
                    public void filesDropped(Collection files, Component component) {
                        if (component instanceof ChatRoomImpl) {
                            ChatRoomImpl roomImpl = (ChatRoomImpl)component;


                            Iterator iter = files.iterator();
                            while (iter.hasNext()) {
                                sendFile((File)iter.next(), roomImpl.getParticipantJID());
                            }

                            SparkManager.getChatManager().getChatContainer().activateChatRoom(roomImpl);
                        }
                    }
                };

                room.addFileDropListener(fileDropListener);


                ChatRoomButton sendFileButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.SEND_FILE_24x24));
                sendFileButton.setToolTipText(Res.getString("message.send.file.to.user"));

                room.getToolBar().addChatRoomButton(sendFileButton);

                final ChatRoomButton sendScreenShotButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.PHOTO_IMAGE));
                sendScreenShotButton.setToolTipText(Res.getString("message.send.picture"));
                room.getToolBar().addChatRoomButton(sendScreenShotButton);

                sendScreenShotButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        sendScreenshot(sendScreenShotButton, room);
                    }
                });

                sendFileButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingWorker worker = new SwingWorker() {
                            public Object construct() {
                                try {
                                    Thread.sleep(10);
                                }
                                catch (InterruptedException e1) {
                                    Log.error(e1);
                                }
                                return true;
                            }

                            public void finished() {
                                ChatRoomImpl roomImpl = (ChatRoomImpl)room;

                                getFileChooser().setDialogTitle(Res.getString("title.select.file.to.send"));
                                getFileChooser().setMultiSelectionEnabled(true);
                                int ok = getFileChooser().showOpenDialog(roomImpl);
                                if (ok != JFileChooser.APPROVE_OPTION) {
                                    return;
                                }

                                File[] file = getFileChooser().getSelectedFiles();
                                if (file == null || file.length == 0) {
                                    return;
                                }


                                final int no = file.length;
                                for (int i = 0; i < no; i++) {
                                    File sendFile = file[i];

                                    if (sendFile.exists()) {
                                        defaultDirectory = sendFile.getParentFile();
                                        sendFile(sendFile, roomImpl.getParticipantJID());
                                    }
                                }


                            }
                        };
                        worker.start();
                    }
                });
            }

            public void chatRoomClosed(ChatRoom room) {
                room.removeFileDropListener(fileDropListener);
            }
        });


    }

    private void sendScreenshot(final ChatRoomButton button, final ChatRoom room) {
        button.setEnabled(false);

        final MainWindow mainWindow = SparkManager.getMainWindow();
        final ChatFrame chatFrame = SparkManager.getChatManager().getChatContainer().getChatFrame();

        final boolean mainWindowVisible = mainWindow.isVisible();
        final boolean chatFrameVisible = chatFrame.isVisible();

        if (mainWindowVisible) {
            mainWindow.setVisible(false);
        }

        if (chatFrameVisible) {
            chatFrame.setVisible(false);
        }

        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(1000);
                    Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    return robot.createScreenCapture(area);
                }
                catch (Throwable e) {
                    Log.error(e);

                    if (mainWindowVisible) {
                        mainWindow.setVisible(true);
                    }

                    if (chatFrameVisible) {
                        chatFrame.setVisible(true);
                    }

                }
                return null;
            }

            public void finished() {
                bufferedImage = (BufferedImage)get();
                if (bufferedImage == null) {
                    JOptionPane.showMessageDialog(null, Res.getString("title.error"), "Unable to process screenshot.", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                final JFrame frame = new JFrame();
                frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

                selectionPanel.setImage(bufferedImage);
                selectionPanel.validate();
                selectionPanel.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        Rectangle clip = selectionPanel.getClip();
                        BufferedImage newImage = null;
                        try {
                            newImage = bufferedImage.getSubimage((int)clip.getX(), (int)clip.getY(), (int)clip.getWidth(), (int)clip.getHeight());
                        }
                        catch (Exception e1) {

                        }

                        if (newImage != null) {
                            sendImage(newImage, room);
                            newImage = null;
                            bufferedImage = null;
                            selectionPanel.clear();
                        }

                        frame.dispose();
                        frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                        if (mainWindowVisible) {
                            mainWindow.setVisible(true);
                        }

                        if (chatFrameVisible) {
                            chatFrame.setVisible(true);
                        }

                        selectionPanel.removeMouseListener(this);
                    }
                });

                frame.addKeyListener(new KeyAdapter() {
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                            frame.dispose();
                            frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            if (mainWindowVisible) {
                                mainWindow.setVisible(true);
                            }

                            if (chatFrameVisible) {
                                chatFrame.setVisible(true);
                            }
                        }
                    }
                });


                frame.setUndecorated(true);
                frame.setSize(bufferedImage.getWidth(null), bufferedImage.getHeight());
                frame.getContentPane().add(selectionPanel);

                // Determine if full-screen mode is supported directly
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gs = ge.getDefaultScreenDevice();
                if (gs.isFullScreenSupported()) {
                    gs.setFullScreenWindow(frame);
                }
                else {
                    // Full-screen mode will be simulated
                    frame.setVisible(true);
                }


                button.setEnabled(true);
            }
        };
        worker.start();
    }

    private void addPresenceListener() {
        SparkManager.getConnection().addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence)packet;
                if (presence.isAvailable()) {
                    String bareJID = StringUtils.parseBareAddress(presence.getFrom());

                    // Iterate through map.
                    List list = (List)waitMap.get(bareJID);
                    if (list != null) {
                        // Iterate through list and send.
                        Iterator iter = list.iterator();
                        ChatRoom room = null;
                        while (iter.hasNext()) {
                            File file = (File)iter.next();
                            room = sendFile(file, bareJID);
                        }

                        if (room != null) {
                            Message message = new Message();
                            message.setBody(Res.getString("message.sent.offline.files"));
                            room.sendMessage(message);
                        }
                    }


                    waitMap.remove(bareJID);
                }
            }
        }, new PacketTypeFilter(Presence.class));
    }

    /**
     * Send a file to a user.
     *
     * @param file the file to send.
     * @param jid  the jid of the user to send the file to.
     * @return the ChatRoom of the user.
     */
    public ChatRoom sendFile(File file, String jid) {
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        String bareJID = StringUtils.parseBareAddress(jid);
        String fullJID = PresenceManager.getFullyQualifiedJID(jid);

        if (!PresenceManager.isOnline(jid)) {
            List list = (List)waitMap.get(jid);
            if (list == null) {
                list = new ArrayList();
            }

            list.add(file);
            waitMap.put(jid, list);

            ChatRoom chatRoom;
            ContactItem contactItem = contactList.getContactItemByJID(jid);
            if (contactItem != null) {
                chatRoom = SparkManager.getChatManager().createChatRoom(jid, contactItem.getNickname(), contactItem.getNickname());
            }
            else {
                chatRoom = SparkManager.getChatManager().createChatRoom(jid, jid, jid);
            }

            chatRoom.getTranscriptWindow().insertNotificationMessage("The user is offline. Will auto-send \"" + file.getName() + "\" when user comes back online.", ChatManager.ERROR_COLOR);
            return null;
        }

        // Create the outgoing file transfer
        final OutgoingFileTransfer transfer = transferManager.createOutgoingFileTransfer(fullJID);


        ContactItem contactItem = contactList.getContactItemByJID(bareJID);

        ChatRoom chatRoom;
        if (contactItem != null) {
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, contactItem.getNickname(), contactItem.getNickname());
        }
        else {
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, bareJID, bareJID);
        }


        TranscriptWindow transcriptWindow = chatRoom.getTranscriptWindow();
        StyledDocument doc = (StyledDocument)transcriptWindow.getDocument();

        // The image must first be wrapped in a style
        Style style = doc.addStyle("StyleName", null);

        SendMessage sendingUI = new SendMessage();
        try {
            transfer.sendFile(file, "Sending file");
        }
        catch (XMPPException e) {
            Log.error(e);
        }

        // Add listener to cancel transfer is sending file to user who just went offline.
        AndFilter presenceFilter = new AndFilter(new PacketTypeFilter(Presence.class), new FromContainsFilter(bareJID));
        final PacketListener packetListener = new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence)packet;
                if (!presence.isAvailable()) {
                    if (transfer != null) {
                        transfer.cancel();
                    }
                }
            }
        };

        // Add presence listener to check if user is offline and cancel sending.
        SparkManager.getConnection().addPacketListener(packetListener, presenceFilter);

        chatRoom.addClosingListener(new ChatRoomClosingListener() {
            public void closing() {
                SparkManager.getConnection().removePacketListener(packetListener);

                if (!transfer.isDone()) {
                    transfer.cancel();
                }
            }
        });

        sendingUI.sendFile(transfer, transferManager, fullJID, contactItem.getNickname());
        StyleConstants.setComponent(style, sendingUI);

        // Insert the image at the end of the text
        try {
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
        }
        catch (BadLocationException e) {
            Log.error(e);
        }

        chatRoom.scrollToBottom();
        return chatRoom;
    }

    /**
     * Send an image to a user.
     *
     * @param image the image to send.
     * @param room  the ChatRoom of the user you wish to send the image to.
     */
    public void sendImage(final BufferedImage image, final ChatRoom room) {
        File tmpDirectory = new File(Spark.getUserSparkHome(), "/tempImages");
        tmpDirectory.mkdirs();

        String imageName = "image_" + StringUtils.randomString(2) + ".png";
        final File imageFile = new File(tmpDirectory, imageName);

        // Write image to system.
        room.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker writeImageThread = new SwingWorker() {
            public Object construct() {
                try {
                    // Write out file in separate thread.
                    ImageIO.write(image, "png", imageFile);
                }
                catch (IOException e) {
                    Log.error(e);
                }
                return true;
            }

            public void finished() {
                ChatRoomImpl roomImpl = (ChatRoomImpl)room;
                sendFile(imageFile, roomImpl.getParticipantJID());
                SparkManager.getChatManager().getChatContainer().activateChatRoom(room);
                room.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        };
        writeImageThread.start();
    }

    /**
     * Returns an image if one is found in the clipboard, otherwise null is returned.
     *
     * @return the image in the clipboard if found, otherwise null.
     */
    public static BufferedImage getClipboard() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                return (BufferedImage)t.getTransferData(DataFlavor.imageFlavor);
            }
        }
        catch (UnsupportedFlavorException e) {
        }
        catch (IOException e) {
        }
        return null;
    }

    /**
     * Adds a new TransferListener to the SparkManager. FileTransferListeners can be used
     * to intercept incoming file transfers for own customizations. You may wish to not
     * allow certain file transfers, or have your own UI to handle incoming files.
     *
     * @param listener the listener
     */
    public void addTransferListener(FileTransferListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the FileTransferListener.
     *
     * @param listener the listener
     */
    public void removeTransferListener(FileTransferListener listener) {
        listeners.remove(listener);
    }

    private boolean fireTransferListeners(FileTransferRequest request) {
        final Iterator iter = new ArrayList(listeners).iterator();
        while (iter.hasNext()) {
            FileTransferListener listener = (FileTransferListener)iter.next();
            boolean accepted = listener.handleTransfer(request);
            if (accepted) {
                return true;
            }
        }
        return false;
    }

    private JFileChooser getFileChooser() {
        if (fc == null) {
            fc = new JFileChooser(defaultDirectory);
            fc.setMultiSelectionEnabled(true);
            if (Spark.isWindows()) {
                fc.setFileSystemView(new WindowsFileSystemView());
            }

            fc.setApproveButtonText(Res.getString("button.send"));
        }
        return fc;
    }
}
