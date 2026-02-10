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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 */
public class FileTransferPreference implements Preference {

    private final FileTransferPreferencePanel ui;
    private final LocalPreferences localPreferences;

    public FileTransferPreference() {
        localPreferences = SettingsManager.getLocalPreferences();
        int timeout = localPreferences.getFileTransferTimeout();

        timeout = timeout * 60 * 1000;

        OutgoingFileTransfer.setResponseTimeout(timeout);

        ui = new FileTransferPreferencePanel();
    }

    @Override
	public String getTitle() {
        return Res.getString("title.file.transfer.preferences");
    }

    @Override
	public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.SEND_FILE_ICON);
    }

    @Override
	public String getTooltip() {
        return Res.getString("tooltip.file.transfer");
    }

    @Override
	public String getListName() {
        return Res.getString("title.file.transfer");
    }

    @Override
	public String getNamespace() {
        return "FILE_TRANSFER";
    }

    @Override
	public JComponent getGUI() {
        return ui;
    }

    @Override
	public void load() {
        boolean ibb = localPreferences.isFileTransferIbbOnly();
        int timeout = localPreferences.getFileTransferTimeout();
        boolean auto = localPreferences.isAutoAcceptFileTransferFromContacts();
        ui.setDownloadDirectory(localPreferences.getDownloadDir());
        ui.setTimeout(Integer.toString(timeout));
        ui.setIbbOnly(ibb);
        ui.setAutoAccept(auto);
    }

    @Override
	public void commit() {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        pref.setFileTransferIbbOnly(ui.getIbbOnly());
        pref.setAutoAcceptFileTransferFromContacts(ui.getAutoAccept());
        String downloadDir = ui.getDownloadDirectory();
        if (ModelUtil.hasLength(downloadDir)) {
            pref.setDownloadDir(downloadDir);
        }

        String timeout = ui.getTimeout();
        if (ModelUtil.hasLength(timeout)) {
            int tout = 1;
            try {
                tout = Integer.parseInt(timeout);
            } catch (NumberFormatException e) {
                // Nothing to do
            }

            pref.setFileTransferTimeout(tout);

            final int timeOutMs = tout * (60 * 1000);
            OutgoingFileTransfer.setResponseTimeout(timeOutMs);
        }
    }

    @Override
	public boolean isDataValid() {
        return true;
    }

    @Override
	public String getErrorMessage() {
        return null;
    }

    @Override
	public Object getData() {
        return null;
    }

    @Override
	public void shutdown() {
        commit();
    }
}
