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
package org.jivesoftware.spark.filetransfer.preferences;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.WindowsFileSystemView;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	private JTextField timeoutField;
    private JTextField downloadDirectoryField;

    private JFileChooser fc;


    public FileTransferPreferencePanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(Res.getString("title.file.transfer.preferences")));
        
        timeoutField = new JTextField();
        downloadDirectoryField = new JTextField();

        JLabel timeoutLabel = new JLabel();
        JLabel downloadDirectoryLabel = new JLabel();

        ResourceUtils.resLabel(timeoutLabel, timeoutField, Res.getString("label.transfer.timeout"));
        ResourceUtils.resLabel(downloadDirectoryLabel, downloadDirectoryField, Res.getString("label.transfer.download.directory"));

        add(timeoutLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(timeoutField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));

        add(downloadDirectoryLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(downloadDirectoryField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        final JButton button = new JButton();
        ResourceUtils.resButton(button, Res.getString("button.browse"));
        add(button, new GridBagConstraints(2, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        button.addActionListener( e -> pickFile(Res.getString("title.choose.directory"), downloadDirectoryField) );

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
    }

}
