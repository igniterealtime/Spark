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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.util.JidUtil;

/**
 * Bean whose properties are the various preference settings for file transfer.
 */
public class FileTransferSettings {
    private Set<String> extensions = Set.of();
    private Set<EntityBareJid> JIDs = Set.of();
    private int kb;
    private boolean checkSize;
    private String cannedRejectionMessage;
    private static final File BACKING_STORE = new File(Spark.getSparkUserHome(), "/transferguard.properties");

    /**
     * Returns a {@link Set} of strings - one for each blocked file extension. Strings are in the form <tt>*.{extension}</tt>.
     */
    public Set<String> getBlockedExtensions(){
        return extensions;
    }

    public void setBlockedExtensions(Set<String> extensions){
        this.extensions = extensions;
    }

    /**
     * Returns a {@link Set} of blocked JIDs. File transfers from users with those JIDs will be automatically rejected.
     */
    public Set<EntityBareJid> getBlockedJIDs() {
        return JIDs;
    }

    public void setBlockedJIDS(Set<EntityBareJid> JIDs){
        this.JIDs = JIDs;
    }

    /**
     * Returns the maximum file size in kilobytes for file transfers. If {@link #getCheckFileSize} returns true,
     * files larger than this maximum will not be accepted.
     */
    public int getMaxFileSize(){
        return kb;
    }

    public void setMaxFileSize(int kb){
        this.kb = kb;
    }

    /**
     * Returns true if there is a maximum allowable file size for transfers.
     */
    public boolean getCheckFileSize(){
        return checkSize;
    }

    public void setCheckFileSize(boolean checkSize){
        this.checkSize = checkSize;
    }

    /**
     * Returns the text of a canned message sent to requestors whose file transfers were automatically rejected. If this
     * returns null or an empty string, no message will be sent.
     */
    public String getCannedRejectionMessage() {
        return cannedRejectionMessage;
    }

    public void setCannedRejectionMessage(String cannedRejectionMessage) {
        this.cannedRejectionMessage = cannedRejectionMessage;
    }

    /**
     * Loads the properties from the filesystem.
     */
    public void load() {
        Properties props = new Properties();
        if (!BACKING_STORE.exists()) {
            return;
        }
        try {
            props.load(new FileInputStream(BACKING_STORE));

            String types = props.getProperty("extensions");
            if (types != null) {
                this.extensions = convertSettingsStringToList(types);
            }

            String users = props.getProperty("jids");
            if (users != null) {
                Set<String> jidStrings = convertSettingsStringToList(users);
                Set<EntityBareJid> jidSet = JidUtil.entityBareJidSetFrom(jidStrings);
                this.JIDs = jidSet;
            }

            String ignore = props.getProperty("checkFileSize");
            if (ignore != null) {
                this.checkSize = Boolean.parseBoolean(ignore);
            }

            String maxSize = props.getProperty("maxSize");
            if (maxSize != null) {
                this.kb = Integer.parseInt(maxSize);
            }
            this.cannedRejectionMessage = props.getProperty("cannedResponse");
        } catch (IOException ioe) {
            Log.error("Error loading Transfer Guard settings", ioe);
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
            } else {
                props.remove("cannedResponse");
            }
            props.store(new FileOutputStream(BACKING_STORE), null);
        } catch (IOException ioe) {
            Log.error(ioe);
        }
    }

    /**
     * Converts a list of strings to a single comma-separated string
     */
    public static String convertSettingsListToString(Collection<? extends CharSequence> settings) {
        return String.join(",", settings);
    }

    /**
     * Converts the supplied string to a {@link List} of strings in lower case.
     * The input is split with the tokens: ',' ':' '\n' '\t' '\r' and ' '.
     */
    public static Set<String> convertSettingsStringToList(String settings) {
        HashSet<String> list = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(settings, ",;\n\t\r ");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken().toLowerCase());
        }
        return list;
    }
}
