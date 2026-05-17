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
package org.jivesoftware.spark.plugins.transfersettings;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.filetransfer.FileTransferListener;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.preference.PreferenceManager;
import org.jivesoftware.spark.util.log.Log;

/**
 * Spark plugin which allows configuration of allowed file sizes, types, and senders for file transfer.
 * Transfer requests which don't meet the configured preferences are automatically rejected.
 */
public class FileTransferSettingsPlugin implements Plugin {
    private FileTransferListener fileTransferListener;

    @Override
    public void initialize() {
        PreferenceManager prefManager = SparkManager.getPreferenceManager();
        prefManager.addPreference(new TransferSettingsPreference());
        fileTransferListener = request -> {
            FileTransferSettings settings = (FileTransferSettings) prefManager.getPreferenceData(TransferSettingsPreference.NAMESPACE);
            try {
                if (!requestContainsBannedFile(request, settings)) {
                    return false;
                }
                request.reject();

                String responseMessage = settings.getCannedRejectionMessage();
                if (responseMessage != null && !responseMessage.isEmpty()) {
                    Message message = StanzaBuilder.buildMessage()
                        .to(request.getRequestor())
                        .setBody(responseMessage)
                        .build();
                    SparkManager.getConnection().sendStanza(message);
                }
                return true;
            } catch (SmackException | InterruptedException ex) {
                Log.warning("Unable to handle file transfer.", ex);
                return false;
            }
        };
        SparkTransferManager transferManager = SparkManager.getTransferManager();
        transferManager.addTransferListener(fileTransferListener);
    }

    @Override
    public void shutdown() {
        SparkTransferManager transferManager = SparkManager.getTransferManager();
        transferManager.removeTransferListener(fileTransferListener);
    }

    @Override
    public void uninstall() {
    }

    @Override
    public boolean canShutDown() {
        return true;
    }

    /**
     * Tests the supplied {@link FileTransferRequest} against the supplied {@link FileTransferSettings}. Returns true if  the request
     * fails to match the configuration for allowed files.
     *
     * @param request  the transfer request to test.
     * @param settings the transfer settings to use in testing the request.
     * @return true if the request fails to match the configuration for allowed files.
     */
    private boolean requestContainsBannedFile(FileTransferRequest request, FileTransferSettings settings) {
        if (settings.getCheckFileSize() && request.getFileSize() > settings.getMaxFileSize()) {
            return true;
        }
        if (settings.getBlockedJIDs().contains(request.getRequestor().asEntityBareJidIfPossible())) {
            return true;
        }
        return settings.getBlockedExtensions().contains(getFileExtensionFromName(request.getFileName()));
    }

    /**
     * Strips the extension off the supplied filename and prepends an asterisk. For example 'bad.doc' would return
     * '*.doc'.
     *
     * @param filename to return the extension for.
     * @return the extension.
     */
    private String getFileExtensionFromName(String filename) {
        int dotIdx = filename.lastIndexOf(".");
        if (dotIdx > 0 && dotIdx < (filename.length() - 1)) {
            return "*" + filename.substring( dotIdx );
        }
        return null;
    }

}
