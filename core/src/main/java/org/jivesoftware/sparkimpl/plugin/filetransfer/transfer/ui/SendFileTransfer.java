/**
 * Copyright (C) 2004-2011 Jive Software. 2026 Ignite Realtime Foundation. All rights reserved.
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.*;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityFullJid;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;
import static org.jivesoftware.sparkimpl.settings.Sizes.Transfer.THUMBNAIL;

public class SendFileTransfer extends JPanel {
    private final FileDragLabel imageLabel = new FileDragLabel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel fileLabel = new JLabel();

    private final TransferButton cancelButton = new TransferButton();
    private final JProgressBar progressBar = new JProgressBar();
    private File fileToSend;
    private OutgoingFileTransfer transfer;

    private final TransferButton retryButton = new TransferButton();

    private FileTransferManager transferManager;
    private EntityFullJid fullJID;
    private String nickname;
    private final JLabel progressLabel = new JLabel();
    private long _startTime;
    private final ChatRoom chatRoom;

    public SendFileTransfer(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        setLayout(new GridBagLayout());
        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0, 0, NORTHWEST, NONE, new Insets(5, 5, 5, 5), 0, 0));

        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1, 0, NORTHWEST, NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1, 0, WEST, NONE, new Insets(0, 5, 5, 5), 0, 0));

        cancelButton.setText(Res.getString("cancel"));
        retryButton.setText(Res.getString("retry"));
        cancelButton.setIcon(SparkRes.getImageIcon(SparkRes.Icon.SMALL_DELETE));
        retryButton.setIcon(SparkRes.getImageIcon(SparkRes.Icon.REFRESH_IMAGE));

        add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 0, 0, WEST, NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(retryButton, new GridBagConstraints(1, 4, 1, 1, 0, 0, WEST, NONE, new Insets(0, 5, 0, 5), 0, 0));
        retryButton.setVisible(false);

        retryButton.addActionListener(e -> {
            try {
                transfer = transferManager.createOutgoingFileTransfer(fullJID);
                transfer.sendFile(fileToSend, "Sending");
            } catch (SmackException e1) {
                Log.error("An error occurred while creating an outgoing file transfer.", e1);
            }
            sendFile(transfer, transferManager, fullJID, nickname);
        });

        cancelButton.setForeground(new Color(73, 113, 196));
        cancelButton.setFont(new Font("Dialog", Font.BOLD, 11));
        cancelButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        retryButton.setForeground(new Color(73, 113, 196));
        retryButton.setFont(new Font("Dialog", Font.BOLD, 11));
        retryButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));
    }

    public void sendFile(final OutgoingFileTransfer transfer, FileTransferManager transferManager, final EntityFullJid jid, final String nickname) {
        this.transferManager = transferManager;
        //SPARK-1869
        FileTransferNegotiator.getInstanceFor(SparkManager.getConnection());
        FileTransferNegotiator.IBB_ONLY = SettingsManager.getLocalPreferences().isFileTransferIbbOnly();

        cancelButton.setVisible(true);
        retryButton.setVisible(false);
        this.fullJID = jid;
        this.nickname = nickname;
        final BareJid bareJid = jid.asBareJid();

        this.transfer = transfer;
        String fileName = transfer.getFileName();
        String filePath = transfer.getFilePath();
        long fileSize = transfer.getFileSize();
        ByteFormat format = new ByteFormat();
        String fileSizeString = format.format(fileSize);

        fileToSend = new File(filePath);
        imageLabel.setFile(fileToSend);
        imageLabel.setIcon(getFileIcon());

        fileLabel.setText(fileName + " (" + fileSizeString + ")");

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(jid);

        saveEventToHistory(bareJid, Res.getString("message.file.transfer.history.request.sent", filePath, fileSizeString, nickname));
        titleLabel.setText(Res.getString("message.transfer.waiting.on.user", contactItem.getDisplayName()));

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                transfer.cancel();
            }
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

        progressBar.setMaximum(100);
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        Insets progressInsets = new Insets(0, 5, 0, 5);
        add(progressBar, new GridBagConstraints(1, 2, 2, 1, 1, 0, WEST, NONE, progressInsets, 150, 0));
        add(progressLabel, new GridBagConstraints(1, 3, 2, 1, 1, 0, WEST, NONE, progressInsets, 150, 0));

        SwingWorker worker = new SwingWorker() {
            @Override
            public Object construct() {
                while (true) {
                    try {
                        if (transfer.getBytesSent() > 0 && _startTime == 0) {
                            _startTime = System.currentTimeMillis();
                        }
                        long startTime = System.currentTimeMillis();
                        long startByte = transfer.getBytesSent();
                        Thread.sleep(500);
                        FileTransfer.Status status = transfer.getStatus();
                        if (status == Status.complete) {
                            saveEventToHistory(bareJid, Res.getString("message.file.transfer.history.send.complete", filePath, nickname));
                            break;
                        } else if (status == Status.error) {
                            saveEventToHistory(bareJid, Res.getString("message.file.transfer.history.send.error", filePath, nickname));
                            break;
                        } else if (status == Status.cancelled) {
                            saveEventToHistory(bareJid, Res.getString("message.file.transfer.history.send.canceled", filePath, nickname));
                            break;
                        } else if (status == Status.refused) {
                            saveEventToHistory(bareJid, Res.getString("message.file.transfer.history.contact.rejected", filePath, nickname));
                            break;
                        }
                        long endTime = System.currentTimeMillis();
                        long endByte = transfer.getBytesSent();

                        long timeDiff = endTime - startTime;
                        long byteDiff = endByte - startByte;

                        updateBar(transfer, nickname, TransferUtils.calculateSpeed(byteDiff, timeDiff));
                    } catch (InterruptedException e) {
                        Log.error("Unable to sleep thread.", e);
                    }
                }
                return "";
            }

            @Override
            public void finished() {
                updateBar(transfer, nickname, "??MB/s");
            }
        };

        worker.start();

        makeClickable(imageLabel);
        makeClickable(titleLabel);
    }

    private Icon getFileIcon() {
        if (isImage(fileToSend.getName())) {
            try {
                URL imageURL = fileToSend.toURI().toURL();
                ImageIcon image = new ImageIcon(imageURL);
                image = GraphicUtils.scaleImageIcon(image, THUMBNAIL, THUMBNAIL);
                return image;
            } catch (Exception e) {
                Log.warning("Could not make thumbnail for image.", e);
            }
        }
        Icon icon = GraphicUtils.getIcon(fileToSend);
        return icon;
    }

    private void makeClickable(final JLabel label) {
        label.setToolTipText(Res.getString("message.click.to.open"));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserLauncher.openInFileManager(fileToSend);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void updateBar(final OutgoingFileTransfer transfer, String nickname, String kBperSecond) {
        FileTransfer.Status status = transfer.getStatus();
        switch (status) {
            case negotiating_stream: {
                titleLabel.setText(Res.getString("message.negotiation.file.transfer", nickname));
                break;
            }
            case error: {
                if (transfer.getException() != null) {
                    Log.error("Error occurred during file transfer.", transfer.getException());
                }
                progressBar.setVisible(false);
                progressLabel.setVisible(false);
                titleLabel.setText(Res.getString("message.unable.to.send.file", nickname));
                cancelButton.setVisible(false);
                retryButton.setVisible(true);
                showAlert(true);
                break;
            }
            case in_progress: {
                titleLabel.setText(Res.getString("message.sending.file.to", nickname));
                showAlert(false);
                // skip showing progress for small transfers
                if (transfer.getFileSize() <= 10 * 1024) {
                    return;
                }
                if (!progressBar.isVisible()) {
                    progressBar.setVisible(true);
                    progressLabel.setVisible(true);
                }
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        long p = transfer.getFileSize() > 0 ? transfer.getBytesSent() * 100 / transfer.getFileSize() : 100;
                        progressBar.setValue((int) p);
                    });
                } catch (Exception e) {
                    Log.error("An error occurred while trying to update the file transfer progress bar.", e);
                }

                ByteFormat format = new ByteFormat();
                String bytesSent = format.format(transfer.getBytesSent());
                String est = TransferUtils.calculateEstimate(transfer.getBytesSent(), transfer.getFileSize(), _startTime, System.currentTimeMillis());
                progressLabel.setText(Res.getString("message.transfer.progressbar.text.sent", bytesSent, kBperSecond, est));
                break;
            }
            case complete: {
                progressBar.setVisible(false);
                if (_startTime == 0) { // SPARK-2192: Sometimes, the startTime of the transfer hasn't been recorded yet when it already finished.
                    _startTime = System.currentTimeMillis();
                }
                String fin = TransferUtils.convertSecondstoHHMMSS(Math.round(Math.max(0, System.currentTimeMillis() - _startTime)) / 1000);
                _startTime = 0;
                progressLabel.setText(Res.getString("label.time", fin));
                titleLabel.setText(Res.getString("message.you.have.sent", nickname));
                cancelButton.setVisible(false);
                showAlert(true);
                break;
            }
            case cancelled: {
                progressBar.setVisible(false);
                progressLabel.setVisible(false);
                titleLabel.setText(Res.getString("message.file.transfer.canceled"));
                cancelButton.setVisible(false);
                retryButton.setVisible(true);
                showAlert(true);
                break;
            }
            case refused: {
                progressBar.setVisible(false);
                progressLabel.setVisible(false);
                titleLabel.setText(Res.getString("message.file.transfer.rejected", nickname));
                cancelButton.setVisible(false);
                retryButton.setVisible(true);
                showAlert(true);
                break;
            }
            case initial: {
                break;
            }
            case negotiating_transfer: {
                break;
            }
            case negotiated: {
                break;
            }
        }
    }

    /***
     * Adds an event text as a message to transcript and saves it to history
     * @param bareJid receiver JID
     * @param eventText Contains file transfer event text
     */
    private void saveEventToHistory(BareJid bareJid, String eventText) {
        MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
            .setBody(eventText)
            .to(bareJid)
            .from(SparkManager.getSessionManager().getJID());
        Message message = messageBuilder.build();
        chatRoom.addToTranscript(message, false);
        SparkManager.getWorkspace().getTranscriptPlugin().persistChatRoom(chatRoom);
    }

    private static class TransferButton extends JButton {
        public TransferButton() {
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
}
