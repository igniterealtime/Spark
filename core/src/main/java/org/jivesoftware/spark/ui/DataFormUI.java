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

import org.jivesoftware.smackx.xdata.BooleanFormField;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormFieldWithOptions;
import org.jivesoftware.smackx.xdata.ListSingleFormField;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.spark.component.CheckBoxList;
import org.jivesoftware.spark.util.ModelUtil;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static java.awt.GridBagConstraints.*;
import static java.awt.GridBagConstraints.NONE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.jivesoftware.smackx.xdata.FormField.Type.*;

/**
 * Builds the UI for any DataForm (JEP-0004: Data Forms), and allow for creation
 * of an answer form to send back the server.
 */
public class DataFormUI extends JPanel {
    private static final long serialVersionUID = -6313707846021436765L;
    private final Map<String, JComponent> valueMap = new HashMap<>();
    private final DataForm searchForm;
    private int row = 5;

    /**
     * Creates a new DataFormUI
     *
     * @param form the <code>DataForm</code> to build a UI with.
     */
    public DataFormUI(DataForm form) {
        this.setLayout(new GridBagLayout());
        this.searchForm = form;
        buildUI(form);
        Insets insets = new Insets(0, 0, 0, 0);
        this.add(new JLabel(), new GridBagConstraints(0, row, 3, 1, 0, 1, CENTER, NONE, insets, 0, 0));
    }


    private void buildUI(DataForm form) {
        // Add default answers to the form to submit
        for (final FormField field : form.getFields()) {
            String variable = field.getFieldName();
            String label = field.getLabel();
            FormField.Type type = field.getType();

            List<? extends CharSequence> valueList = field.getValues();
            if (type == bool) {
                BooleanFormField booleanField = field.ifPossibleAsOrThrow(BooleanFormField.class);
                boolean isSelected = booleanField.getValueAsBoolean();
                JCheckBox box = new JCheckBox(label);
                box.setSelected(isSelected);
                addField(label, box, variable);
            } else if (type == text_single || type == jid_single) {
                String v = "";
                if (!valueList.isEmpty()) {
                    v = valueList.get(0).toString();
                }
                addField(label, new JTextField(v), variable);
            } else if (type == text_multi ||
                type == jid_multi) {
                StringBuilder buf = new StringBuilder();
                if (field instanceof FormFieldWithOptions) {
                    FormFieldWithOptions formFieldWithOptions = (FormFieldWithOptions) field;
                    for (FormField.Option option : formFieldWithOptions.getOptions()) {
                        buf.append(option);
                    }
                }
                addField(label, new JTextArea(buf.toString()), variable);
            } else if (type == text_private) {
                addField(label, new JPasswordField(), variable);
            } else if (type == list_single) {
                ListSingleFormField listSingleFormField = field.ifPossibleAsOrThrow(ListSingleFormField.class);
                JComboBox<FormField.Option> box = new JComboBox<>();
                for (final FormField.Option option : listSingleFormField.getOptions()) {
                    box.addItem(option);
                }
                if (!valueList.isEmpty()) {
                    String defaultValue = valueList.get(0).toString();
                    box.setSelectedItem(defaultValue);
                }
                addField(label, box, variable);
            } else if (type == list_multi) {
                CheckBoxList checkBoxList = new CheckBoxList();
                for (CharSequence value : field.getValues()) {
                    checkBoxList.addCheckBox(new JCheckBox(value.toString()), value.toString());
                }
                addField(label, checkBoxList, variable);
            }
        }
    }

    /**
     * Returns the answered DataForm.
     */
    public DataForm getFilledForm() {
        // Now submit all information
        FillableForm answerForm = new FillableForm(searchForm);
        for (Map.Entry<String, JComponent> entry : valueMap.entrySet()) {
            String answer = entry.getKey();
            JComponent o = entry.getValue();
            if (o instanceof JCheckBox) {
                boolean isSelected = ((JCheckBox) o).isSelected();
                answerForm.setAnswer(answer, isSelected);
            } else if (o instanceof JTextArea) {
                List<String> list = new ArrayList<>();
                String value = ((JTextArea) o).getText();
                StringTokenizer tokenizer = new StringTokenizer(value, ", ", false);
                while (tokenizer.hasMoreTokens()) {
                    list.add(tokenizer.nextToken());
                }
                if (!list.isEmpty()) {
                    answerForm.setAnswer(answer, list);
                }
            } else if (o instanceof JTextField) {
                String value = ((JTextField) o).getText();
                if (!isBlank(value)) {
                    answerForm.setAnswer(answer, value);
                }
            } else if (o instanceof JComboBox) {
                Object v = ((JComboBox<?>) o).getSelectedItem();
                String value;
                if (v instanceof FormField.Option) {
                    value = ((FormField.Option) v).getValueString();
                } else {
                    value = (String) v;
                }
                List<String> list = new ArrayList<>();
                list.add(value);
                if (!list.isEmpty()) {
                    answerForm.setAnswer(answer, list);
                }
            } else if (o instanceof CheckBoxList) {
                List<String> list = ((CheckBoxList) o).getSelectedValues();
                if (!list.isEmpty()) {
                    answerForm.setAnswer(answer, list);
                }
            }
        }

        return answerForm.getDataFormToSubmit();
    }


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

    public Component getComponent(String label) {
        return valueMap.get(label);
    }
}

