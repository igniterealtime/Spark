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

import org.jdesktop.swingx.JXDatePicker;
import org.jivesoftware.smackx.xdata.BooleanFormField;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormFieldChildElement;
import org.jivesoftware.smackx.xdata.ListMultiFormField;
import org.jivesoftware.smackx.xdata.ListSingleFormField;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.BasicValidateElement;
import org.jivesoftware.spark.component.CheckBoxList;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static org.jivesoftware.spark.util.GraphicUtils.localDatePickerGet;
import static org.jivesoftware.spark.util.GraphicUtils.localDatePickerSet;

/**
 * Builds the UI for any DataForm (JEP-0004: Data Forms), and allow for creation
 * of an answer form to send back the server.
 */
public class DataFormUI extends JPanel {
    private final Map<String, UiField> valueMap = new HashMap<>();
    private final DataForm form;
    private int row = 5;

    private abstract static class UiField {
        protected final String variable;
        protected final String label;

        public UiField(FormField field) {
            this.variable = field.getFieldName();
            this.label = field.getLabel();
        }

        public abstract Object getValue();

        public abstract void setValue(Object fieldValue);

        /**
         * Add labeled component to form and map variable to the component
         */
        public abstract void addField();
    }

    private class FixedUiField extends UiField {
        private final JLabel comp;

