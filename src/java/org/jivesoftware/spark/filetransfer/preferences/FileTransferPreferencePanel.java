/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.filetransfer.preferences;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.WindowsFileSystemView;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 */
public class FileTransferPreferencePanel extends JPanel {

    private JTextField timeoutField;
    private JTextField downloadDirectoryField;

    private JFileChooser fc;


    public FileTransferPreferencePanel() {
        setLayout(new GridBagLayout());

        timeoutField = new JTextField();
        downloadDirectoryField = new JTextField();

        JLabel timeoutLabel = new JLabel();
        JLabel downloadDirectoryLabel = new JLabel();

        ResourceUtils.resLabel(timeoutLabel, timeoutField, "&Transfer Timeout(min):");
        ResourceUtils.resLabel(downloadDirectoryLabel, downloadDirectoryField, "&Download Directory:");

        add(timeoutLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(timeoutField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));

        add(downloadDirectoryLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(downloadDirectoryField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        final JButton button = new JButton();
        ResourceUtils.resButton(button, "&Browse...");
        add(button, new GridBagConstraints(2, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pickFile("Choose Directory", downloadDirectoryField);
            }
        });

    }

    public void setTimeout(String minutes) {
        timeoutField.setText(minutes);
    }

    public String getTimeout() {
        return timeoutField.getText();
    }

    public void setDownloadDirectory(String dir) {
        downloadDirectoryField.setText(dir);
    }

    public String getDownloadDirectory() {
        return downloadDirectoryField.getText();
    }

    private void pickFile(String title, JTextField field) {
        if (fc == null) {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (Spark.isWindows()) {
                fc.setFileSystemView(new WindowsFileSystemView());
            }
        }
        fc.setDialogTitle(title);
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            field.setText(file.getAbsolutePath());
        }
        else {

        }
    }

}
