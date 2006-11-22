/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.themes.ThemeManager;
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.Downloads;

import javax.imageio.ImageIO;
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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ReceiveMessage extends JPanel {
    private JLabel imageLabel = new JLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel fileLabel = new JLabel();

    private TransferButton acceptLabel = new TransferButton();
    private TransferButton declineLabel = new TransferButton();
    private JProgressBar progressBar = new JProgressBar();
    private IncomingFileTransfer transfer;
    private TransferButton cancelButton = new TransferButton();

    private ChatRoom chatRoom;


    public ReceiveMessage() {
        setLayout(new GridBagLayout());

        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        add(acceptLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        add(declineLabel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));


        ResourceUtils.resButton(acceptLabel, Res.getString("accept"));
        ResourceUtils.resButton(declineLabel, Res.getString("reject"));

        // Decorate Cancel Button
        decorateCancelButton();


        acceptLabel.setForeground(new Color(73, 113, 196));
        declineLabel.setForeground(new Color(73, 113, 196));
        declineLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        acceptLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        acceptLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));
        declineLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(73, 113, 196)));


        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));

        acceptLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                acceptLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                acceptLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        declineLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                declineLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent e) {
                declineLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void acceptFileTransfer(final FileTransferRequest request) {
        String fileName = request.getFileName();
        long fileSize = request.getFileSize();
        String requestor = request.getRequestor();
        String bareJID = StringUtils.parseBareAddress(requestor);

        ByteFormat format = new ByteFormat();
        String text = format.format(fileSize);

        fileLabel.setText(fileName + " (" + text + ")");

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(bareJID);

        titleLabel.setText(Res.getString("message.user.is.sending.you.a.file", contactItem.getNickname()));

        File tempFile = new File(Spark.getUserHome(), "Spark/tmp");
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
        }
        catch (IOException e) {
            imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
            Log.error(e);
        }


        acceptLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                acceptRequest(request);
            }


        });

        declineLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                rejectRequest(request);
            }
        });

    }

    private void rejectRequest(FileTransferRequest request) {
        request.reject();

        this.setVisible(true);
        final TranscriptWindow window = chatRoom.getTranscriptWindow();
        window.remove(this);

        String message = ThemeManager.getInstance().getStatusMessage(Res.getString("message.file.transfer.canceled"), "");
        window.insertHTML(message);

        setBackground(new Color(239, 245, 250));
        acceptLabel.setText("");
        declineLabel.setText("");
        fileLabel.setText("");
        titleLabel.setText(Res.getString("message.file.transfer.canceled"));
        titleLabel.setForeground(new Color(65, 139, 179));

        window.invalidate();
        window.validate();
        window.repaint();
    }

    private void acceptRequest(final FileTransferRequest request) {
        String requestor = request.getRequestor();
        String bareJID = StringUtils.parseBareAddress(requestor);

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        final ContactItem contactItem = contactList.getContactItemByJID(bareJID);

        setBackground(new Color(239, 245, 250));
        acceptLabel.setText("");
        declineLabel.setText("");
        titleLabel.setText(Res.getString("message.negotiate.file.transfer"));
        titleLabel.setForeground(new Color(65, 139, 179));


        add(progressBar, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 150, 0));
        add(cancelButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        cancelButton.setVisible(true);
        transfer = request.accept();
        try {


            Downloads downloads = Downloads.getInstance();
            final File downloadedFile = new File(downloads.getDownloadDirectory(), request.getFileName());


            progressBar.setMaximum((int)request.getFileSize());
            progressBar.setStringPainted(true);

            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    try {
                        transfer.recieveFile(downloadedFile);
                    }
                    catch (XMPPException e) {
                        Log.error(e);
                    }

                    while (true) {

                        try {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException e) {
                            Log.error(e);
                        }

                        long bytesRead = transfer.getAmountWritten();
                        if (bytesRead == -1) {
                            bytesRead = 0;
                        }
                        ByteFormat format = new ByteFormat();
                        String text = format.format(bytesRead);
                        progressBar.setString(text + " received");

                        progressBar.setValue((int)bytesRead);
                        FileTransfer.Status status = transfer.getStatus();
                        if (status == FileTransfer.Status.error ||
                                status == FileTransfer.Status.complete || status == FileTransfer.Status.cancelled ||
                                status == FileTransfer.Status.refused) {
                            break;
                        }
                        else if (status == FileTransfer.Status.negotiating_stream) {
                            titleLabel.setText(Res.getString("message.negotiate.stream"));
                        }
                        else if (status == FileTransfer.Status.in_progress) {
                            titleLabel.setText(Res.getString("message.receiving.file", contactItem.getNickname()));
                        }
                    }

                    return "ok";
                }

                public void finished() {
                    if (transfer.getAmountWritten() >= request.getFileSize()) {
                        //transferDone(request, transfer);

                        imageLabel.setToolTipText(Res.getString("message.click.to.open"));
                        titleLabel.setToolTipText(Res.getString("message.click.to.open"));

                        imageLabel.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    openFile(downloadedFile);
                                }
                            }
                        });

                        imageLabel.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent e) {
                                imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                            }

                            public void mouseExited(MouseEvent e) {
                                imageLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        });

                        titleLabel.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    openFile(downloadedFile);
                                }
                            }
                        });

                        titleLabel.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent e) {
                                titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                            }

                            public void mouseExited(MouseEvent e) {
                                titleLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                            }
                        });

                        TranscriptWindow window = chatRoom.getTranscriptWindow();

                        try {
                            String message = ThemeManager.getInstance().getNotificationMessage(getFinishedText(titleLabel.getText(), downloadedFile), true);
                            System.out.println(message);
                            window.insertHTML(message);
                            setVisible(false);
                            window.invalidate();
                            window.validate();
                            window.repaint();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    String transferMessage = "";
                    if (transfer.getStatus() == FileTransfer.Status.error) {
                        if (transfer.getException() != null) {
                            Log.error("There was an error during file transfer.", transfer.getException());
                        }
                        transferMessage = Res.getString("message.error.during.file.transfer");
                    }
                    else if (transfer.getStatus() == FileTransfer.Status.refused) {
                        transferMessage = Res.getString("message.transfer.refused");
                    }
                    else if (transfer.getStatus() == FileTransfer.Status.cancelled ||
                            transfer.getAmountWritten() < request.getFileSize()) {
                        transferMessage = Res.getString("message.transfer.cancelled");
                    }

                    setFinishedText(transferMessage);
                    showAlert(true);
                }
            };

            worker.start();

        }
        catch (Exception e) {
            Log.error(e);
        }


    }

    private void setFinishedText(String text) {
        acceptLabel.setText("");
        declineLabel.setText("");
        fileLabel.setText("");
        titleLabel.setText(text);
        titleLabel.setForeground(new Color(65, 139, 179));
        progressBar.setVisible(false);
        cancelButton.setVisible(false);
        invalidate();
        validate();
        repaint();
    }

    private void transferDone(final FileTransferRequest request, FileTransfer transfer) {
        cancelButton.setVisible(false);

        showAlert(true);

        String bareJID = StringUtils.parseBareAddress(request.getRequestor());

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(bareJID);

        titleLabel.setText(Res.getString("message.received.file", contactItem.getNickname()));
        fileLabel.setText(request.getFileName());

        remove(acceptLabel);
        remove(declineLabel);
        remove(progressBar);


        final TransferButton openFileButton = new TransferButton();
        final TransferButton openFolderButton = new TransferButton();
        add(openFileButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(openFolderButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

        Downloads downloads = Downloads.getInstance();
        final File downloadedFile = new File(downloads.getDownloadDirectory(), request.getFileName());

        openFileButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                openFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                openFileButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        openFolderButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                openFolderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                openFolderButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
            public void mousePressed(MouseEvent e) {
                showPopup(e, downloadedFile);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e, downloadedFile);
            }
        });

        openFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                openFile(downloadedFile);
            }
        });

        openFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Downloads downloads = Downloads.getInstance();
                    if (!Spark.isMac()) {
                        try {
                            Desktop.open(downloads.getDownloadDirectory());
                        }
                        catch (DesktopException e) {
                            Log.error(e);
                        }
                    }
                    else if (Spark.isMac()) {
                        Runtime.getRuntime().exec("open " + downloads.getDownloadDirectory().getCanonicalPath());
                    }
                }
                catch (IOException e1) {
                    Log.error(e1);
                }
            }
        });

        if (isImage(downloadedFile.getName())) {
            try {
                URL imageURL = downloadedFile.toURL();
                ImageIcon image = new ImageIcon(imageURL);
                image = GraphicUtils.scaleImageIcon(image, 64, 64);
                imageLabel.setIcon(image);
            }
            catch (MalformedURLException e) {
                Log.error("Could not locate image.", e);
                imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
            }
        }

        invalidate();
        validate();
        repaint();
    }

    private void openFile(File downloadedFile) {
        try {
            if (!Spark.isMac()) {
                try {
                    Desktop.open(downloadedFile);
                }
                catch (DesktopException e) {
                    Log.error(e);
                }
            }
            else if (Spark.isMac()) {
                Process child = Runtime.getRuntime().exec("open " + downloadedFile.getCanonicalPath());
            }
        }
        catch (IOException e1) {
            Log.error(e1);
        }
    }

    private class TransferButton extends JButton {

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
        for (int i = 0; i < imageTypes.length; i++) {
            if (fileName.endsWith(imageTypes[i])) {
                return true;
            }
        }

        return false;
    }

    private void showAlert(boolean alert) {
        if (alert) {
            titleLabel.setForeground(new Color(211, 174, 102));
            setBackground(new Color(250, 249, 242));
        }
        else {
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

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelTransfer();
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                cancelButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

    }

    private void showPopup(MouseEvent e, final File downloadedFile) {
        if (e.isPopupTrigger()) {
            final JPopupMenu popup = new JPopupMenu();

            final ReceiveMessage ui = this;
            Action saveAsAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    final JFileChooser chooser = Downloads.getInstance().getFileChooser();
                    File selectedFile = chooser.getSelectedFile();
                    if (selectedFile != null) {
                        selectedFile = new File(selectedFile.getParent(), downloadedFile.getName());
                    }
                    else {
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
                            URLFileSystem.copy(downloadedFile.toURL(), file);
                        }
                        catch (IOException e1) {
                            Log.error(e1);
                        }
                    }
                }
            };

            saveAsAction.putValue(Action.NAME, Res.getString("menuitem.save.as"));
            popup.add(saveAsAction);
            popup.show(this, e.getX(), e.getY());
        }
    }


    private String getFinishedText(String title, File file) throws Exception {
        final StringBuilder builder = new StringBuilder();
        BufferedImage image = GraphicUtils.getBufferedImage(file);
        File f = new File("c:\\test.png");
        try {
            ImageIO.write(image, "PNG", f);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String iconURL = f.toURL().toString();
        String fileURL = file.toURL().toString();
        String folderURL = file.getParentFile().toURL().toString();
        builder.append("<table width=\"100%\">" +
                "    <tr>" +
                "     <td><img src=\"" + iconURL + "\"></td>" +
                "        <td>" +
                "            " + title + "" +
                "        </td>" +
                "    </tr>" +
                "    <tr>" +
                "        <td colspan=\"2\">" +
                "           " + file.getName() + "" +
                "        </td>" +
                "    </tr>" +
                "    <tr>" +
                "        <td width=\"5%\">" +
                "            <a href=\"" + fileURL + "\" target=\"_blank\">Open</a>" +
                "        </td>" +
                "        <td align=\"left\">" +
                "            <a href=\"" + folderURL + "\" target=\"_blank\">Open Folder</a>" +
                "        </td>" +
                "    </tr>" +
                "</table>");

        return builder.toString();
    }
}
