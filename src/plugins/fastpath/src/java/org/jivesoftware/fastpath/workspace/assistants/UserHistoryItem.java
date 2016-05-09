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
package org.jivesoftware.fastpath.workspace.assistants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.smackx.workgroup.packet.Transcripts;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.util.ModelUtil;

public class UserHistoryItem extends JPanel {
	private static final long serialVersionUID = -2251709231519173523L;
	private WrappedLabel agentsLabel = new WrappedLabel();
    private JLabel startTimeLabel = new JLabel();
    private JLabel durationLabel = new JLabel();

    private String sessionID;

    public UserHistoryItem(Collection<Transcripts.AgentDetail> agentDetails, Date joinTime, Date endTime) {
        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        setLayout(new GridBagLayout());


        final JLabel conLabel = new JLabel();
        conLabel.setText(FpRes.getString("duration") + ":");
        conLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(conLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        add(durationLabel, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
        long duration = endTime.getTime() - joinTime.getTime();
        durationLabel.setText(ModelUtil.getTimeFromLong(duration));

        final JLabel nameLabel = new JLabel();
        nameLabel.setText(FpRes.getString("agents") + ":");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(agentsLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        agentsLabel.setBackground(Color.white);
        agentsLabel.setOpaque(false);

        final SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        JLabel timeLabel = new JLabel(FpRes.getString("date") + ":");
        timeLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        String theDate = simpleFormat.format(joinTime);
        add(timeLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(startTimeLabel, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        startTimeLabel.setText(theDate);

        StringBuffer buf = new StringBuffer();
        Iterator<Transcripts.AgentDetail> agents = agentDetails.iterator();
        while (agents.hasNext()) {
            Transcripts.AgentDetail agentDetail = agents.next();
            String agentJID = agentDetail.getAgentJID();
            agentJID = UserManager.unescapeJID(agentJID);
            buf.append(agentJID);
            if (agents.hasNext()) {
                buf.append("\n");
            }
        }

        agentsLabel.setText(buf.toString());


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

