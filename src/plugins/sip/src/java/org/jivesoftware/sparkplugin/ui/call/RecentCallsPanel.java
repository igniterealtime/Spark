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
package org.jivesoftware.sparkplugin.ui.call;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.SoftPhoneManager;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.sparkplugin.callhistory.HistoryCall;
import org.jivesoftware.sparkplugin.calllog.LogManager;

/**
 * UI to represent all calls relating to this user.
 *
 * @author Derek DeMoro
 */
public class RecentCallsPanel extends JPanel {


	private static final long	serialVersionUID	= 7759582394286918370L;
	private DefaultListModel model = new DefaultListModel();
    private JList list = new JList(model);

    public RecentCallsPanel(InterlocutorUI ic) {
        setLayout(new GridBagLayout());

        setBackground(Color.white);

        add(new JScrollPane(list), new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        list.setCellRenderer(new JPanelRenderer());


        final LogManager logManager = SoftPhoneManager.getInstance().getLogManager();
        for (HistoryCall call : logManager.getCallHistory()) {
            String number = SoftPhoneManager.getNumbersFromPhone(call.getNumber());
            String newCallNumber = SoftPhoneManager.getNumbersFromPhone(ic.getCall().getNumber());
            if (number.equals(newCallNumber)) {
                final CallEntry callEntry = new CallEntry(call.getNumber(), new Date(call.getTime()));
                model.addElement(callEntry);
            }
        }

    }


    /**
     * Represents a single entry into the phone history list.
     */
    private class CallEntry extends JPanel {
		private static final long	serialVersionUID	= 5650351009200951861L;

		public CallEntry(String title, Date time) {
            setLayout(new GridBagLayout());

            final JLabel titleLabel = new JLabel(title);

            final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");

            final JLabel descriptionLabel = new JLabel(formatter.format(time));
            descriptionLabel.setForeground(Color.gray);
            descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));

            final JLabel imageLabel = new JLabel(SparkRes.getImageIcon(SparkRes.SMALL_PIN_BLUE));
            imageLabel.setHorizontalTextPosition(JLabel.LEFT);
            imageLabel.setHorizontalAlignment(JLabel.LEFT);
            add(imageLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

            add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
            add(descriptionLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));

        }
    }
}
