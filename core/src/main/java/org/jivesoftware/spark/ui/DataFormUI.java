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
package org.jivesoftware.spark.ui;

import org.jivesoftware.smackx.xdata.*;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.spark.component.CheckBoxList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static java.awt.GridBagConstraints.*;
import static java.awt.GridBagConstraints.NONE;

/**
 * Builds the UI for any DataForm (JEP-0004: Data Forms), and allow for creation
 * of an answer form to send back the server.
 */
public class DataFormUI extends JPanel {
    private static final long serialVersionUID = -6313707846021436765L;
    private final Map<String, JComponent> valueMap = new HashMap<>();
    private final DataForm form;
    private int row = 5;

    /**
     * Creates a new DataFormUI
     *
     * @param form the <code>DataForm</code> to build a UI with.
     */
    public DataFormUI(DataForm form) {
        this.setLayout(new GridBagLayout());
        this.form = form;
        buildUI();
        Insets insets = new Insets(0, 0, 0, 0);
        this.add(new JLabel(), new GridBagConstraints(0, row, 3, 1, 0, 1, CENTER, NONE, insets, 0, 0));
    }


    private void buildUI() {
        // Add default answers to the form to submit
        for (final FormField field : form.getFields()) {
            String variable = field.getFieldName();
            String label = field.getLabel();
            FormField.Type type = field.getType();
            List<? extends CharSequence> valueList = field.getValues();
            switch (type) {
                case bool: {
                    BooleanFormField booleanField = field.ifPossibleAsOrThrow(BooleanFormField.class);
                    boolean isSelected = booleanField.getValueAsBoolean();
                    JCheckBox box = new JCheckBox(label);
                    box.setSelected(isSelected);
                    addField(label, box, variable);
                    break;
                }
                case text_single:
                case jid_single: {
                    String v = "";
                    if (!valueList.isEmpty()) {
                        v = valueList.get(0).toString();
                    }
                    addField(label, new JTextField(v), variable);
                    break;
                }
                case text_multi:
                case jid_multi: {
                    String text = String.join(",", valueList);
                    addField(label, new JTextArea(text), variable);
                    break;
                }
                case text_private: {
                    String v = "";
                    if (!valueList.isEmpty()) {
                        v = valueList.get(0).toString();
                    }
                    addField(label, new JPasswordField(v), variable);
                    break;
                }
                case list_single: {
                    ListSingleFormField listSingleFormField = field.ifPossibleAsOrThrow(ListSingleFormField.class);
                    JComboBox<String> box = new JComboBox<>();
                    for (FormField.Option option : listSingleFormField.getOptions()) {
                        box.addItem(option.getValueString());
                    }
                    if (!valueList.isEmpty()) {
                        String defaultValue = valueList.get(0).toString();
                        box.setSelectedItem(defaultValue);
                    }
                    addField(label, box, variable);
                    break;
                }
                case list_multi: {
                    ListMultiFormField listMultiFormField = field.ifPossibleAsOrThrow(ListMultiFormField.class);
                    CheckBoxList checkBoxList = new CheckBoxList();
                    for (FormField.Option option : listMultiFormField.getOptions()) {
                        String optionLabel = option.getLabel();
                        String optionValue = option.getValueString();
                        boolean isSelected = valueList.contains(optionValue);
                        checkBoxList.addCheckBox(new JCheckBox(optionLabel, isSelected), optionValue);
                    }
                    addField(label, checkBoxList, variable);
                    break;
                }
                case fixed: {
                    if (!valueList.isEmpty()) {
                        String v = valueList.get(0).toString();
                        addField(label, new JLabel(v), variable);
                    }
                    break;
                }
                case hidden: {
                    // nothing to render
                    break;
                }
            }
        }
    }

    /**
     * Returns the answered DataForm.
     */
    public FillableForm getFilledForm() {
        // Now submit all information
        FillableForm answerForm = new FillableForm(form);
        for (Map.Entry<String, JComponent> entry : valueMap.entrySet()) {
            String answer = entry.getKey();
            JComponent o = entry.getValue();
            // Extract form values from components; populates submit form
            if (o instanceof JCheckBox) {
                boolean isSelected = ((JCheckBox) o).isSelected();
                answerForm.setAnswer(answer, isSelected);
            } else if (o instanceof JTextArea) {
                List<String> list = new ArrayList<>();
                String value = ((JTextArea) o).getText();
                StringTokenizer tokenizer = new StringTokenizer(value, ",", false);
                while (tokenizer.hasMoreTokens()) {
                    list.add(tokenizer.nextToken().trim());
                }
                // an empty list is not allowed
                if (!list.isEmpty()) {
                    answerForm.setAnswer(answer, list);
                }
            } else if (o instanceof JTextField) {
                String value = ((JTextField) o).getText();
                answerForm.setAnswer(answer, value);
            } else if (o instanceof JComboBox) {
                String value = (String) ((JComboBox<?>) o).getSelectedItem();
                answerForm.setAnswer(answer, value);
            } else if (o instanceof CheckBoxList) {
                List<String> list = ((CheckBoxList) o).getSelectedValues();
                // an empty list is not allowed
                if (!list.isEmpty()) {
                    answerForm.setAnswer(answer, list);
                }
            }
        }

        return answerForm;
    }


    /**
     * Add labeled component to form and map variable to the component
     */
    private void addField(String label, JComponent comp, String variable) {
        Insets insets = new Insets(5, 5, 5, 5);
        if (!(comp instanceof JCheckBox)) {
            this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
        if (comp instanceof JTextArea) {
            this.add(new JScrollPane(comp), new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 100, 50));
        } else if (comp instanceof JCheckBox) {
            this.add(comp, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, HORIZONTAL, insets, 0, 0));
        } else if (comp instanceof CheckBoxList) {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 0, 0, WEST, HORIZONTAL, insets, 0, 50));
        } else {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
        valueMap.put(variable, comp);
        row++;
    }

    public JComponent getComponent(String variable) {
        return valueMap.get(variable);
    }
}

