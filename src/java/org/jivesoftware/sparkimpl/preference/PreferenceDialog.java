/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference;

import org.jivesoftware.spark.SparkManager;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PreferenceDialog implements PropertyChangeListener {
    private JDialog preferenceDialog;
    private JOptionPane pane = null;
    private PreferencesPanel prefPanel;

    public void invoke(JFrame parentFrame, PreferencesPanel contentPane) {

        this.prefPanel = contentPane;

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Construct Dialog
        preferenceDialog = new JDialog(parentFrame,
                "Preferences",
                true);

        Object[] options = {"Close"};
        pane = new JOptionPane(contentPane, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);
        preferenceDialog.pack();
        preferenceDialog.setSize(600, 500);
        preferenceDialog.setContentPane(mainPanel);
        preferenceDialog.setLocationRelativeTo(SparkManager.getMainWindow());

        pane.addPropertyChangeListener(this);

        preferenceDialog.setVisible(true);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (pane.getValue() instanceof Integer) {
            pane.removePropertyChangeListener(this);
            preferenceDialog.dispose();
            return;
        }
        String value = (String)pane.getValue();
        if (value.equals("Close")) {
            boolean okToClose = prefPanel.closing();
            if (okToClose) {
                preferenceDialog.setVisible(false);
            }
            else {
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        }
    }

    public JDialog getDialog() {
        return preferenceDialog;
    }
}