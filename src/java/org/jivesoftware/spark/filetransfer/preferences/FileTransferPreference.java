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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 */
public class FileTransferPreference implements Preference {

    private FileTransferPreferencePanel ui;
    private LocalPreferences localPreferences;

    public FileTransferPreference() {
        localPreferences = SettingsManager.getLocalPreferences();
        int timeout = localPreferences.getFileTransferTimeout();

        timeout = timeout * 60 * 1000;

        OutgoingFileTransfer.setResponseTimeout(timeout);

        ui = new FileTransferPreferencePanel();
    }

    public String getTitle() {
        return Res.getString("title.file.transfer.preferences");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.SEND_FILE_24x24);
    }

    public String getTooltip() {
        return Res.getString("tooltip.file.transfer");
    }

    public String getListName() {
        return Res.getString("title.file.transfer");
    }

    public String getNamespace() {
        return "FILE_TRANSFER";
    }

    public JComponent getGUI() {
        return ui;
    }

    public void load() {
        File downloadedDir = new File(SparkManager.getUserDirectory(), "downloads");

        String downloadDirectory = localPreferences.getDownloadDir();
        if (downloadDirectory == null) {
            downloadDirectory = downloadedDir.getAbsolutePath();
        }

        int timeout = localPreferences.getFileTransferTimeout();

        ui.setDownloadDirectory(downloadDirectory);
        ui.setTimeout(Integer.toString(timeout));
    }

    public void commit() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        String downloadDir = ui.getDownloadDirectory();
        if (ModelUtil.hasLength(downloadDir)) {
            pref.setDownloadDir(downloadDir);
        }

        String timeout = ui.getTimeout();
        if (ModelUtil.hasLength(timeout)) {
            int tout = 1;
            try {
                tout = Integer.parseInt(timeout);
            }
            catch (NumberFormatException e) {
                // Nothing to do
            }

            pref.setFileTransferTimeout(tout);

            final int timeOutMs = tout * (60 * 1000);
            OutgoingFileTransfer.setResponseTimeout(timeOutMs);
        }

        SettingsManager.saveSettings();


    }

    public boolean isDataValid() {
        return true;
    }

    public String getErrorMessage() {
        return null;
    }

    public Object getData() {
        return null;
    }

    public void shutdown() {
        commit();
    }
}
