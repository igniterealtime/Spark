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
package org.jivesoftware.fastpath.workspace.search;

import org.jivesoftware.fastpath.FpRes;

import java.util.Date;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;


public class SearchItem extends JPanel {
	private static final long serialVersionUID = -6319272932605736970L;
	private JLabel fullNameLabel = new JLabel();
    private JLabel dateLabel = new JLabel();
    private JLabel questionLabel = new JLabel();

    private String sessionID;

    public SearchItem(String fullName, Date date, String question) {
        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        setLayout(new GridBagLayout());

        final JLabel nameLabel = new JLabel();
        nameLabel.setText(FpRes.getString("name") + ":");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(fullNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(dateLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        final JLabel questionTitle = new JLabel();
        questionTitle.setText(FpRes.getString("question") + ":");
        questionTitle.setFont(new Font("Dialog", Font.BOLD, 11));

        add(questionTitle, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        add(questionLabel, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        final SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        String theDate = simpleFormat.format(date);

        fullNameLabel.setText(fullName);
        dateLabel.setText(theDate);
        questionLabel.setText(question);
    }

    public String getToolTipText(){
        StringBuffer buf = new StringBuffer();
        buf.append("<html><body>");
        buf.append("<table width=200><tr><td>"+FpRes.getString("question") + ": "+questionLabel.getText()+"</td></tr></table></body></html>");
        return buf.toString();
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }

    /**
     * Lets make sure that the panel doesn't stretch past the
     * scrollpane view pane.
     *
     * @return the preferred dimension
     */
    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }
}