        private FixedUiField(FormField field) {
            super(field);
            String value = field.getFirstValue();
            comp = value != null ? new JLabel(value) : null;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object fieldValue) {
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(comp, new GridBagConstraints(0, row, 2, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
    }

    private class BooleanUiField extends UiField {
        private final JCheckBox comp;

        private BooleanUiField(FormField field) {
            super(field);
            comp = new JCheckBox(label);
            BooleanFormField booleanField = field.ifPossibleAsOrThrow(BooleanFormField.class);
            boolean isSelected = booleanField.getValueAsBoolean();
            setValue(isSelected);
        }

        @Override
        public Boolean getValue() {
            return comp.isSelected();
        }

        @Override
        public void setValue(Object isSelected) {
            comp.setSelected((Boolean) isSelected);
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(comp, new GridBagConstraints(0, row, 1, 1, 0, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
    }

    private class TextSingleUiField extends UiField {
        private final JComponent comp;
        private final String datatype;

        private TextSingleUiField(FormField field) {
            super(field);
            String v = field.getFirstValue();
            datatype = getValidationDataType(field);
            if (datatype == null) {
                JTextField textField = new JTextField();
                comp = textField;
                setValue(v);
                return;
            }
            switch (datatype) {
                case "xs:byte":
                case "xs:short":
                case "xs:integer":
                case "xs:int":
                case "xs:long": {
                    JSpinner spinner = new JSpinner();
                    comp = spinner;
                    break;
                }
                case "xs:dateTime": {
                    JXDatePicker datePicker = new JXDatePicker();
                    comp = datePicker;
                    break;
                }
                case "xs:time": {
                    SpinnerDateModel model = new SpinnerDateModel();
                    JSpinner spinner = new JSpinner(model);
                    spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
                    comp = spinner;
                    break;
                }
                case "xs:language": {
                    JTextField textField = new JTextField();
                    comp = textField;
                    break;
                }
                case "xs:decimal":
                case "xs:double":
                default: {
                    JTextField textField = new JTextField();
                    comp = textField;
                    break;
                }
            }
            setValue(v);
        }

        @Override
        public String getValue() {
            if (datatype == null) {
                return ((JTextField) comp).getText();
            }
            switch (datatype) {
                case "xs:byte":
                case "xs:short":
                case "xs:integer":
                case "xs:int":
                case "xs:long": {
                    Object value = ((JSpinner) comp).getValue();
                    String valueStr = value != null ? value.toString() : null;
                    return valueStr;
                }
                case "xs:dateTime": {
                    return localDatePickerGet((JXDatePicker) comp);
                }
                case "xs:time": {
                    Object value = ((JSpinner) comp).getValue();
                    String valueStr = value != null ? value.toString() : null;
                    return valueStr;
                }
                case "xs:language": {
                    return ((JTextField) comp).getText();
                }
                case "xs:decimal":
                case "xs:double":
                default: {
                    return ((JTextField) comp).getText();
                }
            }
        }

        @Override
        public void setValue(Object textVal) {
            if (datatype == null) {
                ((JTextField) comp).setText((String) textVal);
                return;
            }
            switch (datatype) {
                case "xs:byte":
                case "xs:short":
                case "xs:integer":
                case "xs:int":
                case "xs:long": {
                    try {
                        Long numVal = Long.valueOf((String) textVal);
                        ((JSpinner) comp).setValue(numVal);
                    } catch (Exception e) {
                        Log.warning("bad number field value " + variable + ": " + e);
                    }
                    return;
                }
                case "xs:dateTime": {
                    localDatePickerSet((JXDatePicker) comp, (String) textVal);
                    return;
                }
                case "xs:time": {
                    ((JSpinner) comp).setValue(textVal);
                    return;
                }
                case "xs:language": {
                    ((JTextField) comp).setText((String) textVal);
                    return;
                }
                case "xs:decimal":
                case "xs:double":
                default: {
                    ((JTextField) comp).setText((String) textVal);
                    return;
                }
            }
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
            DataFormUI.this.add(comp, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
    }

    private class JidSingleUiField extends UiField {
        private final JTextField comp = new JTextField();

        private JidSingleUiField(FormField field) {
            super(field);
            String v = field.getFirstValue();
            setValue(v);
        }

        @Override
        public String getValue() {
            return comp.getText();
        }

        @Override
        public void setValue(Object jid) {
            comp.setText((String) jid);
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
            DataFormUI.this.add(comp, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
    }

    private class TextMultiUiField extends UiField {
        private final JTextArea comp = new JTextArea();

        private TextMultiUiField(FormField field) {
            super(field);
            setValue(field.getValues());
        }

        @Override
        public List<String> getValue() {
            String value = comp.getText();
            String[] parts = value.split("[,\n]");
            List<String> list = new ArrayList<>(parts.length);
            for (String token : parts) {
                String t = token.trim();
                if (!t.isEmpty()) {
                    list.add(t);
                }
            }
            return list;
        }

        @Override
        public void setValue(Object values) {
            String text = String.join("\n", (Iterable) values);
            comp.setText(text);
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
            DataFormUI.this.add(new JScrollPane(comp), new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 100, 50));
        }
    }

    private class TextPrivateUiField extends UiField {
        private final JPasswordField comp = new JPasswordField();

        private TextPrivateUiField(FormField field) {
            super(field);
            String v = field.getFirstValue();
            setValue(v);
        }

        @Override
        public String getValue() {
            char[] pwd = comp.getPassword();
            return new String(pwd);
        }

        @Override
        public void setValue(Object fieldValue) {
            comp.setText((String) fieldValue);
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
            DataFormUI.this.add(comp, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
    }

    private class ListSingleUiField extends UiField {
        private final JComboBox<String> comp = new JComboBox<>();

        private ListSingleUiField(FormField field) {
            super(field);
            ListSingleFormField listSingleFormField = field.ifPossibleAsOrThrow(ListSingleFormField.class);
            for (FormField.Option option : listSingleFormField.getOptions()) {
                comp.addItem(option.getValueString());
            }
            String v = field.getFirstValue();
            setValue(v);
        }

        @Override
        public Object getValue() {
            return comp.getSelectedItem();
        }

        @Override
        public void setValue(Object fieldValue) {
            comp.setSelectedItem(fieldValue);
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
            DataFormUI.this.add(comp, new GridBagConstraints(1, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
    }

    private class ListMultiUiField extends UiField {
        private final CheckBoxList comp = new CheckBoxList();

        private ListMultiUiField(FormField field) {
            super(field);
            List<? extends CharSequence> valueList = field.getValues();
            ListMultiFormField listMultiFormField = field.ifPossibleAsOrThrow(ListMultiFormField.class);
            for (FormField.Option option : listMultiFormField.getOptions()) {
                String optionLabel = option.getLabel();
                String optionValue = option.getValueString();
                boolean isSelected = valueList.contains(optionValue);
                comp.addCheckBox(new JCheckBox(optionLabel, isSelected), optionValue);
            }
//            setValue(valueList);
        }

        @Override
        public Object getValue() {
            return comp.getSelectedValues();
        }

        @Override
        public void setValue(Object fieldValue) {
            // not implemented
        }

        @Override
        public void addField() {
            Insets insets = new Insets(5, 5, 5, 5);
            DataFormUI.this.add(new JLabel(label), new GridBagConstraints(0, row, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
            DataFormUI.this.add(comp, new GridBagConstraints(1, row, 1, 1, 0, 0, WEST, HORIZONTAL, insets, 0, 50));
        }
    }

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
            FormField.Type type = field.getType();
            UiField uiField;
            switch (type) {
                case bool: {
                    uiField = new BooleanUiField(field);
                    break;
                }
                case text_single: {
                    uiField = new TextSingleUiField(field);
                    break;
                }
                case jid_single: {
                    uiField = new JidSingleUiField(field);
                    break;
                }
                case text_multi:
                case jid_multi: {
                    uiField = new TextMultiUiField(field);
                    break;
                }
                case text_private: {
                    uiField = new TextPrivateUiField(field);
                    break;
                }
                case list_single: {
                    uiField = new ListSingleUiField(field);
                    break;
                }
                case list_multi: {
                    uiField = new ListMultiUiField(field);
                    break;
                }
                case fixed: {
                    uiField = new FixedUiField(field);
                    break;
                }
                case hidden:
                default: {
                    // nothing to render
                    uiField = null;
                    break;
                }
            }
            if (uiField != null) {
                uiField.addField();
                row++;
                valueMap.put(variable, uiField);
            }
        }
    }

    /**
     * Returns the answered DataForm.
     */
    public FillableForm getFilledForm() {
        // Now submit all information
        FillableForm answerForm = new FillableForm(form);
        for (Map.Entry<String, UiField> entry : valueMap.entrySet()) {
            String answer = entry.getKey();
            UiField uiField = entry.getValue();
            Object value = uiField.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof Boolean) {
                answerForm.setAnswer(answer, (Boolean) value);
            } else if (value instanceof List) {
                answerForm.setAnswer(answer, (List<String>) value);
            } else {
                answerForm.setAnswer(answer, value.toString());
            }
        }

        return answerForm;
    }

    public boolean setFieldValue(String variable, String fieldValue) {
        UiField field = valueMap.get(variable);
        if (!(field instanceof TextSingleUiField)) {
            return false;
        }
        field.setValue(fieldValue);
        return true;
    }

    private static String getValidationDataType(FormField field) {
        List<FormFieldChildElement> childElements = field.getFormFieldChildElements(ValidateElement.QNAME);
        for (FormFieldChildElement childEl : childElements) {
            if (childEl instanceof BasicValidateElement) {
                BasicValidateElement basicValidateEl = (BasicValidateElement) childEl;
                return basicValidateEl.getDatatype();
            }
        }
        return null;
    }
}

