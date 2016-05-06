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
package org.jivesoftware.spark.component;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Constructs a selection list with Checkboxes.
 *
 * @author Derek DeMoro
 */
public class CheckBoxList extends JPanel {
    private static final long serialVersionUID = 4145933151755357313L;
    private Map<JCheckBox, String> valueMap = new HashMap<>();
    private JPanel internalPanel = new JPanel();

    /**
     * Create the CheckBoxList UI.
     */
    public CheckBoxList() {
        setLayout(new BorderLayout());
        internalPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 5, 5, true, false));
        add(new JScrollPane(internalPanel), BorderLayout.CENTER);
    }

    /**
     * Add a checkbox with an associated value.
     *
     * @param box   the checkbox.
     * @param value the value bound to the checkbox.
     */
    public void addCheckBox(JCheckBox box, String value) {
        internalPanel.add(box);
        valueMap.put(box, value);
    }

    /**
     * Returns a list of selected checkbox values.
     *
     * @return list of selected checkbox values.
     */
    public List<String> getSelectedValues() {
        List<String> list = new ArrayList<>();
        for (JCheckBox checkbox : valueMap.keySet()) {
            if (checkbox.isSelected()) {
                String value = valueMap.get(checkbox);
                list.add(value);
            }
        }
        return list;
    }
}
