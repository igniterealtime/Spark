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
package org.jivesoftware.spark.filetransfer;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
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
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.filetransfer.preferences.FileTransferPreference;
import org.jivesoftware.spark.preference.PreferenceManager;
import org.jivesoftware.spark.ui.ChatFrame;
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
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.Downloads;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui.ReceiveFileTransfer;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui.SendFileTransfer;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui.TransferUtils;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;

/**
 * Responsible for the handling of File Transfer within Spark. You would use the SparkManager
 * for sending of images, files, multiple files and adding your own transfer listeners for plugin work.
 *
 * @author Derek DeMoro
 */
public class SparkTransferManager {

    private List<FileTransferListener> listeners = new ArrayList<FileTransferListener>();
    private File defaultDirectory;

    private static SparkTransferManager singleton;
    private static final Object LOCK = new Object();

    private FileTransferManager transferManager;
    private Map<String,ArrayList<File>> waitMap = new HashMap<String,ArrayList<File>>();
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
        actionsMenu.addSeparator();
        actionsMenu.add(downloadsMenu);
        downloadsMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {        
   		launchFile(Downloads.getDownloadDirectory());
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
            public void filesDropped(Collection<File> files, Component component) {
                if (component instanceof ContactItem) {
                    ContactItem item = (ContactItem)component;

                    ChatRoom chatRoom = null;
                    for (File file : files) {
                        chatRoom = sendFile(file, item.getJID());
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

//        // Add View Downloads to Command Panel
//        final JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel();
//
//        RolloverButton viewDownloads = new RolloverButton(SparkRes.getImageIcon(SparkRes.DOWNLOAD_16x16));
//        viewDownloads.setToolTipText(Res.getString("menuitem.view.downloads"));
//        commandPanel.add(viewDownloads);
//        viewDownloads.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {              
//                try {
//					Desktop.getDesktop().browse(Downloads.getDownloadDirectory().toURI());
//				} catch (IOException e1) {
//					Log.error("Could not find file-browser");
//				}
//            }
//        });
       }

    
    /**
     * Return correct URI for filePath. dont mind of local or remote path
     * 
     * @param filePath
     * @return
     */
    private static URI getFileURI(String filePath) {
        URI uri = null;
        filePath = filePath.trim();
        if (filePath.indexOf("http") == 0 || filePath.indexOf("\\") == 0) {
            if (filePath.indexOf("\\") == 0)
                filePath = "file:" + filePath;
            try {
                filePath = filePath.replaceAll(" ", "%20");
                URL url = new URL(filePath);
                uri = url.toURI();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        } else {
            File file = new File(filePath);
            uri = file.toURI();
        }
        return uri;
    }
    
    private void handleTransferRequest(FileTransferRequest request, ContactList contactList) {
        // Check if a listener handled this request
        if (fireTransferListeners(request)) {
            return;
        }

        String requestor = request.getRequestor();
        String bareJID = StringUtils.parseBareAddress(requestor);
        String fileName = request.getFileName();


        ContactItem contactItem = contactList.getContactItemByJID(bareJID);

        ChatRoom chatRoom;
        if (contactItem != null) {
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, contactItem.getDisplayName(), contactItem.getDisplayName());
        }
        else {
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, bareJID, bareJID);
        }

        TranscriptWindow transcriptWindow = chatRoom.getTranscriptWindow();
        transcriptWindow.insertCustomText(Res.getString("message.file.transfer.chat.window"), true, false, Color.BLACK);        

        final ReceiveFileTransfer receivingMessageUI = new ReceiveFileTransfer();
        receivingMessageUI.acceptFileTransfer(request);

        chatRoom.addClosingListener(new ChatRoomClosingListener() {
            public void closing() {
                receivingMessageUI.cancelTransfer();
            }
        });
        
        transcriptWindow.addComponent(receivingMessageUI);

        chatRoom.increaseUnreadMessageCount();

        chatRoom.scrollToBottom();
        
        String fileTransMsg = contactItem.getDisplayName() + " " + Res.getString("message.file.transfer.short.message") + " " + fileName;
        SparkManager.getChatManager().getChatContainer().fireNotifyOnMessage(chatRoom, true, fileTransMsg, Res.getString("message.file.transfer.notification"));
    }


    public void sendFileTo(ContactItem item) {
        FileDialog fileChooser = getFileChooser(SparkManager.getMainWindow(), Res.getString("title.select.file.to.send"));
        fileChooser.setVisible(true);

        if (fileChooser.getDirectory() == null || fileChooser.getFile() == null) {
            return;
        }

        File file = new File(fileChooser.getDirectory(), fileChooser.getFile());

        if (file.exists()) {
            defaultDirectory = file.getParentFile();
            sendFile(file, item.getJID());
        }

    }

    private void addSendFileButton() {
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {

            public void chatRoomOpened(final ChatRoom room) {
                if (!(room instanceof ChatRoomImpl)) {
                    return;
                }

                // Otherwise,
                new ChatRoomTransferDecorator(room);
            }

            public void chatRoomClosed(ChatRoom room) {

            }
        });


    }

    public void sendScreenshot(final ChatRoomButton button, final ChatRoom room) {
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

                final Frame frame = new Frame();
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
                            // Nothing to do
                        }

                        if (newImage != null) {
                            sendImage(newImage, room);
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


                frame.setSize(bufferedImage.getWidth(null), bufferedImage.getHeight());
                frame.add(selectionPanel);
                frame.setUndecorated(true);
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
                    ArrayList<File> list = waitMap.get(bareJID);
                    if (list != null) {
                        // Iterate through list and send.
                        Iterator<File> iter = list.iterator();
                        ChatRoom room = null;
                        while (iter.hasNext()) {
                            File file = iter.next();
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
	
	long maxsize = Long.parseLong(Default.getString(Default.FILE_TRANSFER_MAXIMUM_SIZE));
	long warningsize = Long.parseLong(Default.getString(Default.FILE_TRANSFER_WARNING_SIZE));
	

	if(file.length()>= maxsize && maxsize != -1)
	{
	    String maxsizeString = TransferUtils.getAppropriateByteWithSuffix(maxsize);
	    String yoursizeString = TransferUtils.getAppropriateByteWithSuffix(file.length());
	    String output = Res.getString("message.file.transfer.file.too.big.error", maxsizeString, yoursizeString);
	    JOptionPane.showMessageDialog(null, output, Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
	    return null;
	}
	
	if(file.length() >= warningsize && warningsize != -1)
	{
	    int result = JOptionPane.showConfirmDialog(null, Res.getString("message.file.transfer.file.too.big.warning"), Res.getString("title.error"), JOptionPane.YES_NO_OPTION);
	
	    if(result != 0)
	    {
		return null;
	    }
	}
	
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        String bareJID = StringUtils.parseBareAddress(jid);
        String fullJID = PresenceManager.getFullyQualifiedJID(jid);

        if (!PresenceManager.isOnline(jid)) {
            ArrayList<File> list = waitMap.get(jid);
            if (list == null) {
                list = new ArrayList<File>();
            }

            list.add(file);
            waitMap.put(jid, list);

            ChatRoom chatRoom;
            ContactItem contactItem = contactList.getContactItemByJID(jid);
            if (contactItem != null) {
                chatRoom = SparkManager.getChatManager().createChatRoom(jid, contactItem.getDisplayName(), contactItem.getDisplayName());
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
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, contactItem.getDisplayName(), contactItem.getDisplayName());
        }
        else {
            chatRoom = SparkManager.getChatManager().createChatRoom(bareJID, bareJID, bareJID);
        }


        TranscriptWindow transcriptWindow = chatRoom.getTranscriptWindow();

        SendFileTransfer sendingUI = new SendFileTransfer();
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

        try {
            sendingUI.sendFile(transfer, transferManager, fullJID, contactItem.getDisplayName());
        }
        catch (NullPointerException e) {
            Log.error(e);
        }

        transcriptWindow.addComponent(sendingUI);

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
        File tmpDirectory = new File(Spark.getSparkUserHome(), "/tempImages");
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
            // Nothing to do
        }
        catch (IOException e) {
            // Nothing to do
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
        for (FileTransferListener listener : new ArrayList<FileTransferListener>(listeners)) {
            boolean accepted = listener.handleTransfer(request);
            if (accepted) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Launches a file browser or opens a file with java Desktop.open() if is
     * supported
     * 
     * @param file
     */
    private void launchFile(File file) {
        if (!Desktop.isDesktopSupported())
            return;
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(file);
        } catch (IOException ex) {
            launchFile(file.getPath());
        }
    }

    /**
     * Launches a file browser or opens a file with java Desktop.open() if is
     * supported
     * 
     * @param filePath
     */
    private void launchFile(String filePath) {
        if (filePath == null || filePath.trim().length() == 0)
            return;
        if (!Desktop.isDesktopSupported())
            return;
        Desktop dt = Desktop.getDesktop();
        try {
            dt.browse(getFileURI(filePath));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Sets the current default directory to store files.
     *
     * @param directory the default directory.
     */
    public void setDefaultDirectory(File directory) {
        defaultDirectory = directory;
    }

    /**
     * Return the File Chooser to user.
     * @param parent the parent component.
     * @param title the title.
     * @return the FileChooser. (Native Widget)
     */
    public FileDialog getFileChooser(Frame parent, String title) {
        return new FileDialog(parent, title, FileDialog.LOAD);
    }
}
