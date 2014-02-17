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
package org.jivesoftware.spark.plugins.transfersettings;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.filetransfer.FileTransferListener;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.preference.PreferenceManager;

/**
 * Spark plugin which allows configuration of allowed file sizes, types, and senders for file transfer.
 * Transfer requests which don't meet the configured preferences are automatically rejecte.
 */
public class FileTransferSettingsPlugin implements Plugin {

    private PreferenceManager prefManager;

    /**
     * Called after Spark is loaded to initialize the new plugin.
     */
    public void initialize() {
        addTransferListener();
        prefManager = SparkManager.getPreferenceManager();
        prefManager.addPreference(new TransferSettingsPreference());
    }

    /**
     * Called when Spark is shutting down to allow for persistence of information
     * or releasing of resources.
     */
    public void shutdown() {

    }

    /**
     * Called when the plugin is uninstalled with the Spark plugin manager.
     */
    public void uninstall() {
    }

    /**
     * Return true if the Spark can shutdown on users request.
     *
     * @return true if Spark can shutdown on users request.
     */
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
        if (settings.getBlockedJIDs().contains(trimJID(request.getRequestor()))) {
            return true;
        }
        if (settings.getBlockedExtensions().contains(getFileExtensionFromName(request.getFileName()))) {
            return true;
        }
        return false;

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
            StringBuffer buffer = new StringBuffer("*");
            buffer.append(filename.substring(dotIdx));
            return buffer.toString();
        }

        return null;
    }

    /**
     * Trims the resource off the end of the supplied JID. For example, 'dude@jivesoftware.com/spark' would return
     * 'dude@jivesoftware.com'
     *
     * @param completeJID the JID which possibly includes a resource.
     * @return the JID without the resource.
     */
    private String trimJID(String completeJID) {
        int slashIDX = completeJID.indexOf('/');
        if (slashIDX > 0) {
            return completeJID.substring(0, slashIDX);
        }
        else {
            return completeJID;
        }
    }


    /**
     * Adds a {@link FileTransferListener} to allow this plugin to intercept {@link FileTransferRequest}s.
     */
    private void addTransferListener() {

        SparkTransferManager transferManager = SparkManager.getTransferManager();

        transferManager.addTransferListener(new FileTransferListener() {
            public boolean handleTransfer(FileTransferRequest request) {
                FileTransferSettings settings = (FileTransferSettings)prefManager.getPreferenceData("transferSettings");

                if (requestContainsBannedFile(request, settings)) {
                    request.reject();

                    String responseMessage = settings.getCannedRejectionMessage();
                    if (responseMessage != null && responseMessage.length() > 0) {
                        Message message = new Message();
                        message.setTo(request.getRequestor());
                        message.setBody(responseMessage);
                        SparkManager.getConnection().sendPacket(message);
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        });
    }
}