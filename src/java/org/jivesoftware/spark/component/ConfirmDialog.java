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
package org.jivesoftware.spark.component;

import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Implementation of a Confirm Dialog to replace the modal JOptionPane.confirm.  This is intended
 * for use as a yes - no dialog.
 *
 * @author Derek DeMoro
 */
public class ConfirmDialog extends BackgroundPanel {
    private static final long serialVersionUID = -441250586899776207L;
    private JLabel message;
    private JLabel iconLabel;
    private JButton yesButton;
    private JButton noButton;

    private ConfirmListener listener = null;
    private JDialog dialog;

    /**
     * Creates the base confirm Dialog.
     */
    public ConfirmDialog() {
        setLayout(new GridBagLayout());

        message = new JLabel();
        iconLabel = new JLabel();
        yesButton = new JButton();
        noButton = new JButton();

        add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(message, new GridBagConstraints(1, 0, 4, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(yesButton, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(noButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (listener != null) {
                    listener.yesOption();
                    listener = null;
                }
                dialog.dispose();
            }
        });

        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.dispose();
            }
        });


    }

    /**
     * Creates and displays the new confirm dialog.
     *
     * @param parent  the parent dialog.
     * @param title   the title of this dialog.
     * @param text    the main text to display.
     * @param yesText the text to use on the OK or Yes button.
     * @param noText  the text to use on the No button.
     * @param icon    the icon to use for graphical represenation.
     */
    public void showConfirmDialog(JFrame parent, String title, String text, String yesText, String noText, Icon icon) {
        message.setText("<html><body>" + text + "</body></html>");
        iconLabel.setIcon(icon);

        ResourceUtils.resButton(yesButton, yesText);
        ResourceUtils.resButton(noButton, noText);

        dialog = new JDialog(parent, title, false);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent windowEvent) {
                if (listener != null) {
                    listener.noOption();
                }
            }
        });
    }

    public void setDialogSize(int width, int height) {
        dialog.setSize(width, height);
        dialog.pack();
        dialog.validate();
    }

    /**
     * Sets the ConfirmListener to use with this dialog instance.
     *
     * @param listener the <code>ConfirmListener</code> to use with this instance.
     */
    public void setConfirmListener(ConfirmListener listener) {
        this.listener = listener;
    }

    /**
     * Used to handle yes/no selection in dialog. You would use this simply to
     * be notified when a user has either clicked on the yes or no dialog.
     */
    public interface ConfirmListener {

        /**
         * Fired when the Yes button has been clicked.
         */
        void yesOption();

        /**
         * Fired when the No button has been clicked.
         */
        void noOption();
    }
}
