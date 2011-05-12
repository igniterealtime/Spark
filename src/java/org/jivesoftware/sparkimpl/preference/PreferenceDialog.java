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

package org.jivesoftware.sparkimpl.preference;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettings;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;

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
				saveLayout();
				preferenceDialog.setVisible(false);
				preferenceDialog.dispose();
			}
        });
        btn_save.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean okToClose = prefPanel.closing();
	            if (okToClose) {
	            	 saveLayout();
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
        preferenceDialog.setContentPane(mainPanel);
        preferenceDialog.pack();
        
        LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
        if ((settings.getPreferencesFrameX() == 0 && settings.getPreferencesFrameY() == 0)
      	  || settings.getPreferencesFrameHeight() < 100
      	  || settings.getPreferencesFrameWidth() < 100) {
            // Use default settings.
      	   preferenceDialog.setSize(750, 550);
      	   preferenceDialog.setLocationRelativeTo(SparkManager.getMainWindow());
        }
        else {
      	  preferenceDialog.setBounds(settings.getPreferencesFrameX(), settings.getPreferencesFrameY(), settings.getPreferencesFrameWidth(), settings.getPreferencesFrameHeight());
        }

        pane.addPropertyChangeListener(this);

        preferenceDialog.setVisible(true);
        preferenceDialog.toFront();
        
        preferenceDialog.addWindowListener(new WindowAdapter() {
      	  public void windowClosing(WindowEvent e) {
      		  saveLayout();
      	  }
        });
    }

    public void propertyChange(PropertyChangeEvent e) {
    	if (pane.getValue() instanceof Integer) {
    			saveLayout();
            pane.removePropertyChangeListener(this);
            preferenceDialog.dispose();
            return;
        }
    }

    public JDialog getDialog() {
        return preferenceDialog;
    }
    
    /**
     * Saves the layout on closing of the main window.
     */
    private void saveLayout() {
        try {
            LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();
            settings.setPreferencesFrameHeight(preferenceDialog.getHeight());
            settings.setPreferencesFrameWidth(preferenceDialog.getWidth());
            settings.setPreferencesFrameX(preferenceDialog.getX());
            settings.setPreferencesFrameY(preferenceDialog.getY());
            LayoutSettingsManager.saveLayoutSettings();
        }
        catch (Exception e) {
            // Don't let this cause a real problem shutting down.
        }
    }
}
