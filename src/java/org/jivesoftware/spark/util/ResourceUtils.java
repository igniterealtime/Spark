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
package org.jivesoftware.spark.util;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.component.RolloverButton;

import javax.swing.AbstractButton;
import javax.swing.JLabel;

import java.awt.Component;

/**
 * Utility class to make using resources easier.
 * <p/>
 * <strong>Example use:</strong>
 * <p/>
 * <pre>
 * <p/>
 * JLabel lblUsername = new JLabel();
 * JTextField tfUsername = new JTextField();
 * ResourceUtils.resLabel( lblUserName, tfUserName,
 *                         IdeArb.getString ( IdeArb.USERNAME ) );
 * <p/>
 * JButton b = new JButton();
 * ResourceUtils.resButton( b, IdeArb.getString( IdeArb.SOME_STRING ) );
 * <p/>
 * </pre>
 */
public final class ResourceUtils {
    /**
     * Sets the resources on a {@link JLabel}.  It sets the text, mnemonic,
     * and labelFor property.
     *
     * @param label     The Label on which to set the properties
     * @param labelFor  the {@link Component} to set with the
     *                  <code>labelFor</code> property on the <code>label</code>.
     * @param labelText The text label to set on the <code>label</code>
     * @see JLabel#setText(String)
     * @see JLabel#setLabelFor(Component)
     * @see JLabel#setDisplayedMnemonic(int)
     */
    public static void resLabel(JLabel label, Component labelFor, String labelText) {
        label.setText(stripMnemonic(labelText));

        if (Spark.isWindows()) {
            label.setDisplayedMnemonic(getMnemonicKeyCode(labelText));
        }
        label.setLabelFor(labelFor);
    }

    /**
     * Sets the resources on a subclass of {@link AbstractButton}.  The common
     * classes are {@link javax.swing.JRadioButton}, {@link javax.swing.JButton},
     * and {@link javax.swing.JCheckBox}
     * <p/>
     * This method sets the text and mnemonic.
     *
     * @param button    The button on which to set the text and mnemonoic
     * @param labelText the text which contains the displayed text and mnemonic
     * @see AbstractButton#setText(String)
     * @see AbstractButton#setMnemonic(int)
     */
    public static void resButton(AbstractButton button, String labelText) {
        button.setText(stripMnemonic(labelText));

        if (Spark.isWindows()) {
            button.setMnemonic(getMnemonicKeyCode(labelText));
        }
    }

      /**
     * Sets the resources on a subclass of {@link AbstractButton}.  The common
     * classes are {@link javax.swing.JRadioButton}, {@link javax.swing.JButton},
     * and {@link javax.swing.JCheckBox}
     * <p/>
     * This method sets the text and mnemonic.
     *
     * @param button    The button on which to set the text and mnemonoic
     * @param labelText the text which contains the displayed text and mnemonic
     * @see AbstractButton#setText(String)
     * @see AbstractButton#setMnemonic(int)
     */
    public static void resButton(RolloverButton button, String labelText) {
        button.setText(stripMnemonic(labelText));

        if (Spark.isWindows()) {
            button.setMnemonic(getMnemonicKeyCode(labelText));
        }
    }

    public static String stripMnemonic(String label) {
        String text;
        int index = label.indexOf("&");
        if (index != -1) {
            text = label.substring(0, index);
            if (label.length() > index) {
                text = text + label.substring(index + 1);
                return text;
            }
        }
        return label;
    }

    public static int getMnemonicKeyCode(String mnemonic) {
        int mindex = mnemonic.indexOf("&");
        if (mindex > -1) {
            return (int)mnemonic.toUpperCase().charAt(mindex + 1);
        }
        return 0;
    }

    private ResourceUtils() {
    }


}
