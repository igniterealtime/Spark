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
package org.jivesoftware.spark.filetransfer.preferences;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.WindowsFileSystemView;

import javax.swing.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

/**
 * FileTransferPreferencePanel is the UI for handling File Transfer Preferences.
 *
 * @author Derek DeMoro
 */
public class FileTransferPreferencePanel extends JPanel {

	private static final long serialVersionUID = -2404221882867691253L;
	private final JTextField timeoutField;
    private final JTextField downloadDirectoryField;
    private final JCheckBox ibbOnly;
    private final JCheckBox autoAccept;

    private JFileChooser fc;


    public FileTransferPreferencePanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(Res.getString("title.file.transfer.preferences")));
        
        timeoutField = new JTextField();
        downloadDirectoryField = new JTextField();
        ibbOnly = new JCheckBox();
        autoAccept = new JCheckBox();

        JLabel timeoutLabel = new JLabel();
        JLabel downloadDirectoryLabel = new JLabel();
        final JButton downloadButton = new JButton();

        ResourceUtils.resLabel(timeoutLabel, timeoutField, Res.getString("label.transfer.timeout"));
        ResourceUtils.resLabel(downloadDirectoryLabel, downloadDirectoryField, Res.getString("label.transfer.download.directory"));
        ResourceUtils.resButton(ibbOnly, Res.getString("checkbox.filetransfer.ibb.only"));
        ResourceUtils.resButton(autoAccept, Res.getString("checkbox.filetransfer.autoaccept.presence"));
        ResourceUtils.resButton(downloadButton, Res.getString("button.browse"));

        add(ibbOnly, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(autoAccept, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(timeoutLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(timeoutField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));

        add(downloadDirectoryLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(downloadDirectoryField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(downloadButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        downloadButton.addActionListener( e -> pickFile(Res.getString("title.choose.directory"), downloadDirectoryField) );
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

    public void setIbbOnly(boolean enable) {
        ibbOnly.setSelected(enable);
    }

    public boolean getIbbOnly() { return ibbOnly.isSelected();  }

    public void setAutoAccept(boolean enable) {
        autoAccept.setSelected(enable);
    }

    public boolean getAutoAccept() { return autoAccept.isSelected();  }

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
    }

}
