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
package org.jivesoftware.spark.util;

import org.jasypt.util.text.AES256TextEncryptor;
import org.jivesoftware.resource.Res;

import javax.swing.*;

/**
 * Encrypts and Decrypts text using AES256 key derived from user-defined password (PBKDF2).
 *
 * @author Derek DeMoro
 */
public class Encryptor {

    private static final IllegalArgumentException UNDEFINED_MASTER_PASSWORD_EXCEPTION = new IllegalArgumentException("No master password provided");
    public static final AES256TextEncryptor AES256_INSTANCE = new AES256TextEncryptor();
    private static boolean MASTER_PASSWORD_SET = false;

    /**
     * Set the Master password for PBKDF
     * @param isNew first time the master password is set (nothing encrypted before)
     */
    public static void setMasterPassword(boolean isNew) {
        if(MASTER_PASSWORD_SET) {
            return;
        }

        final JPasswordField passwordField = new JPasswordField(70);
        final int option = JOptionPane.showConfirmDialog(null, passwordField, Res.getString(isNew? "dialog.title.master.password.prompt.new": "dialog.title.master.password.prompt.old"), JOptionPane.OK_CANCEL_OPTION);
        if(option != JOptionPane.OK_OPTION) // pressing OK button
        {
            throw UNDEFINED_MASTER_PASSWORD_EXCEPTION;
        }

        AES256_INSTANCE.setPasswordCharArray(passwordField.getPassword());
        MASTER_PASSWORD_SET = true;
    }

    public static void setMasterPassword(String password) {
        if(password == null) {
            throw UNDEFINED_MASTER_PASSWORD_EXCEPTION;
        }

        AES256_INSTANCE.setPassword(password);
        MASTER_PASSWORD_SET = true;
    }
}
