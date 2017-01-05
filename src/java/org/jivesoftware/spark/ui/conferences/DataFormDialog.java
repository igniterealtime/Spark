/**
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
package org.jivesoftware.spark.ui.conferences;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Option;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.CheckBoxList;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

public class DataFormDialog extends JPanel {
    private static final long serialVersionUID = -1536217028590811636L;
    private final Map<String,JComponent> valueMap = new HashMap<>();
    private int row = 0;
    JDialog dialog = null;


    public DataFormDialog(JFrame parent, final MultiUserChat chat, final Form submitForm) {
        dialog = new JDialog(parent, true);
        dialog.setTitle(Res.getString("title.configure.chat.room"));

        this.setLayout(new GridBagLayout());
        Form form = null;
        // Create the room
        try {
            form = chat.getConfigurationForm();
        }
        catch (XMPPException | SmackException e) {
            Log.error(e);
        }

        // Create a new form to submit based on the original form
        try {
            // Add default answers to the form to submit
            for ( final FormField field : form.getFields() ) {
                String variable = field.getVariable();
                String label = field.getLabel();
                FormField.Type type = field.getType();

                List<String> valueList = field.getValues();

                if (type.equals(FormField.Type.bool)) {
                    String o = valueList.get(0);
                    boolean isSelected = o.equals("1");
                    JCheckBox box = new JCheckBox(label);
                    box.setSelected(isSelected);
                    submitForm.setAnswer( variable, isSelected );
                    addField(label, box, variable);
                }
                else if (type.equals(FormField.Type.text_single) ||
                        type.equals(FormField.Type.jid_single)) {
                    String value = valueList.get(0);
                    submitForm.setAnswer( variable, value );
                    addField(label, new JTextField(value), variable);
                }
                else if (type.equals(FormField.Type.text_multi) ||
                        type.equals(FormField.Type.jid_multi)) {
                    StringBuilder buf = new StringBuilder();
                    final Iterator<String> iter = valueList.iterator();
                    while (iter.hasNext()) {
                        buf.append( iter.next() );

                        if (iter.hasNext()) {
                            buf.append(",");
                        }
                    }
                    submitForm.setAnswer( variable, valueList );
                    addField(label, new JTextArea(buf.toString()), variable);
                }
                else if (type.equals(FormField.Type.text_private)) {
                    if ( !valueList.isEmpty() )
                    {
                        submitForm.setAnswer( variable, valueList.get( 0 ) );
                    }
                    addField(label, new JPasswordField(), variable);
                }
                else if (type.equals(FormField.Type.list_single)) {
                    JComboBox box = new JComboBox();
                    for ( final FormField.Option option : field.getOptions() ) {
                        String value = option.getValue();
                        box.addItem(value);
                    }
                    if (valueList.size() > 0) {
                        String defaultValue = valueList.get(0);
                        box.setSelectedItem(defaultValue);
                        submitForm.setAnswer( variable, valueList );
                    }
                    addField(label, box, variable);
                }
                else if (type.equals(FormField.Type.list_multi)) {
                    CheckBoxList checkBoxList = new CheckBoxList();
                    final List<String> values = field.getValues();
                    for ( final Option option : field.getOptions() ) {
                        String optionLabel = option.getLabel();
                        String optionValue = option.getValue();
                        checkBoxList.addCheckBox(new JCheckBox(optionLabel, values.contains(optionValue)), optionValue);
                    }
                    submitForm.setAnswer( variable, valueList  );
                    addField(label, checkBoxList, variable);
                }
            }
        }
        catch (NullPointerException e) {
            Log.error(e);
        }


        JButton button = new JButton();
        ResourceUtils.resButton(button, Res.getString("button.update"));
        button.addActionListener( e -> {
            dialog.dispose();
            // Now submit all information
            updateRoomConfiguration(submitForm, chat);
        } );

        final JScrollPane pane = new JScrollPane(this);
        pane.getVerticalScrollBar().setBlockIncrement(200);
        pane.getVerticalScrollBar().setUnitIncrement(20);

        dialog.getContentPane().setLayout(new BorderLayout());

        dialog.getContentPane().add(pane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(button);

        JButton cancelButton = new JButton();
        ResourceUtils.resButton(cancelButton, Res.getString("button.cancel"));
        cancelButton.addActionListener( actionEvent -> dialog.dispose() );

        bottomPanel.add(cancelButton);

        dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(600, 400);
        GraphicUtils.centerWindowOnScreen(dialog);
        dialog.setVisible(true);


    }

    private void updateRoomConfiguration(Form submitForm, MultiUserChat chat) {
        for (Object o1 : valueMap.keySet()) {
            String answer = (String) o1;
            Object o = valueMap.get(answer);
            if (o instanceof JCheckBox) {
                boolean isSelected = ((JCheckBox) o).isSelected();
                submitForm.setAnswer(answer, isSelected);
            } else if (o instanceof JTextArea) {
                List<String> list = new ArrayList<>();
                String value = ((JTextArea) o).getText();
                StringTokenizer tokenizer = new StringTokenizer(value, ", ", false);
                while (tokenizer.hasMoreTokens()) {
                    list.add(tokenizer.nextToken());
                }
                if (list.size() > 0) {
                    submitForm.setAnswer(answer, list);
                }
            } else if (o instanceof JTextField) {
                String value = ((JTextField) o).getText();
                if (ModelUtil.hasLength(value)) {
                    submitForm.setAnswer(answer, value);
                }
            } else if (o instanceof JComboBox) {
                String value = (String) ((JComboBox) o).getSelectedItem();
                List<String> list = new ArrayList<>();
                list.add(value);
                if (list.size() > 0) {
                    submitForm.setAnswer(answer, list);
                }
            } else if (o instanceof CheckBoxList) {
                List<String> list = ((CheckBoxList) o).getSelectedValues();
                if (list.size() > 0) {
                    submitForm.setAnswer(answer, list);
                }
            }
        }


        try {
            chat.sendConfigurationForm(submitForm);
            RoomInfo info = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getRoomInfo( chat.getRoom() );
            //remove bookmark if any for non persistent room
            if (!info.isPersistent()) {
                BookmarkManager.getBookmarkManager(SparkManager.getConnection()).removeBookmarkedConference(info.getRoom());
            }
        }
        catch (XMPPException | SmackException e) {
            Log.error(e);
        }
    }

    private void addField(String label, JComponent comp, String variable) {

        if (!(comp instanceof JCheckBox)) {
            JLabel formLabel = new JLabel(label);
            formLabel.setFont(new Font("dialog", Font.BOLD, 10));
            this.add(formLabel, new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        }
        if (comp instanceof JTextArea) {
            JScrollPane pane = new JScrollPane(comp);
            pane.setBorder(BorderFactory.createTitledBorder(Res.getString("group.comma.delimited")));
            this.add(pane, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 75, 50));
        }
        else if (comp instanceof JCheckBox) {
            this.add(comp, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        }
        else if (comp instanceof CheckBoxList) {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 75, 50));
        }
        else {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 75, 0));

        }
        valueMap.put(variable, comp);
        row++;
    }


    public String getValue(String label) {
        Component comp = valueMap.get(label);
        if (comp instanceof JCheckBox) {
            return "" + ((JCheckBox)comp).isSelected();
        }

        if (comp instanceof JTextField) {
            return ((JTextField)comp).getText();
        }
        return null;
    }
}
