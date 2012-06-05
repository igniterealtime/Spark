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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JLabelIconRenderer;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class PreferencesPanel extends JPanel implements ListSelectionListener {

    private static final long serialVersionUID = 1520420979038154046L;
    private final JLabel titleLabel = new JLabel();
    /**
     * flowpanel is the right panel, where the plugin specific UI is displayed
     */
    private final JPanel flowPanel = new JPanel(new BorderLayout());
    /**
     * scrollPane is the left panel displaying the preference icons
     */
    private JScrollPane scrollPane;
    private DefaultListModel listModel = new DefaultListModel();
    private JList list = new JList(listModel);
    private Preference currentPreference;
    
    /**
     * <h1>Constructor - PreferencesPanel</h1>
     * This is an option to select the transmitted preference by code
     * If the given preference is null or not contained in the preference-list,
     * the first index of the list will be selected.
     * 
     * @param preferences the preference list
     * @param displayPref the preference you want to select
     */
    public PreferencesPanel (Iterator<Preference> preferences, Preference displayPref){
        this(preferences);
        if ( displayPref != null || listModel.getSize() == 1){
            // iterate through all preference-ui items
            for (int i = 0; i < listModel.size(); i++){
                PreferenceUI p = (PreferenceUI)listModel.get( i );
                // check if the namespace is the namespace we search for
                if (p.getPreference().getNamespace() == displayPref.getNamespace()){
                    // if we've got our target, we can select this item and stop the search
                    list.setSelectedIndex( i );
                    break; 
                }
            }
            // if we got a valid target, we trigger the selection changed method
            if (list.getSelectedIndex() > -1) selectionChanged();
        }
    }
    
    public PreferencesPanel(Iterator<Preference> preferences) {
        this.setLayout(new GridBagLayout());

        titleLabel.setText(Res.getString("title.preferences"));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(125, 0));
        scrollPane.setMinimumSize(new Dimension(125,100));
        list.setFixedCellHeight(50);

        add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 50, 0));
        add(flowPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));


        list.setCellRenderer(new JLabelIconRenderer());
        list.addListSelectionListener(this);
        // Populate with current preferences
        while (preferences.hasNext()) {
            Preference preference = preferences.next();
            listModel.addElement(new PreferenceUI(preference));
        }

        list.setSelectedIndex(0);
    }

    private synchronized void selectionChanged(){
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
            selectionChanged();
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