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

import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JLabelIconRenderer;
import org.jivesoftware.spark.preference.Preference;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;

public class PreferencesPanel extends JPanel implements ListSelectionListener {
    private final JLabel titleLabel = new JLabel();
    private final JSeparator jSeparator1 = new JSeparator();
    private final JPanel flowPanel = new JPanel(new BorderLayout());
    private DefaultListModel listModel = new DefaultListModel();
    private JList list = new JList(listModel);
    private Preference currentPreference;

    public PreferencesPanel(Iterator preferences) {
        this.setLayout(new BorderLayout());
        titleLabel.setText("Spark Preferences");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 15));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(125, 0));
        this.add(scrollPane, BorderLayout.WEST);
        list.setFixedCellHeight(70);

        this.add(flowPanel, BorderLayout.CENTER);


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
                            "Preference Error", JOptionPane.ERROR_MESSAGE);
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
                return true;
            }
            else {
                JOptionPane.showMessageDialog(this, currentPreference.getErrorMessage(),
                        "Preference Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
}