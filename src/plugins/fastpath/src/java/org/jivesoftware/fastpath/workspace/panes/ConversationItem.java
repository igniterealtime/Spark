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
package org.jivesoftware.fastpath.workspace.panes;

import org.jivesoftware.fastpath.FpRes;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversationItem extends JPanel {
	private static final long serialVersionUID = 3596381734502962203L;
	private JLabel agentLabel = new JLabel();
    private JLabel visitorLabel = new JLabel();
    private JLabel emailLabel = new JLabel();
    private JLabel dateLabel = new JLabel();
    private JLabel questionLabel = new JLabel();

    private String sessionID;

    public ConversationItem(String agent, String visitor, Date date, String email, String question, String sessionID) {
        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        setLayout(new GridBagLayout());

        final JLabel aLabel = new JLabel();
        aLabel.setText(FpRes.getString("agent") +":");
        aLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(aLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(agentLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(dateLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        final JLabel nameLabel = new JLabel();
        nameLabel.setText(FpRes.getString("visitor") +":");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(nameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));
        add(visitorLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));


        final JLabel emailTitleLabel = new JLabel();
        emailTitleLabel.setText(FpRes.getString("email") + ":");
        emailTitleLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        add(emailTitleLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));
        add(emailLabel, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));

        final JLabel questionTitle = new JLabel();
        questionTitle.setText(FpRes.getString("question") + ":");
        questionTitle.setFont(new Font("Dialog", Font.BOLD, 11));

        add(questionTitle, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));
        add(questionLabel, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));


        final SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        String theDate = simpleFormat.format(date);

        agentLabel.setText(agent);
        visitorLabel.setText(visitor);
        dateLabel.setText(theDate);
        emailLabel.setText(email);
        questionLabel.setText(question);

        setSessionID(sessionID);
    }

    public String getToolTipText() {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><body>");
        buf.append("<table width=200><tr><td>"+FpRes.getString("question") +": " + questionLabel.getText() + "</td></tr></table></body></html>");
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
