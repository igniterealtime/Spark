/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JLabelIconRenderer;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

public class PreferencesPanel extends JPanel implements ListSelectionListener {
    private final JLabel titleLabel = new JLabel();
    private final JPanel flowPanel = new JPanel(new BorderLayout());
    private DefaultListModel listModel = new DefaultListModel();
    private JList list = new JList(listModel);
    private Preference currentPreference;

    public PreferencesPanel(Iterator preferences) {
        this.setLayout(new GridBagLayout());

        titleLabel.setText(Res.getString("title.preferences"));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(125, 0));
        list.setFixedCellHeight(70);

        add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 50, 0));
        add(flowPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));


        list.setCellRenderer(new JLabelIconRenderer());
        list.addListSelectionListener(this);
        // Populate with current preferences
        while (preferences.hasNext()) {
            Preference preference = (Preference)preferences.next();
            listModel.addElement(new PreferenceUI(preference));
        }

        list.setSelectedIndex(0);
    }

    public void valueChanged(ListSelectionEvent e) {

        if (!e.getValueIsAdjusting()) {

            if (currentPreference != null) {
                if (currentPreference.isDataValid()) {
                    currentPreference.commit();
                }
                else {
                    JOptionPane.showMessageDialog(this, currentPreference.getErrorMessage(),
                            Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    list.removeListSelectionListener(this);
                    list.setSelectedIndex(e.getLastIndex());
                    list.addListSelectionListener(this);
                }

            }

            PreferenceUI o = (PreferenceUI)list.getSelectedValue();
            Preference pref = o.getPreference();
            pref.load();

            JComponent comp = pref.getGUI();
            flowPanel.removeAll();

            // Create the title panel for this dialog
            TitlePanel titlePanel = new TitlePanel(pref.getTitle(),
                    pref.getTooltip(),
                    pref.getIcon(),
                    false);


            flowPanel.add(comp, BorderLayout.CENTER);
            flowPanel.add(titlePanel, BorderLayout.NORTH);
            flowPanel.invalidate();
            flowPanel.validate();
            flowPanel.repaint();
            currentPreference = pref;
        }
    }

    public boolean closing() {
        if (currentPreference != null) {
            if (currentPreference.isDataValid()) {
                currentPreference.commit();
                SettingsManager.fireListeners();
                return true;
            }
            else {
                JOptionPane.showMessageDialog(this, currentPreference.getErrorMessage(),
                        Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
}