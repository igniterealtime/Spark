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
package org.jivesoftware.sparkimpl.plugin.fileupload;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui.TransferUtils;
import org.jivesoftware.sparkimpl.settings.Sizes;
import org.jxmpp.jid.EntityFullJid;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;

public class FileUploadChatComponent extends JPanel {
    private final FileDragLabel imageLabel = new FileDragLabel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel fileLabel = new JLabel();

    private final JProgressBar progressBar = new JProgressBar();
    private File fileToSend;
    private final JLabel progressLabel = new JLabel();
    private long _startTime;

    public FileUploadChatComponent() {
        setLayout(new GridBagLayout());
        setBackground(new Color(250, 249, 242));
        Insets insets = new Insets(5, 5, 5, 5);
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0, 0, NORTHWEST, NONE, insets, 0, 0));

        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1, 0, NORTHWEST, NONE, insets, 0, 0));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.WEST, NONE, new Insets(0, 5, 5, 5), 0, 0));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));
    }

    public void sendFile(final OutgoingFileTransfer transfer, final EntityFullJid jid, final String nickname) {
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

        titleLabel.setText(Res.getString("message.transfer.waiting.on.user", contactItem.getDisplayName()));

        if (isImage(fileName)) {
            try {
                URL imageURL = new File(transfer.getFilePath()).toURI().toURL();
                ImageIcon image = new ImageIcon(imageURL);
                image = GraphicUtils.scaleImageIcon(image, Sizes.Transfer.THUMBNAIL, Sizes.Transfer.THUMBNAIL);
                imageLabel.setIcon(image);
            } catch (MalformedURLException e) {
                Log.error("Could not locate image.", e);
                imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.Icon.DOCUMENT_INFO_32x32));
            }
        } else {
            File file = new File(transfer.getFilePath());
            Icon icon = GraphicUtils.getIcon(file);
            imageLabel.setIcon(icon);
        }

        progressBar.setMaximum(100);
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        add(progressBar, new GridBagConstraints(1, 2, 2, 1, 1, 0, GridBagConstraints.WEST, NONE, new Insets(0, 5, 0, 5), 150, 0));
        add(progressLabel, new GridBagConstraints(1, 3, 2, 1, 1, 0, GridBagConstraints.WEST, NONE, new Insets(0, 5, 0, 5), 150, 0));

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
                        long endTime = System.currentTimeMillis();
                        long endByte = transfer.getBytesSent();

                        long timeDiff = endTime - startTime;
                        long byteDiff = endByte - startByte;

                        updateBar(transfer, nickname, TransferUtils.calculateSpeed(byteDiff, timeDiff));
                    } catch (InterruptedException e) {
                        Log.error("Unable to sleep thread.", e);
                        return null;
                    }
                }
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

    }


    private static class TransferButton extends JButton {
        public TransferButton() {
            decorate();
        }

        /**
         * Decorates the button with the appropriate UI configurations.
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
}
