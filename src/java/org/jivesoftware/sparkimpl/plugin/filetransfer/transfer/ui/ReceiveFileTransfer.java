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
package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Timer;
import java.util.TimerTask;

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
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.FileDragLabel;
import org.jivesoftware.spark.filetransfer.preferences.FileTransferPreference;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.Downloads;

public class ReceiveFileTransfer extends JPanel {

    private static final long serialVersionUID = -2974192409566650923L;
    private FileDragLabel imageLabel = new FileDragLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel fileLabel = new JLabel();

    private TransferButton acceptButton = new TransferButton(Res.getString("accept"), SparkRes.getImageIcon(SparkRes.ACCEPT_INVITE_IMAGE));
    private TransferButton declineButton = new TransferButton(Res.getString("reject"), SparkRes.getImageIcon(SparkRes.REJECT_INVITE_IMAGE));
    private TransferButton pathButton = new TransferButton(Res.getString("message.file.transfer.direrror.setdir"), SparkRes.getImageIcon(SparkRes.SETTINGS_IMAGE_16x16));
    private JProgressBar progressBar = new JProgressBar();
    private IncomingFileTransfer transfer;
    private TransferButton cancelButton = new TransferButton();
    private JLabel progressLabel = new JLabel();
    private long bytesRead;
    private long _starttime;
    private long _endtime;

