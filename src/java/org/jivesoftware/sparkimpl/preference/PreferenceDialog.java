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
import org.jivesoftware.resource.Res;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
                Res.getString("title.preferences"),
                false);

        JButton btn_apply = new JButton(Res.getString("apply"));
        JButton btn_save = new JButton(Res.getString("save"));
        JButton btn_close = new JButton(Res.getString("close"));
        
        btn_close.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				preferenceDialog.setVisible(false);
				preferenceDialog.dispose();
			}
        });
        btn_save.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean okToClose = prefPanel.closing();
	            if (okToClose) {
	                preferenceDialog.setVisible(false);
	                preferenceDialog.dispose();
	            }
	            else {
	                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	            }
			}
        });
        btn_apply.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean okToClose = prefPanel.closing();
	            if (!okToClose) {
	                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	            }
			}
        });
        
        Object[] options = {btn_apply, btn_save, btn_close};
        pane = new JOptionPane(contentPane, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);
        preferenceDialog.pack();
        preferenceDialog.setSize(750, 550);
        preferenceDialog.setContentPane(mainPanel);
        preferenceDialog.setLocationRelativeTo(SparkManager.getMainWindow());

        pane.addPropertyChangeListener(this);

        preferenceDialog.setVisible(true);
        preferenceDialog.toFront();
    }

    public void propertyChange(PropertyChangeEvent e) {
    	if (pane.getValue() instanceof Integer) {
            pane.removePropertyChangeListener(this);
            preferenceDialog.dispose();
            return;
        }
    }

    public JDialog getDialog() {
        return preferenceDialog;
    }
}
