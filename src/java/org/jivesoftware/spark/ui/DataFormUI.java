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
package org.jivesoftware.spark.ui;

import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
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

/**
 * Builds the UI for any DataForm (JEP-0004: Data Forms), and allow for creation
 * of an answer form to send back the the server.
 */
public class DataFormUI extends JPanel {
	private static final long serialVersionUID = -6313707846021436765L;
	private final Map<String,JComponent> valueMap = new HashMap<String,JComponent>();
    private int row = 5;
    private Form searchForm;

    /**
     * Creates a new DataFormUI
     *
     * @param form the <code>DataForm</code> to build a UI with.
     */
    public DataFormUI(Form form) {
        this.setLayout(new GridBagLayout());
        this.searchForm = form;

        buildUI(form);

        this.add(new JLabel(), new GridBagConstraints(0, row, 3, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }


    private void buildUI(Form form) {
        // Add default answers to the form to submit
        Iterator<FormField> fields = form.getFields();
        while (fields.hasNext()) {
            FormField field = fields.next();
            String variable = field.getVariable();
            String label = field.getLabel();
            String type = field.getType();

            Iterator<?> iter = field.getValues();
            List<Object> valueList = new ArrayList<Object>();
            while (iter.hasNext()) {
                valueList.add(iter.next());
            }

            if (type.equals(FormField.TYPE_BOOLEAN)) {
                String o = (String)valueList.get(0);
                boolean isSelected = o.equals("1");
                JCheckBox box = new JCheckBox(label);
                box.setSelected(isSelected);
                addField(label, box, variable);
            }
            else if (type.equals(FormField.TYPE_TEXT_SINGLE) || type.equals(FormField.TYPE_JID_SINGLE)) {
                String v = "";
                if (valueList.size() > 0) {
                    v = (String)valueList.get(0);
                }
                addField(label, new JTextField(v), variable);
            }
            else if (type.equals(FormField.TYPE_TEXT_MULTI) ||
                    type.equals(FormField.TYPE_JID_MULTI)) {
                StringBuffer buf = new StringBuffer();
                iter = field.getOptions();
                while (iter.hasNext()) {
                    buf.append((String)iter.next());
                }
                addField(label, new JTextArea(buf.toString()), variable);
            }
            else if (type.equals(FormField.TYPE_TEXT_PRIVATE)) {
                addField(label, new JPasswordField(), variable);
            }
            else if (type.equals(FormField.TYPE_LIST_SINGLE)) {
                JComboBox box = new JComboBox();
                iter = field.getOptions();
                while (iter.hasNext()) {
                    FormField.Option option = (FormField.Option)iter.next();
                    box.addItem(option);
                }
                if (valueList.size() > 0) {
                    String defaultValue = (String)valueList.get(0);
                    box.setSelectedItem(defaultValue);
                }

                addField(label, box, variable);
            }
            else if (type.equals(FormField.TYPE_LIST_MULTI)) {
                CheckBoxList checkBoxList = new CheckBoxList();
                Iterator<?> i = field.getValues();
                while (i.hasNext()) {
                    String value = (String)i.next();
                    checkBoxList.addCheckBox(new JCheckBox(value), value);
                }
                addField(label, checkBoxList, variable);
            }
        }
    }

    /**
     * Returns the answered DataForm.
     *
     * @return the answered DataForm.
     */
    public Form getFilledForm() {
        // Now submit all information
        Iterator<String> valueIter = valueMap.keySet().iterator();
        Form answerForm = searchForm.createAnswerForm();
        while (valueIter.hasNext()) {
            String answer = (String)valueIter.next();
            Object o = valueMap.get(answer);
            if (o instanceof JCheckBox) {
                boolean isSelected = ((JCheckBox)o).isSelected();
                answerForm.setAnswer(answer, isSelected);
            }
            else if (o instanceof JTextArea) {
                List<String> list = new ArrayList<String>();
                String value = ((JTextArea)o).getText();
                StringTokenizer tokenizer = new StringTokenizer(value, ", ", false);
                while (tokenizer.hasMoreTokens()) {
                    list.add(tokenizer.nextToken());
                }
                if (list.size() > 0) {
                    answerForm.setAnswer(answer, list);
                }
            }
            else if (o instanceof JTextField) {
                String value = ((JTextField)o).getText();
                if (ModelUtil.hasLength(value)) {
                    answerForm.setAnswer(answer, value);
                }
            }
            else if (o instanceof JComboBox) {
                Object v = ((JComboBox)o).getSelectedItem();
                String value;
                if (v instanceof FormField.Option) {
                    value = ((FormField.Option)v).getValue();
                }
                else {
                    value = (String)v;
                }
                List<String> list = new ArrayList<String>();
                list.add(value);
                if (list.size() > 0) {
                    answerForm.setAnswer(answer, list);
                }
            }
            else if (o instanceof CheckBoxList) {
                List<String> list = ((CheckBoxList)o).getSelectedValues();
                if (list.size() > 0) {
                    answerForm.setAnswer(answer, list);
                }
            }
        }

        return answerForm;
    }


    private void addField(String label, JComponent comp, String variable) {
        if (!(comp instanceof JCheckBox)) {
            this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        }
        if (comp instanceof JTextArea) {
            this.add(new JScrollPane(comp), new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 100, 50));
        }
        else if (comp instanceof JCheckBox) {
            this.add(comp, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        }
        else if (comp instanceof CheckBoxList) {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 50));
        }
        else {
            this.add(comp, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        }
        valueMap.put(variable, comp);
        row++;
    }

    public Component getComponent(String label) {
        return valueMap.get(label);
    }
}

