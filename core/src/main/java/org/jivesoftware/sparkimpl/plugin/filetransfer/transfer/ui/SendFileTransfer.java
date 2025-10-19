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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

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
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.stringprep.XmppStringprepException;

public class SendFileTransfer extends JPanel {

    private static final long serialVersionUID = -4403839897649365671L;
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
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        cancelButton.setText(Res.getString("cancel"));
        retryButton.setText(Res.getString("retry"));
        cancelButton.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));
        retryButton.setIcon(SparkRes.getImageIcon(SparkRes.REFRESH_IMAGE));

        add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(retryButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        retryButton.setVisible(false);

        retryButton.addActionListener(e -> {
            try {
                File file = new File(transfer.getFilePath());
                transfer = transferManager.createOutgoingFileTransfer(fullJID);
                transfer.sendFile(file, "Sending");
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

        fileToSend = new File(transfer.getFilePath());
        imageLabel.setFile(fileToSend);

        fileLabel.setText(fileName + " (" + fileSizeString + ")");

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(jid);

        saveEventToHistory(bareJid, Res.getString("message.file.transfer.history.request.sent", filePath, fileSizeString, nickname));
        titleLabel.setText(Res.getString("message.transfer.waiting.on.user", contactItem.getDisplayName()));

        if (isImage(fileName)) {
            try {
                URL imageURL = new File(transfer.getFilePath()).toURI().toURL();
                ImageIcon image = new ImageIcon(imageURL);
                image = GraphicUtils.scaleImageIcon(image, 64, 64);
                imageLabel.setIcon(image);
            } catch (MalformedURLException e) {
                Log.error("Could not locate image.", e);
                imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
            }
        } else {
            File file = new File(transfer.getFilePath());
            Icon icon = GraphicUtils.getIcon(file);
            imageLabel.setIcon(icon);
        }
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
        add(progressBar, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));
        add(progressLabel, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));

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

    private void makeClickable(final JLabel label) {
        label.setToolTipText(Res.getString("message.click.to.open"));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openFile(fileToSend);
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

    private void openFile(File downloadedFile) {
        try {
            Desktop.getDesktop().open(downloadedFile);
        } catch (IOException e) {
            Log.error("An error occurred while trying to open downloaded file: " + downloadedFile, e);
        }
    }

    private void updateBar(final OutgoingFileTransfer transfer, String nickname, String kBperSecond) {
        FileTransfer.Status status = transfer.getStatus();
        if (status == Status.negotiating_stream) {
            titleLabel.setText(Res.getString("message.negotiation.file.transfer", nickname));
        } else if (status == Status.error) {
            if (transfer.getException() != null) {
                Log.error("Error occurred during file transfer.", transfer.getException());
            }
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            titleLabel.setText(Res.getString("message.unable.to.send.file", nickname));
            cancelButton.setVisible(false);
            retryButton.setVisible(true);
            showAlert(true);
        } else if (status == Status.in_progress) {
            titleLabel.setText(Res.getString("message.sending.file.to", nickname));
            showAlert(false);
            if (!progressBar.isVisible()) {
                progressBar.setVisible(true);
                progressLabel.setVisible(true);
            }

            try {
                SwingUtilities.invokeAndWait(() -> {
                    // 100 % = Filesize
                    // x %   = Currentsize
                    long p = (transfer.getBytesSent() * 100 / transfer.getFileSize());
                    progressBar.setValue(Math.round(p));
                });
            } catch (Exception e) {
                Log.error("An error occurred while trying to update the file transfer progress bar.", e);
            }

            ByteFormat format = new ByteFormat();
            String bytesSent = format.format(transfer.getBytesSent());
            String est = TransferUtils.calculateEstimate(transfer.getBytesSent(), transfer.getFileSize(), _startTime, System.currentTimeMillis());

            progressLabel.setText(Res.getString("message.transfer.progressbar.text.sent", bytesSent, kBperSecond, est));
        } else if (status == Status.complete) {
            progressBar.setVisible(false);

            if ( _startTime == 0 ) { // SPARK-2192: Sometimes, the startTime of the transfer hasn't been recorded yet when it already finished.
                _startTime = System.currentTimeMillis();
            }
            String fin = TransferUtils.convertSecondstoHHMMSS(Math.round(Math.max(0, System.currentTimeMillis() - _startTime)) / 1000);
            _startTime = 0;
            progressLabel.setText(Res.getString("label.time", fin));
            titleLabel.setText(Res.getString("message.you.have.sent", nickname));
            cancelButton.setVisible(false);
            showAlert(true);
        } else if (status == Status.cancelled) {
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            titleLabel.setText(Res.getString("message.file.transfer.canceled"));
            cancelButton.setVisible(false);
            retryButton.setVisible(true);
            showAlert(true);
        } else if (status == Status.refused) {
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            titleLabel.setText(Res.getString("message.file.transfer.rejected", nickname));
            cancelButton.setVisible(false);
            retryButton.setVisible(true);
            showAlert(true);
        }
    }

    /***
     * Adds an event text as a message to transcript and saves it to history
     * @param bareJid receiver JID
     * @param eventText Contains file transfer event text
     */
    private void saveEventToHistory(BareJid bareJid, String eventText) {
        MessageBuilder messageBuilder = StanzaBuilder.buildMessage().
            setBody(eventText);
        Message message = messageBuilder.build();
        message.setTo(bareJid);
        message.setFrom(SparkManager.getSessionManager().getJID());
        chatRoom.addToTranscript(message, false);
        SparkManager.getWorkspace().getTranscriptPlugin().persistChatRoom(chatRoom);
    }

    private static class TransferButton extends JButton {
        private static final long serialVersionUID = 8807434179541503654L;

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
