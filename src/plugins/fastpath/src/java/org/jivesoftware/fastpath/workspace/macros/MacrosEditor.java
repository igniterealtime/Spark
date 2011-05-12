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
package org.jivesoftware.fastpath.workspace.macros;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.Workpane;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.workgroup.ext.macros.Macro;
import org.jivesoftware.smackx.workgroup.ext.macros.MacroGroup;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.Table;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

public class MacrosEditor extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel initialResponseLabel = new JLabel();
    private JTextArea initialResponseField = new JTextArea();
    private RolloverButton newButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.SMALL_ADD_IMAGE));
    private RolloverButton deleteButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.SMALL_DELETE));
    private JButton saveButton = new JButton();
    private MacroTable table;
    private MacroGroup personalGroup = null;
    private JDialog dialog;


    public MacrosEditor() {
        table = new MacroTable();

        setLayout(new GridBagLayout());

        add(initialResponseLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(new JScrollPane(initialResponseField), new GridBagConstraints(0, 1, 1, 1, 1.0, .5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));

        ResourceUtils.resLabel(initialResponseLabel, initialResponseField, FpRes.getString("label.initial.response"));
        ResourceUtils.resButton(newButton, FpRes.getString("button.new"));
        ResourceUtils.resButton(deleteButton, FpRes.getString("button.delete"));
        ResourceUtils.resButton(saveButton, FpRes.getString("button.save"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        //buttonPanel.add(saveButton);
        add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        JScrollPane pane = new JScrollPane(table);
        pane.getViewport().setBackground(Color.white);

        // add table
        add(pane, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));


        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                createNewResponse();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                save();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                deleteRow();
            }
        });
    }

    public void showEditor(Component parent) {
        try {
            personalGroup = FastpathPlugin.getAgentSession().getMacros(false);
        }
        catch (XMPPException e) {
            Log.error("No personal macros set.");
            personalGroup = new MacroGroup();
        }

        Properties props = FastpathPlugin.getLitWorkspace().getWorkgroupProperties();
        String initialResponse = props.getProperty(Workpane.INITIAL_RESPONSE_PROPERTY);
        if (ModelUtil.hasLength(initialResponse)) {
            initialResponseField.setText(initialResponse);
        }

        List<Macro> macros = personalGroup.getMacros();
        Iterator<Macro> iter = macros.iterator();
        while (iter.hasNext()) {
            Macro macro = (Macro)iter.next();
            String title = macro.getTitle();
            String response = macro.getResponse();

            table.getTableModel().addRow(new Object[]{title, response});
        }

        dialog = MessageDialog.showComponent(FpRes.getString("title.personal.macros"), FpRes.getString("message.specify.personal.macros"), FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_24x24), this, parent, 600, 400, true);
        save();
    }


    /**
     * Private implementation of table for the CustomerTable.
     */
    private static final class MacroTable extends Table {

		private static final long serialVersionUID = 4777649199509083939L;

		MacroTable() {
            super(new String[]{FpRes.getString("title.response.name"), FpRes.getString("title.response.text")});

            getColumnModel().setColumnMargin(0);
            setSelectionBackground(SELECTION_COLOR);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowSelectionAllowed(true);
        }

        // Handle image rendering correctly
        public TableCellRenderer getCellRenderer(int row, int column) {
            if (column == 10) {
                return new JLabelRenderer(false);
            }
            else {
                return super.getCellRenderer(row, column);
            }
        }
    }

    private void save() {
        // Save Personal Macros

        MacroGroup macroGroup = new MacroGroup();
        int count = table.getRowCount();
        for (int i = 0; i < count; i++) {
            Macro macro = new Macro();
            String title = (String)table.getValueAt(i, 0);
            String value = (String)table.getValueAt(i, 1);
            macro.setTitle(title);
            macro.setResponse(value);
            macroGroup.addMacro(macro);
        }

        macroGroup.setTitle(FpRes.getString("title.personal"));

        try {
            FastpathPlugin.getAgentSession().saveMacros(macroGroup);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, FpRes.getString("message.macros.not.saved"), FpRes.getString("title.error"), JOptionPane.ERROR_MESSAGE);
        }

        String initialResponse = initialResponseField.getText();
        Properties props = FastpathPlugin.getLitWorkspace().getWorkgroupProperties();
        if (ModelUtil.hasLength(initialResponse)) {
            props.setProperty(Workpane.INITIAL_RESPONSE_PROPERTY, initialResponse);
            FastpathPlugin.getLitWorkspace().saveProperties(props);
        }
    }

    private void deleteRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            table.getTableModel().removeRow(selectedRow);

            ((DefaultTableModel)table.getModel()).fireTableRowsDeleted(selectedRow, selectedRow);
        }
    }

    private void createNewResponse() {
        final MacroPanel macroPanel = new MacroPanel();
        MessageDialog.showComponent(FpRes.getString("title.create.canned.response"), FpRes.getString("message.add.new.response"), FastpathRes.getImageIcon(FastpathRes.HELP2_24x24), macroPanel, dialog, 500, 400, true);
        String title = macroPanel.getTitle();
        String response = macroPanel.getResponse();

        if (ModelUtil.hasLength(title) && ModelUtil.hasLength(response)) {
            // add to table.
            table.getTableModel().addRow(new Object[]{title, response});
        }
    }
}