    public ReceiveFileTransfer() {
        setLayout(new GridBagLayout());

        setBackground(new Color(250, 249, 242));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(titleLabel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        titleLabel.setForeground(new Color(211, 174, 102));
        add(fileLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        add(acceptButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(pathButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        pathButton.setVisible( false );
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
            public void mouseEntered(MouseEvent e) {
                pathButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                pathButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        acceptButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                acceptButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                acceptButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        declineButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                declineButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent e) {
                declineButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
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

        titleLabel.setText(Res.getString("message.user.is.sending.you.a.file", contactItem.getDisplayName()));

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
        }
        catch (IOException e) {
            imageLabel.setIcon(SparkRes.getImageIcon(SparkRes.DOCUMENT_INFO_32x32));
            Log.error(e);
        }
        
        acceptButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
            	try{
            		Downloads.checkDownloadDirectory();
            		acceptRequest(request);
            	}catch (Exception ex){
                    // this means there is a problem with the download directory
            	    request.reject();
                    
                    setBackground(new Color(239, 245, 250));
                    acceptButton.setVisible(false);
                    declineButton.setVisible(false);
                    if (Downloads.getDownloadDirectory() == null){
                        fileLabel.setText(""); 
                    }else{
                        ResourceUtils.resLabel( fileLabel, null, Res.getString("label.transfer.download.directory") + 
                                " " + Downloads.getDownloadDirectory().getAbsolutePath() );      
                    }

                    // option to set a new path for the file-download
                    pathButton.setVisible( true );
                    pathButton.addMouseListener( new MouseAdapter() { 
                        public void mousePressed(MouseEvent e) {
                            Preference p = SparkManager.getPreferenceManager().getPreference( 
                                    new FileTransferPreference().getNamespace() );
                            // retrieve the filetransfer preferences and show the preference menu
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
        });

        declineButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                rejectRequest(request);
            }
        });

    }

    private void rejectRequest(FileTransferRequest request) {
        request.reject();

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
	String requestor = request.getRequestor();
	String bareJID = StringUtils.parseBareAddress(requestor);

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
	final File downloadedFile = new File(Downloads.getDownloadDirectory(), request.getFileName());
	
	try {
	    _starttime = System.currentTimeMillis();
            transfer.recieveFile(downloadedFile);
        }
        catch (XMPPException e) {
            Log.error(e);
        }
	
        progressBar.setMaximum(100); // setting it to percent
        progressBar.setStringPainted(true);


	final Timer timer = new Timer();
	TimerTask updateProgessBar = new TimerTask() {
	    @Override
	    public void run() {
		if (transfer.getAmountWritten() >= request.getFileSize() 
			|| transfer.getStatus() == FileTransfer.Status.error 
			|| transfer.getStatus() == FileTransfer.Status.refused
			|| transfer.getStatus() == FileTransfer.Status.cancelled
			|| transfer.getStatus() == FileTransfer.Status.complete) 
		{
		    this.cancel();
		    timer.cancel();
		    _endtime = System.currentTimeMillis();
		    updateonFinished(request, downloadedFile);
		}else
		{
		    // 100 % = Filesize
		    // x %   = Currentsize	    
		    long p = (transfer.getAmountWritten() * 100 / transfer.getFileSize() );
		    progressBar.setValue(Math.round(p));        
		}
		
	    }
	};
	
	final Timer timer2 = new Timer();
	TimerTask updatePrograssBarText = new TimerTask() {
	long timenow;
	long timeearlier;
	long bytesnow;
	long bytesearlier;
	    @Override
	    public void run() {
		if (transfer.getAmountWritten() >= request.getFileSize() 
			|| transfer.getStatus() == FileTransfer.Status.error 
			|| transfer.getStatus() == FileTransfer.Status.refused
			|| transfer.getStatus() == FileTransfer.Status.cancelled
			|| transfer.getStatus() == FileTransfer.Status.complete) 
		{
		    this.cancel();
		    timer2.cancel();
		}
		else{
    		
            	timenow = System.currentTimeMillis();
            	bytesnow = transfer.getAmountWritten();
            	bytesRead = transfer.getAmountWritten();
                    if (bytesRead == -1) {
                        bytesRead = 0;
                    }
                    ByteFormat format = new ByteFormat();
                    String text = format.format(bytesRead);
                    
                    
                                    
                    	
                    FileTransfer.Status status = transfer.getStatus();
                    if (status == FileTransfer.Status.in_progress) 
                    {
                        titleLabel.setText(Res.getString("message.receiving.file", contactItem.getDisplayName()));
                        String speed =TransferUtils.calculateSpeed(bytesnow-bytesearlier, timenow-timeearlier);
                        String est = TransferUtils.calculateEstimate(bytesnow, transfer.getFileSize(), _starttime, System.currentTimeMillis());
                        progressLabel.setText(Res.getString("message.transfer.progressbar.text.received", text, speed, est));
                    }
                    else if (status == FileTransfer.Status.negotiating_stream) {
                        titleLabel.setText(Res.getString("message.negotiate.stream"));
                    }
                    bytesearlier = bytesnow;
                    timeearlier = timenow;
		}
	    }
	};
	
	
	
	timer.scheduleAtFixedRate(updateProgessBar, 10, 10);
	timer2.scheduleAtFixedRate(updatePrograssBarText, 10, 500);
	
    }

    private void updateonFinished(final FileTransferRequest request,
	    final File downloadedFile) {
	if (transfer.getAmountWritten() >= request.getFileSize()) {
            transferDone(request, transfer);

            imageLabel.setFile(downloadedFile);
            imageLabel.setToolTipText(Res.getString("message.click.to.open"));
            titleLabel.setToolTipText(Res.getString("message.click.to.open"));
            String fin = TransferUtils.convertSecondstoHHMMSS(Math.round(_endtime-_starttime)/1000);
            progressLabel.setText(Res.getString("label.time", fin));

            imageLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        launchFile(downloadedFile);
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
                        launchFile(downloadedFile);
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
        }
        else if (transfer.getStatus() == FileTransfer.Status.refused) {
            transferMessage = Res.getString("message.transfer.refused");
        }
        else if (transfer.getStatus() == FileTransfer.Status.cancelled ||
            transfer.getAmountWritten() < request.getFileSize()) {
            transferMessage = Res.getString("message.transfer.cancelled");
        }
        else if(transfer.getAmountWritten() >= request.getFileSize())
        {
            transferMessage = Res.getString("message.transfer.complete", transfer.getFileName());
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

    private void transferDone(final FileTransferRequest request, FileTransfer transfer) {
        cancelButton.setVisible(false);

        showAlert(true);

        String bareJID = StringUtils.parseBareAddress(request.getRequestor());

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        ContactItem contactItem = contactList.getContactItemByJID(bareJID);

        titleLabel.setText(Res.getString("message.received.file", contactItem.getDisplayName()));
        fileLabel.setText(request.getFileName());

        remove(acceptButton);
        remove(declineButton);
        remove(progressBar);
        remove(pathButton);


        final TransferButton openFileButton = new TransferButton();
        final TransferButton openFolderButton = new TransferButton();
        add(openFileButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(openFolderButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        
        
        final File downloadedFile = new File(Downloads.getDownloadDirectory(), request.getFileName());     
        openFileButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                openFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                openFileButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            public void mousePressed(MouseEvent e) {
                launchFile(Downloads.getDownloadDirectory()+"\\"+request.getFileName());
            }
        });

        openFolderButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                openFolderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            }

            public void mouseExited(MouseEvent e) {
                openFolderButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

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
            public void mousePressed(MouseEvent e) {
                showPopup(e, downloadedFile);
            }

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

  

    private class TransferButton extends JButton {

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
        cancelButton.setIcon(SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelTransfer();
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
                cancelButton.setVisible(false);
                
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

            final ReceiveFileTransfer ui = this;
            Action saveAsAction = new AbstractAction() {
				private static final long serialVersionUID = -3010501340128285438L;

				public void actionPerformed(ActionEvent e) {
                    final JFileChooser chooser = Downloads.getFileChooser();
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
                            URLFileSystem.copy(downloadedFile.toURI().toURL(), file);
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
}
