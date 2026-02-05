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
import org.jivesoftware.smackx.xdata.BooleanFormField;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Option;
import org.jivesoftware.smackx.xdata.ListMultiFormField;
import org.jivesoftware.smackx.xdata.ListSingleFormField;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.form.Form;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.CheckBoxList;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

public class DataFormDialog extends JPanel {
    private final Map<String, JComponent> valueMap = new HashMap<>();
    private int row = 0;
    private final JDialog dialog;

    public DataFormDialog(JFrame parent, final MultiUserChat chat, final FillableForm submitForm) {
        dialog = new JDialog(parent, true);
        dialog.setTitle(Res.getString("title.configure.chat.room"));

        this.setLayout(new GridBagLayout());
        Form form = null;
        // Create the room
        try {
            form = chat.getConfigurationForm();
        } catch (XMPPException | SmackException | InterruptedException e) {
            // TODO: Just logging the exception wont do it and actually just cause an NPE below, we need to handle it
            // better.
            Log.error(e);
        }

        // Create a new form to submit based on the original form
        try {
            // Add default answers to the form to submit
            for (final FormField field : form.getDataForm().getFields()) {
                String variable = field.getFieldName();
                String label = field.getLabel();
                FormField.Type type = field.getType();

                List<? extends CharSequence> valueList = field.getValues();

                if (type == FormField.Type.text_private) {
                    String value = null;
                    if (!valueList.isEmpty()) {
                        value = valueList.get(0).toString();
                        submitForm.setAnswer(variable, value);
                    }
                    addField(label, new JPasswordField(value), variable);
                }

                if (!valueList.isEmpty()) {
                    switch (type) {
                        case bool: {
                            BooleanFormField booleanField = field.ifPossibleAsOrThrow(BooleanFormField.class);
                            boolean isSelected = booleanField.getValueAsBoolean();
                            JCheckBox box = new JCheckBox(label);
                            box.setSelected(isSelected);
                            submitForm.setAnswer(variable, isSelected);
                            addField(label, box, variable);
                            break;
                        }
                        case text_single:
                        case jid_single: {
                            String value = valueList.get(0).toString();
                            submitForm.setAnswer(variable, value);
                            addField(label, new JTextField(value), variable);
                            break;
                        }
                        case text_multi:
                        case jid_multi: {
                            String text = String.join(",", valueList);
                            submitForm.setAnswer(variable, valueList);
                            addField(label, new JTextArea(text), variable);
                            break;
                        }
                        case list_single: {
                            ListSingleFormField listSingleFormField = field.ifPossibleAsOrThrow(ListSingleFormField.class);
                            JComboBox<String> box = new JComboBox<>();
                            for (Option option : listSingleFormField.getOptions()) {
                                box.addItem(option.getValueString());
                            }
                            String defaultValue = valueList.get(0).toString();
                            box.setSelectedItem(defaultValue);
                            submitForm.setAnswer(variable, valueList.get(0));
                            addField(label, box, variable);
                            break;
                        }
                        case list_multi: {
                            ListMultiFormField listMultiFormField = field.ifPossibleAsOrThrow(ListMultiFormField.class);
                            CheckBoxList checkBoxList = new CheckBoxList();
                            final List<? extends CharSequence> values = field.getValues();
                            for (Option option : listMultiFormField.getOptions()) {
                                String optionLabel = option.getLabel();
                                String optionValue = option.getValueString();
                                checkBoxList.addCheckBox(new JCheckBox(optionLabel, values.contains(optionValue)), optionValue);
                            }
                            submitForm.setAnswer(variable, valueList);
                            addField(label, checkBoxList, variable);
                            break;
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.error(e);
            // TODO: Why do we continue here as nothing had happened? If there is an NPE somewhere in this block, then
            // we should fix it, instead of masking it. Remove this try/catch block and see if it still appears, if so:
            // fix it.
        }


        JButton button = new JButton();
        ResourceUtils.resButton(button, Res.getString("button.update"));
        button.addActionListener(e -> {
            dialog.dispose();
            // Now submit all information
            updateRoomConfiguration(submitForm, chat);
        });

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
        cancelButton.addActionListener(actionEvent -> dialog.dispose());

        bottomPanel.add(cancelButton);

        dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(600, 400);
        GraphicUtils.centerWindowOnScreen(dialog);
        dialog.setVisible(true);
    }

    private void updateRoomConfiguration(FillableForm submitForm, MultiUserChat chat) {
        for (Map.Entry<String, JComponent> answerComp : valueMap.entrySet()) {
            String answer = answerComp.getKey();
            JComponent o = answerComp.getValue();
            // Extract form values from components; populates submit form
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
                if (!list.isEmpty()) {
                    submitForm.setAnswer(answer, list);
                }
            } else if (o instanceof JTextField) {
                String value = ((JTextField) o).getText();
                if (ModelUtil.hasLength(value)) {
                    submitForm.setAnswer(answer, value);
                }
            } else if (o instanceof JComboBox) {
                String value = (String) ((JComboBox<?>) o).getSelectedItem();
                List<String> list = new ArrayList<>(1);
                list.add(value);
                submitForm.setAnswer(answer, list.stream().iterator().next());
            } else if (o instanceof CheckBoxList) {
                List<String> list = ((CheckBoxList) o).getSelectedValues();
                if (!list.isEmpty()) {
                    submitForm.setAnswer(answer, list);
                }
            }
        }

        try {
            chat.sendConfigurationForm(submitForm);
            MultiUserChatManager mucManager = SparkManager.getMucManager();
            RoomInfo info = mucManager.getRoomInfo(chat.getRoom());
            // Remove bookmark if any for non-persistent room
            if (!info.isPersistent()) {
                BookmarkManager.getBookmarkManager(SparkManager.getConnection()).removeBookmarkedConference(info.getRoom());
            }
        } catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
            MessageDialog.showErrorDialog(Res.getString("group.send_config.error"), e);
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
        } else if (comp instanceof JCheckBox) {
            this.add(comp, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        } else if (comp instanceof CheckBoxList) {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 75, 50));
        } else {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 75, 0));

        }
        valueMap.put(variable, comp);
        row++;
    }


    public String getValue(String label) {
        Component comp = valueMap.get(label);
        if (comp instanceof JCheckBox) {
            return "" + ((JCheckBox) comp).isSelected();
        }

        if (comp instanceof JTextField) {
            return ((JTextField) comp).getText();
        }
        return null;
    }
}
