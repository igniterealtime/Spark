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
package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.resource.Res;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * <code>InputTextAreaDialog</code> class can be used for any input required that a simple text field could
 * not handle.
 *
 * @author Derek DeMoro
 * @version 1.0, 06/28/2005
 */
public final class InputTextAreaDialog implements PropertyChangeListener {
    private JTextArea textArea;
    private JOptionPane optionPane;
    private JDialog dialog;

    private String stringValue;
    private int width = 400;
    private int height = 250;

    /**
     * Empty Constructor.
     */
    public InputTextAreaDialog() {
    }

    /**
     * Returns the input from a user.
     *
     * @param title       the title of the dialog.
     * @param description the dialog description.
     * @param icon        the icon to use.
     * @param width       the dialog width
     * @param height      the dialog height
     * @return the users input.
     */
    public String getInput(String title, String description, Icon icon, int width, int height) {
        this.width = width;
        this.height = height;

        return getInput(title, description, icon, SparkManager.getMainWindow());
    }

    /**
     * Prompt and return input.
     *
     * @param title       the title of the dialog.
     * @param description the dialog description.
     * @param icon        the icon to use.
     * @param parent      the parent to use.
     * @return the user input.
     */
    public String getInput(String title, String description, Icon icon, Component parent) {
        textArea = new JTextArea();
        textArea.setLineWrap(true);

        TitlePanel titlePanel = new TitlePanel(title, description, icon, true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        final Object[] options = {Res.getString("ok"), Res.getString("cancel")};
        optionPane = new JOptionPane(new JScrollPane(textArea), JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(optionPane, BorderLayout.CENTER);

        // Let's make sure that the dialog is modal. Cannot risk people
        // losing this dialog.
        JOptionPane p = new JOptionPane();
        dialog = p.createDialog(parent, title);
        dialog.setModal(true);
        dialog.pack();
        dialog.setSize(width, height);
        dialog.setContentPane(mainPanel);
        dialog.setLocationRelativeTo(parent);
        optionPane.addPropertyChangeListener(this);

        // Add Key Listener to Send Field
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_TAB) {
                    optionPane.requestFocus();
                }
                else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    dialog.dispose();
                }
            }
        });

        textArea.requestFocus();


        dialog.setVisible(true);
        return stringValue;
    }

    /**
     * Move to focus forward action.
     */
    public Action nextFocusAction = new AbstractAction("Move Focus Forwards") {
		private static final long serialVersionUID = 1238373124060258519L;

		public void actionPerformed(ActionEvent evt) {
            ((Component)evt.getSource()).transferFocus();
        }
    };

    /**
     * Moves the focus backwards in the dialog.
     */
    public Action prevFocusAction = new AbstractAction("Move Focus Backwards") {
		private static final long serialVersionUID = 7927553016576134725L;

		public void actionPerformed(ActionEvent evt) {
            ((Component)evt.getSource()).transferFocusBackward();
        }
    };

    public void propertyChange(PropertyChangeEvent e) {
        String value = (String)optionPane.getValue();
        if (Res.getString("cancel").equals(value)) {
            stringValue = null;
            dialog.setVisible(false);
        }
        else if (Res.getString("ok").equals(value)) {
            stringValue = textArea.getText();
            if (stringValue.trim().length() == 0) {
                stringValue = "";
            }
            else {
                stringValue = stringValue.trim();
            }
            dialog.setVisible(false);
        }
    }
}