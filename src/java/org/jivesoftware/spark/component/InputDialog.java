/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * <code>InputDialog</code> class is used to retrieve information from a user.
 *
 * @author Derek DeMoro
 */
public final class InputDialog implements PropertyChangeListener {
    private JTextArea textArea;
    private JOptionPane optionPane;
    private JDialog dialog;

    private String stringValue;
    private int width = 400;
    private int height = 250;

    /**
     * Empty Constructor.
     */
    public InputDialog() {
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

        // Lets make sure that the dialog is modal. Cannot risk people
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
        textArea.setWrapStyleWord(true);


        dialog.setVisible(true);
        return stringValue;
    }

    /**
     * Move to focus forward action.
     */
    public Action nextFocusAction = new AbstractAction("Move Focus Forwards") {
        public void actionPerformed(ActionEvent evt) {
            ((Component)evt.getSource()).transferFocus();
        }
    };

    /**
     * Moves the focus backwards in the dialog.
     */
    public Action prevFocusAction = new AbstractAction("Move Focus Backwards") {
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
                stringValue = null;
            }
            else {
                stringValue = stringValue.trim();
            }
            dialog.setVisible(false);
        }
    }
}