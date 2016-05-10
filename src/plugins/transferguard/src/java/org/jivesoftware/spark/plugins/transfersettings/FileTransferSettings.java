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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Bean whose properties are the various preference settings for file transfer.
 */
public class FileTransferSettings {

    private List<String> extensions = new ArrayList<>();
    private List<String> JIDs = new ArrayList<>();
    private int kb;
    private boolean checkSize = false;
    String cannedRejectionMessage;
    private static File BACKING_STORE = new File(System.getProperty("user.home") + "/.sparkExt.properties");


    /**
     * Returns a {@link List} of strings - one for each blocked file extension. Strings are in the form <tt>*.{extension}</tt>.
     * @return a {@link List} of blocked file extensions
     */
    public List<String> getBlockedExtensions(){
        return extensions;
    }

    /**
     * Sets the {@link List} of blocked file extensions.
     * @param extensions    the {@link List} of blocked file extensions.
     */
    public void setBlockedExtensions(List<String> extensions){
        this.extensions = extensions;
    }

    /**
     * Returns a {@link List} of blocked JIDs. File transfers from users with those JIDs will be automaticlly rejected.
     * @return a {@link List} of blocked JIDs.
     */
    public List<String> getBlockedJIDs() {
        return JIDs;
    }

    /**
     * Sets the {@link List} of blocked JIDs.
     * @param JIDs  the {@link List} of blocked JIDs.
     */
    public void setBlockedJIDS(List<String> JIDs){
        this.JIDs = JIDs;
    }

    /**
     * Returns the maximum file size in kilobytes for file transfers. If {@link #getCheckFileSize} returns true,
     * files larger than this maximum will not be accepted.
     * @return the maximum file size in kilobytes for file transfers.
     */
    public int getMaxFileSize(){
        return kb;
    }

    /**
     * Sets the maximum file size in kilobytes for file transfers.
     * @param kb the maximum file size in kilobytes for file transfers.
     */
    public void setMaxFileSize(int kb){
        this.kb = kb;
    }

    /**
     * Returns true if there is a maximum allowable file size for transfers.
     * @return true if there is a maximum allowable file size for transfers.
     */
    public boolean getCheckFileSize(){
        return checkSize;
    }

    /**
     * If set to true, files larger than the maximum file size as returned by {@link #getMaxFileSize}
     * will not be accepted.
     * @param checkSize true if size should be checked.
     */
    public void setCheckFileSize(boolean checkSize){
        this.checkSize = checkSize;
    }

    /**
     * Returns the text of a canned message sent to requestors whose file transfers were automatically rejected. If this
     * returns null or an empty string, no message will be sent.
     * @return the text of a canned message sent to requestors whose file transfers were automatically rejected.
     */
    public String getCannedRejectionMessage() {
        return cannedRejectionMessage;
    }

    /**
     * Sets the text of a canned message sent to requestors whose file transfers were automatically rejected. If set
     * to null or an empty string, no message will be sent.
     * @param cannedRejectionMessage the canned message text.
     */
    public void setCannedRejectionMessage(String cannedRejectionMessage) {
        this.cannedRejectionMessage = cannedRejectionMessage;
    }

    /**
     * Loads the properties from the filesystem.
     */
    public void load() {
        Properties props = new Properties();

        if (BACKING_STORE.exists()) {
            try {
                props.load(new FileInputStream(BACKING_STORE));

                String types = props.getProperty("extensions");
                if (types != null) {
                    this.extensions = convertSettingsStringToList(types);
                }

                String users = props.getProperty("jids");
                if (users != null) {
                    this.JIDs = convertSettingsStringToList(users);
                }

                String ignore = props.getProperty("checkFileSize");
                if (ignore != null) {
                    this.checkSize = Boolean.valueOf(ignore);
                }

                String maxSize = props.getProperty("maxSize");
                if (maxSize != null) {
                    this.kb = Integer.parseInt(maxSize);
                }

                this.cannedRejectionMessage = props.getProperty("cannedResponse");

            } catch (IOException ioe) {
                System.out.println("Error Loading properties from Filesystem"+ioe);
                //TODO handle error better.
            }
        }
    }

    /**
     * Saves the properties to the filesystem.
     */
    public void store() {
        Properties props = new Properties();

        try {
            props.setProperty("extensions", convertSettingsListToString(extensions));
            props.setProperty("jids", convertSettingsListToString(JIDs));
            props.setProperty("checkFileSize", Boolean.toString(checkSize));
            props.setProperty("maxSize", Integer.toString(kb));
            if (cannedRejectionMessage != null) {
                props.setProperty("cannedResponse", cannedRejectionMessage);
            }

            props.store(new FileOutputStream(BACKING_STORE), BACKING_STORE.getAbsolutePath());

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    /**
     * Converts a list of strings to a single comma separated string
     * @param settings the {@link List} of strings.
     * @return a comma separated string.
     */
    public static String convertSettingsListToString(List<String> settings) {
        StringBuilder buffer = new StringBuilder();
        for (Iterator<String> iter=settings.iterator(); iter.hasNext(); ) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }

    /**
     * Converts the supplied string to a {@link List} of strings. The input is split
     * with the tokensL: ',' ':' '\n' '\t' '\r' and ' '.
     * @param settings  the string to convert.
     * @return  the resultant {@link List}.
     */
    public static List<String> convertSettingsStringToList(String settings) {
        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(settings, ",;\n\t\r ");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }
}