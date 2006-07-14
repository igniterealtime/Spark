/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Constructs a selection list with Checkboxes.
 */
public class CheckBoxList extends JPanel {
    private Map valueMap = new HashMap();
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
    public List getSelectedValues() {
        List list = new ArrayList();
        Iterator iter = valueMap.keySet().iterator();
        while (iter.hasNext()) {
            JCheckBox checkbox = (JCheckBox)iter.next();
            if (checkbox.isSelected()) {
                String value = (String)valueMap.get(checkbox);
                list.add(value);
            }
        }
        return list;
    }
}
