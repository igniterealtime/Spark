/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Downloads {
    final JPanel mainPanel = new JPanel();
    File downloadedDir;
    private JPanel list = new JPanel();

    private static Downloads singleton;
    private static final Object LOCK = new Object();
    private JDialog dlg;
    private JFileChooser chooser;

    private LocalPreferences pref;

    /**
     * Returns the singleton instance of <CODE>Downloads</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>Downloads</CODE>
     */
    public static Downloads getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                Downloads controller = new Downloads();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }


    private Downloads() {
        ChatFrame frame = SparkManager.getChatManager().getChatContainer().getChatFrame();
        dlg = new JDialog(SparkManager.getMainWindow(), Res.getString("title.downloads"), false);
        dlg.setContentPane(mainPanel);
        dlg.pack();
        dlg.setSize(400, 400);
        dlg.setResizable(true);

        dlg.setLocationRelativeTo(frame);

        pref = SettingsManager.getLocalPreferences();
        downloadedDir = new File(pref.getDownloadDir());
        downloadedDir.mkdirs();
        pref.setDownloadDir(downloadedDir.getAbsolutePath());


        list.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 1, true, false));
        list.setBackground(Color.white);

        mainPanel.setLayout(new GridBagLayout());


        mainPanel.add(new JScrollPane(list), new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));


        JButton cleanUpButton = new JButton("Clean Up", SparkRes.getImageIcon(SparkRes.SMALL_DELETE));

        JLabel locationLabel = new JLabel();
        locationLabel.setText("All Files Downloaded To: ");

        RolloverButton userHomeButton = new RolloverButton("Downloads", null);

        Action openFolderAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                downloadedDir = new File(SparkManager.getUserDirectory(), "downloads");
                if (!downloadedDir.exists()) {
                    downloadedDir.mkdirs();
                }
                openFile(downloadedDir);
            }
        };
        userHomeButton.addActionListener(openFolderAction);

        mainPanel.add(locationLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(userHomeButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        mainPanel.add(cleanUpButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Remove all download panels
        cleanUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                list.removeAll();
                list.validate();
                list.repaint();
            }
        });

    }

    public JFileChooser getFileChooser() {
        if (chooser == null) {
            downloadedDir = new File(SparkManager.getUserDirectory(), "downloads");
            if (!downloadedDir.exists()) {
                downloadedDir.mkdirs();
            }
            chooser = new JFileChooser(downloadedDir);
            if (Spark.isWindows()) {
                chooser.setFileSystemView(new WindowsFileSystemView());
            }
        }
        return chooser;
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


    public File getDownloadDirectory() {
        return downloadedDir;
    }

    public void addDownloadPanel(JPanel panel) {
        list.add(panel);
    }

    public void removeDownloadPanel(JPanel panel) {
        list.remove(panel);
        list.validate();
        list.repaint();
    }

    public void showDownloadsDirectory() {
        downloadedDir = new File(pref.getDownloadDir());
        if (!downloadedDir.exists()) {
            downloadedDir.mkdirs();
        }
        openFile(downloadedDir);
    }
}
