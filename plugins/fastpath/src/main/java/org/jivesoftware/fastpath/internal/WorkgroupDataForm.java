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
package org.jivesoftware.fastpath.internal;

import org.jivesoftware.spark.component.CheckBoxList;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormFieldWithOptions;
import org.jivesoftware.smackx.xdata.ListSingleFormField;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.form.Form;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Builds the UI for any DataForm (JEP-0004: Data Forms), and allow for creation
 * of an answer form to send back the the server.
 */
public class WorkgroupDataForm extends JPanel {
	private static final long serialVersionUID = -2368907321868842234L;
	private final Map<String, JComponent> valueMap = new HashMap<>();
    private int row = 5;
    private final Form searchForm;
    private final Map<String, String> presetVariables;
    private final List<String> requiredList = new ArrayList<>();
    private EnterListener listener;


    /**
     * Creates a new DataFormUI
     *
     * @param form the <code>DataForm</code> to build a UI with.
     */
    public WorkgroupDataForm(Form form, Map<String, String> presetVariables) {
        this.presetVariables  = presetVariables;
        this.setLayout(new GridBagLayout());
        this.searchForm = form;

        buildUI(form);

        this.add(new JLabel(), new GridBagConstraints(0, row, 3, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }


    private void buildUI(Form form) {
        // Add default answers to the form to submit
        for ( final FormField field : form.getDataForm().getFields() ) {
            String variable = field.getFieldName();
            if(field.isRequired()){
                requiredList.add(variable);
            }

            if(presetVariables.containsKey(variable)){
                continue;
            }

            String label = field.getLabel();
            FormField.Type type = field.getType();

            List<CharSequence> valueList = new ArrayList<>(field.getValues());

            if (type.equals(FormField.Type.bool)) {
                String o = valueList.get(0).toString();
                boolean isSelected = o.equals("1");
                JCheckBox box = new JCheckBox(label);
                box.setSelected(isSelected);
                addField(label, box, variable);
            }
            else if (type.equals(FormField.Type.text_single) || type.equals(FormField.Type.jid_single)) {
                String v = "";
                if (valueList.size() > 0) {
                    v = valueList.get(0).toString();
                }
                addField(label, new JTextField(v), variable);
            }
            else if (type.equals(FormField.Type.text_multi) ||
                type.equals(FormField.Type.jid_multi)) {
                StringBuilder buf = new StringBuilder();
                if (field instanceof FormFieldWithOptions) {
                    FormFieldWithOptions formFieldWithOptions = (FormFieldWithOptions) field;
                    for ( final FormField.Option option : formFieldWithOptions.getOptions() ) {
                        buf.append(option);
                    }
                }
                addField(label, new JTextArea(buf.toString()), variable);
            }
            else if (type.equals(FormField.Type.text_private)) {
                addField(label, new JPasswordField(), variable);
            }
            else if (type.equals(FormField.Type.list_single)) {
                ListSingleFormField listSingleFormField = field.ifPossibleAsOrThrow(ListSingleFormField.class);
                JComboBox<FormField.Option> box = new JComboBox<>();
                for ( final FormField.Option option : listSingleFormField.getOptions() ) {
                    box.addItem(option);
                }
                if (valueList.size() > 0) {
                    String defaultValue = (String)valueList.get(0);
                    box.setSelectedItem(defaultValue);
                }

                addField(label, box, variable);
            }
            else if (type.equals(FormField.Type.list_multi)) {
                CheckBoxList checkBoxList = new CheckBoxList();
                for ( final CharSequence value : field.getValues() ) {
                    checkBoxList.addCheckBox(new JCheckBox(value.toString()), value.toString());
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
    public FillableForm getFilledForm() {
        // Now submit all information
        Iterator<String> valueIter = valueMap.keySet().iterator();
        FillableForm answerForm = searchForm.getFillableForm();
        while (valueIter.hasNext()) {
            String answer = valueIter.next();
            Object o = valueMap.get(answer);
            if (o instanceof JCheckBox) {
                boolean isSelected = ((JCheckBox)o).isSelected();
                answerForm.setAnswer(answer, isSelected);
            }
            else if (o instanceof JTextArea) {
                List<String> list = new ArrayList<>();
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
                Object v = ((JComboBox<?>) o).getSelectedItem();
                String value = (v instanceof FormField.Option) ? ((FormField.Option) v).getValue().toString() : (String) v;

                List<String> list = new ArrayList<>();
                list.add(value);
                if (list.size() > 0) {
                    answerForm.setAnswer(answer, list);
                }
            }
            else if (o instanceof CheckBoxList) {
                List<? extends CharSequence> list = ((CheckBoxList) o).getSelectedValues();
                if (list.size() > 0) {
                    answerForm.setAnswer(answer, list);
                }
            }
        }

        for (Object o : presetVariables.keySet()) {
            String variable = (String) o;
            String value = presetVariables.get(variable);
            answerForm.setAnswer(variable, value);
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

        comp.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    if(listener != null){
                        listener.enterPressed();
                    }
                }
            }
        });
    }

    public Component getComponent(String label) {
        return valueMap.get(label);
    }


    private String getValue(String label) {
        Component comp = valueMap.get(label);
        if (comp instanceof JCheckBox) {
            return "" + ((JCheckBox)comp).isSelected();
        }

        if (comp instanceof JTextField) {
            return ((JTextField)comp).getText();
        }
        return null;
    }

    public void setEnterListener(EnterListener listener){
        this.listener = listener;
    }

    public interface EnterListener {

        void enterPressed();
    }
}


