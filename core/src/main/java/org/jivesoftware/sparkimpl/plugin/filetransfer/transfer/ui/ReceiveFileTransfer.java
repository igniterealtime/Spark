/**
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
package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.filetransfer.preferences.FileTransferPreference;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.Downloads;

import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.stringprep.XmppStringprepException;

public class ReceiveFileTransfer extends JPanel {

    private static final long serialVersionUID = -2974192409566650923L;
    private final FileDragLabel imageLabel = new FileDragLabel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel fileLabel = new JLabel();

    private final TransferButton acceptButton = new TransferButton(Res.getString("accept"), SparkRes.getImageIcon(SparkRes.ACCEPT_INVITE_IMAGE));
    private final TransferButton declineButton = new TransferButton(Res.getString("reject"), SparkRes.getImageIcon(SparkRes.REJECT_INVITE_IMAGE));
    private final TransferButton pathButton = new TransferButton(Res.getString("message.file.transfer.direrror.setdir"), SparkRes.getImageIcon(SparkRes.SETTINGS_IMAGE_16x16));
    private final JProgressBar progressBar = new JProgressBar();
    private IncomingFileTransfer transfer;
    private final TransferButton cancelButton = new TransferButton();
    private final JLabel progressLabel = new JLabel();
    private long bytesRead;
    private long startTime;
    private long endTime;
    private final ChatRoom chatRoom;
    private String nickname;
    private String fileName;

    public ReceiveFileTransfer(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        setLayout(new GridBagLayout());

        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        add(acceptButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(pathButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        pathButton.setVisible(false);
        add(declineButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        // Decorate Cancel Button
        decorateCancelButton();

        pathButton.setForeground(new Color(73, 113, 196));
        acceptButton.setForeground(new Color(73, 113, 196));
        declineButton.setForeground(new Color(73, 113, 196));
        pathButton.setFont(new Font("Dialog", Font.BOLD, 11));
        declineButton.setFont(new Font("Dialog", Font.BOLD, 11));
        acceptButton.setFont(new Font("Dialog", Font.BOLD, 11));

        acceptButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));
        declineButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));

        pathButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pathButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pathButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        acceptButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                acceptButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                acceptButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        declineButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                declineButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                declineButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public void acceptFileTransfer(final FileTransferRequest request) {
        fileName = request.getFileName();
        long fileSize = request.getFileSize();
        Jid requestor = request.getRequestor();
        BareJid bareJID = requestor.asBareJid();
        //SPARK-1869
        FileTransferNegotiator.getInstanceFor(SparkManager.getConnection());
        FileTransferNegotiator.IBB_ONLY = SettingsManager.getLocalPreferences().isFileTransferIbbOnly();

        ByteFormat format = new ByteFormat();
        String fileSizeString = format.format(fileSize);

        fileLabel.setText(fileName + " (" + fileSizeString + ")");

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(bareJID);
        nickname = contactItem.getDisplayName();

        saveEventToHistory(bareJID, Res.getString("message.file.transfer.history.request.received", fileName, fileSizeString, nickname));
        titleLabel.setText(Res.getString("message.user.is.sending.you.a.file", nickname));

        File tempFile = new File(Spark.getSparkUserHome(), "/tmp");
        try {
            tempFile.mkdirs();

            File file = new File(tempFile, fileName);
            file.delete();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write("a");
            out.close();

            imageLabel.setIcon(GraphicUtils.getIcon(file));

            // Delete temp file when program exits.
            file.delete();
        } catch (IOException e) {
            imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
            Log.error("An exception occurred while accepting a file transfer.", e);
        }

        if (SettingsManager.getLocalPreferences().isAutoAcceptFileTransferFromContacts()) {
            final Roster roster = SparkManager.getRoster();
            if (roster.isSubscribedToMyPresence(requestor)) {
                doAccept(request);
                return;
            }
        }
        acceptButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                doAccept(request);
            }
        });

        declineButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                doReject(request);
            }
        });
    }

    private void doAccept(FileTransferRequest request) {
        Jid requestor = request.getRequestor();
        BareJid bareJID = requestor.asBareJid();
        saveEventToHistory(bareJID, Res.getString("message.file.transfer.history.you.accepted", fileName, nickname));
        try {
            Downloads.checkDownloadDirectory();
            acceptRequest(request);
        } catch (Exception ex) {
            // this means there is a problem with the download directory
            saveEventToHistory(bareJID, Res.getString("message.file.transfer.history.receive.failed", fileName, nickname));
            try {
                request.reject();
            } catch (SmackException.NotConnectedException | InterruptedException e1) {
                Log.warning("Unable to reject the request.", ex);
            }

            setBackground(new Color(239, 245, 250));
            acceptButton.setVisible(false);
            declineButton.setVisible(false);
            if (Downloads.getDownloadDirectory() == null) {
                fileLabel.setText("");
            } else {
                ResourceUtils.resLabel(fileLabel, null, Res.getString("label.transfer.download.directory") +
                    " " + Downloads.getDownloadDirectory().getAbsolutePath());
            }

            // option to set a new path for the file-download
            pathButton.setVisible(true);
            pathButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Preference p = SparkManager.getPreferenceManager().getPreference(
                        new FileTransferPreference().getNamespace());
                    // retrieve the file transfer preferences and show the preference menu
                    // to the user
                    SparkManager.getPreferenceManager().showPreferences(p);
                }
            });

            titleLabel.setText(ex.getMessage());
            titleLabel.setForeground(new Color(65, 139, 179));

            invalidate();
            validate();
            repaint();
        }
    }

    private void doReject(FileTransferRequest request) {
        Jid requestor = request.getRequestor();
        BareJid bareJID = requestor.asBareJid();
        saveEventToHistory(bareJID, Res.getString("message.file.transfer.history.you.rejected", fileName, nickname));
        rejectRequest(request);
    }

    private void rejectRequest(FileTransferRequest request) {
        try {
            request.reject();
        } catch (SmackException.NotConnectedException | InterruptedException ex) {
            Log.warning("Unable to reject the request.", ex);
        }
        setBackground(new Color(239, 245, 250));
        acceptButton.setVisible(false);
        declineButton.setVisible(false);
        fileLabel.setText("");
        titleLabel.setText(Res.getString("message.file.transfer.canceled"));
        titleLabel.setForeground(new Color(65, 139, 179));

        invalidate();
        validate();
        repaint();
    }

    private void acceptRequest(final FileTransferRequest request) {
        Jid requester = request.getRequestor();
        BareJid bareJID = requester.asBareJid();

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        final ContactItem contactItem = contactList
            .getContactItemByJID(bareJID);

        setBackground(new Color(239, 245, 250));
        acceptButton.setVisible(false);
        declineButton.setVisible(false);
        titleLabel.setText(Res.getString("message.negotiate.file.transfer"));
        titleLabel.setForeground(new Color(65, 139, 179));

        add(progressBar, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));
        add(progressLabel, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));
        add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        cancelButton.setVisible(true);
        transfer = request.accept();

        final File downloadedFile = toUniqueDownloadedFile(request);

        try {
            startTime = System.currentTimeMillis();
            transfer.receiveFile(downloadedFile);
        } catch (SmackException | IOException e) {
            Log.error("An error occurred while accepting a file transfer request.", e);
        }

        progressBar.setMaximum(100); // setting it to percent
        progressBar.setStringPainted(true);

        final Timer timer = new Timer();
        TimerTask updateProgressBar = new TimerTask() {
            @Override
            public void run() {
                if (transfer.getAmountWritten() >= request.getFileSize()
                    || transfer.getStatus() == FileTransfer.Status.error
                    || transfer.getStatus() == FileTransfer.Status.refused
                    || transfer.getStatus() == FileTransfer.Status.cancelled
                    || transfer.getStatus() == FileTransfer.Status.complete) {
                    this.cancel();
                    timer.cancel();
                    endTime = System.currentTimeMillis();
                    updateOnFinished(request, downloadedFile);
                } else {
                    // 100 % = FileSize
                    // x %   = CurrentSize
                    long p = (transfer.getAmountWritten() * 100 / transfer.getFileSize());
                    progressBar.setValue(Math.round(p));
                }

            }
        };

        final Timer timer2 = new Timer();
        TimerTask updateProgressBarText = new TimerTask() {
            long timeNow;
            long timeEarlier;
            long bytesNow;
            long bytesEarlier;

            @Override
            public void run() {
                if (transfer.getAmountWritten() >= request.getFileSize()
                    || transfer.getStatus() == FileTransfer.Status.error
                    || transfer.getStatus() == FileTransfer.Status.refused
                    || transfer.getStatus() == FileTransfer.Status.cancelled
                    || transfer.getStatus() == FileTransfer.Status.complete) {
                    this.cancel();
                    timer2.cancel();
                } else {
                    timeNow = System.currentTimeMillis();
                    bytesNow = transfer.getAmountWritten();
                    bytesRead = transfer.getAmountWritten();
                    if (bytesRead == -1) {
                        bytesRead = 0;
                    }
                    ByteFormat format = new ByteFormat();
                    String text = format.format(bytesRead);

                    FileTransfer.Status status = transfer.getStatus();
                    if (status == FileTransfer.Status.in_progress) {
                        titleLabel.setText(Res.getString("message.receiving.file", contactItem.getDisplayName()));
                        String speed = TransferUtils.calculateSpeed(bytesNow - bytesEarlier, timeNow - timeEarlier);
                        String est = TransferUtils.calculateEstimate(bytesNow, transfer.getFileSize(), startTime, System.currentTimeMillis());
                        progressLabel.setText(Res.getString("message.transfer.progressbar.text.received", text, speed, est));
                    } else if (status == FileTransfer.Status.negotiating_stream) {
                        titleLabel.setText(Res.getString("message.negotiate.stream"));
                    }
                    bytesEarlier = bytesNow;
                    timeEarlier = timeNow;
                }
            }
        };

        timer.scheduleAtFixedRate(updateProgressBar, 10, 10);
        timer2.scheduleAtFixedRate(updateProgressBarText, 10, 500);
    }

    /**
     * Return a file reference that is suitable to store the content from the inbound file transfer into.
     *
     * This method will use the filename as provided by the request, but will add increments to that name in case a file
     * of the same name already exists in the download directory.
     *
     * @param request The file transfer request
     * @return A file object that can be used to store the inbound file data into.
     * @see <a href="https://igniterealtime.atlassian.net/browse/SPARK-2198">SPARK-2198</a> Prevent incoming file transfer to overwrite existing file.
     */
    protected static File toUniqueDownloadedFile(FileTransferRequest request)
    {
        File downloadedFile = new File(Downloads.getDownloadDirectory(), request.getFileName());
        int count = 1;
        while (downloadedFile.isFile() && downloadedFile.exists()) {
            if ( request.getFileName().contains(".")) {
                // start finding unused names like 'file (1).txt' and 'file (2).txt'
                final String name = request.getFileName().substring(0, request.getFileName().lastIndexOf('.'));
                final String ext = request.getFileName().substring(request.getFileName().lastIndexOf('.'));
                downloadedFile = new File(Downloads.getDownloadDirectory(), name +" ("+count++ +")" + ext);
            } else {
                // start finding unused names like 'file-1' and 'file-2'
                downloadedFile = new File(Downloads.getDownloadDirectory(), request.getFileName() + "-"+count++);
            }
        }
        return downloadedFile;
    }

    private void updateOnFinished(final FileTransferRequest request,
                                  final File downloadedFile) {
        Jid requestor = request.getRequestor();
        BareJid bareJID = requestor.asBareJid();
        if (transfer.getAmountWritten() >= request.getFileSize()) {
            transferDone(bareJID, downloadedFile);

            imageLabel.setFile(downloadedFile);
            imageLabel.setToolTipText(Res.getString("message.click.to.open"));
            titleLabel.setToolTipText(Res.getString("message.click.to.open"));
            String fin = TransferUtils.convertSecondstoHHMMSS(Math.round(endTime - startTime) / 1000);
            progressLabel.setText(Res.getString("label.time", fin));

            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        launchFile(downloadedFile);
                    }
                }
            });

            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    imageLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });

            titleLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        launchFile(downloadedFile);
                    }
                }
            });

            titleLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    titleLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });

            invalidate();
            validate();
            repaint();
        }

        String transferMessage = "";
        if (transfer.getStatus() == FileTransfer.Status.error) {
            if (transfer.getException() != null) {
                Log.error("There was an error during file transfer.", transfer.getException());
            }
            transferMessage = Res.getString("message.error.during.file.transfer");
            saveEventToHistory(bareJID, Res.getString("message.file.transfer.history.receive.failed", fileName, nickname));
        } else if (transfer.getStatus() == FileTransfer.Status.refused) {
            transferMessage = Res.getString("message.transfer.refused");
        } else if (transfer.getStatus() == FileTransfer.Status.cancelled ||
            transfer.getAmountWritten() < request.getFileSize()) {
            transferMessage = Res.getString("message.transfer.cancelled");
            saveEventToHistory(bareJID, Res.getString("message.file.transfer.history.receive.canceled", fileName, nickname));
        } else if (transfer.getAmountWritten() >= request.getFileSize()) {
            transferMessage = Res.getString("message.transfer.complete", downloadedFile.getName()); // TODO this overwrites the message that was set by transferDone
            saveEventToHistory(bareJID, Res.getString("message.file.transfer.history.receive.success", fileName, nickname));
        }

        setFinishedText(transferMessage);
        showAlert(true);
    }

    private void setFinishedText(String text) {
        acceptButton.setVisible(false);
        declineButton.setVisible(false);
        fileLabel.setText("");
        titleLabel.setText(text);
        titleLabel.setForeground(new Color(65, 139, 179));
        progressBar.setVisible(false);
        cancelButton.setVisible(false);
        invalidate();
        validate();
        repaint();
    }

    private void transferDone(final BareJid requestor, final File downloadedFile) {
        cancelButton.setVisible(false);

        showAlert(true);

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(requestor);

        titleLabel.setText(Res.getString("message.received.file", contactItem.getDisplayName()));
        fileLabel.setText(downloadedFile.getName());

        remove(acceptButton);
        remove(declineButton);
        remove(progressBar);
        remove(pathButton);

        final TransferButton openFileButton = new TransferButton();
        final TransferButton openFolderButton = new TransferButton();
        add(openFileButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(openFolderButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        openFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                openFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                openFileButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                launchFile(downloadedFile);
            }
        });

        openFolderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                openFolderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                openFolderButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent event) {
                launchFile(Downloads.getDownloadDirectory());
            }
        });

        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        ResourceUtils.resButton(openFileButton, Res.getString("open"));
        ResourceUtils.resButton(openFolderButton, Res.getString("open.folder"));

        openFileButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));
        openFileButton.setForeground(new Color(73, 113, 196));
        openFileButton.setFont(new Font("Dialog", Font.BOLD, 11));

        openFolderButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));
        openFolderButton.setForeground(new Color(73, 113, 196));
        openFolderButton.setFont(new Font("Dialog", Font.BOLD, 11));

        imageLabel.setIcon(GraphicUtils.getIcon(downloadedFile));
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e, downloadedFile);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e, downloadedFile);
            }
        });

        if (isImage(downloadedFile.getName())) {
            try {
                URL imageURL = downloadedFile.toURI().toURL();
                ImageIcon image = new ImageIcon(imageURL);
                image = GraphicUtils.scaleImageIcon(image, 64, 64);
                imageLabel.setIcon(image);
            } catch (MalformedURLException e) {
                Log.error("Could not locate image.", e);
                imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
            }
        }

        invalidate();
        validate();
        repaint();
    }

    /***
     * Adds an event text as a message to transcript and saves it to history
     * @param bareJID requestor JID
     * @param eventText Contains file transfer event text
     */
    private void saveEventToHistory(BareJid bareJID, String eventText) {
        MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
            .setBody(eventText)
            .to(bareJID)
            .from(SparkManager.getSessionManager().getJID());
        Message message = messageBuilder.build();
        chatRoom.addToTranscript(message, false);
        SparkManager.getWorkspace().getTranscriptPlugin().persistChatRoom(chatRoom);
    }

    private static class TransferButton extends JButton {
        private static final long serialVersionUID = -9198495278243559064L;

        public TransferButton() {
            decorate();
        }

        /**
         * Create a new RolloverButton.
         *
         * @param text the button text.
         * @param icon the button icon.
         */
        public TransferButton(String text, Icon icon) {
            super(text, icon);
            decorate();
        }

        /**
         * Decorates the button with the approriate UI configurations.
         */
        private void decorate() {
            setBorderPainted(false);
            setOpaque(true);

            setContentAreaFilled(false);
            setMargin(new Insets(1, 1, 1, 1));
        }
    }

    private boolean isImage(String fileName) {
        fileName = fileName.toLowerCase();

        String[] imageTypes = {"jpeg", "gif", "jpg", "png"};
        for (String imageType : imageTypes) {
            if (fileName.endsWith(imageType)) {
                return true;
            }
        }
        return false;
    }

    private void showAlert(boolean alert) {
        if (alert) {
            titleLabel.setForeground(new Color(211, 174, 102));
            setBackground(new Color(250, 249, 242));
        } else {
            setBackground(new Color(239, 245, 250));
            titleLabel.setForeground(new Color(65, 139, 179));
        }
    }

    public void cancelTransfer() {
        if (transfer != null) {
            transfer.cancel();
        }
    }

    /**
     * Handle the UI for the Cancel Button
     */
    private void decorateCancelButton() {
        cancelButton.setVisible(false);
        ResourceUtils.resButton(cancelButton, Res.getString("cancel"));
        cancelButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));
        cancelButton.setForeground(new Color(73, 113, 196));
        cancelButton.setFont(new Font("Dialog", Font.BOLD, 11));
        cancelButton.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

        cancelButton.addActionListener(e -> {
            cancelTransfer();
            acceptButton.setVisible(false);
            declineButton.setVisible(false);
            cancelButton.setVisible(false);
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void showPopup(MouseEvent e, final File downloadedFile) {
        if (e.isPopupTrigger()) {
            final JPopupMenu popup = new JPopupMenu();

            final ReceiveFileTransfer ui = this;
            Action saveAsAction = new AbstractAction() {
                private static final long serialVersionUID = -3010501340128285438L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    final JFileChooser chooser = Downloads.getFileChooser();
                    File selectedFile = chooser.getSelectedFile();
                    if (selectedFile != null) {
                        selectedFile = new File(selectedFile.getParent(), downloadedFile.getName());
                    } else {
                        selectedFile = downloadedFile;
                    }
                    chooser.setSelectedFile(selectedFile);

                    int ok = chooser.showSaveDialog(ui);
                    if (ok == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        try {
                            if (file.exists()) {
                                int confirm = JOptionPane.showConfirmDialog(ui, Res.getString("message.file.exists.question"), Res.getString("title.file.exists"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if (confirm == JOptionPane.NO_OPTION) {
                                    return;
                                }
                            }
                            URLFileSystem.copy(downloadedFile.toURI().toURL(), file);
                        } catch (IOException e1) {
                            Log.error("An exception occurred while selecting a location to save a received file.", e1);
                        }
                    }
                }
            };

            saveAsAction.putValue(Action.NAME, Res.getString("menuitem.save.as"));
            popup.add(saveAsAction);
            popup.show(this, e.getX(), e.getY());
        }
    }

    /**
     * Return correct URI for filePath. dont mind of local or remote path
     *
     * @param file to open
     * @return URI for the file.
     */
    private static URI getFileURI(File file) {
        URI uri = null;
        String filePath = file.getPath().trim();
        if (filePath.indexOf("http") == 0 || filePath.indexOf("\\") == 0) {
            if (filePath.indexOf("\\") == 0)
                filePath = "file:" + filePath;
            try {
                filePath = filePath.replaceAll(" ", "%20");
                URL url = new URL(filePath);
                uri = url.toURI();
            } catch (MalformedURLException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        } else {
            uri = new File(filePath).toURI();
        }
        return uri;
    }

    /**
     * Attempts to open the file. If no associated application can be found, or if that application fails to launch, or
     * if the provided file is a directory, a file browser that shows the content of the folder in which the file
     * resides is shown.
     *
     * @param file the file to be shown.
     */
    private void launchFile(File file) {
        if (!Desktop.isDesktopSupported()) {
            Log.warning("Cannot launch file (not supported in this environment).");
            return;
        }

        final Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException ex) {
            try {
                // Potentially trying to open on a network path that has spaces (SPARK-1350). Try again, using a URI.
                desktop.browse(getFileURI(file));
                return;
            } catch (Exception ex1) {
                // The specified file has no associated application or the associated application fails to be launched.
                // Show the folder containing the file as a last-ditch effort (SPARK-2199).
                if (file.isFile() && file.getParentFile() != null) {
                    try {
                        desktop.open(file.getParentFile());
                        return;
                    } catch (IOException ex2) {
                        // Log the original exception (see below)
                    }
                }
            }
            // In case of failure, log the original exception, which is likely to be most relevant.
            Log.warning("Unable to open file: " + file.getName(), ex);
        }
    }
}
