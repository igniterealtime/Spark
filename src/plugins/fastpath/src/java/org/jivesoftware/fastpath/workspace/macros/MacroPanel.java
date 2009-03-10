/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date:  $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package org.jivesoftware.fastpath.workspace.macros;

import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.jivesoftware.fastpath.FpRes;


/**
 * UI to create a single personal macro within the FastPath Client.
 */
public class MacroPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel titleLabel;
    private JLabel typeLabel;
    private JLabel responseLabel;

    private JTextField titleField;
    private JComboBox typeField;
    private JTextArea responseField;

    public MacroPanel() {
        titleLabel = new JLabel();
        typeLabel = new JLabel();
        responseLabel = new JLabel();

        titleField = new JTextField();

        String[] items = {"Text", "URL", "Image"};
        typeField = new JComboBox(items);

        responseField = new JTextArea();


        // Set layout
        setLayout(new GridBagLayout());

        ResourceUtils.resLabel(titleLabel, titleField, FpRes.getString("label.title") + ":");
        ResourceUtils.resLabel(typeLabel, typeField, FpRes.getString("label.type") + ":");
        ResourceUtils.resLabel(responseLabel, responseField, FpRes.getString("label.response") + ":");
        

        // Add Title Label
        add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(titleField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            /*
        // Add Type Label
        add(typeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(typeField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            */
        // Add Response Field
        add(responseLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(new JScrollPane(responseField), new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Returns the title specified in the dialog.
     * @return the title
     */
    public String getTitle() {
        return titleField.getText();
    }

    /**
     * Return the type of response.
     * @return the type of response.
     */
    public int getType() {
        return typeField.getSelectedIndex();
    }

    /**
     * Return the response for this macro.
     * @return the response for this macro.
     */
    public String getResponse() {
        return responseField.getText();
    }
}
