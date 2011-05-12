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

public class QueueItem extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel queueNameLabel = new JLabel();
    private JLabel numberInQueueLabel = new JLabel();
    private JLabel averageWaitTimeLabel = new JLabel();
    private JLabel lastCustomerLabel = new JLabel();

    public QueueItem(String queueName, int numberInQueue, int waitTime, String lastCustomer) {
        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        setLayout(new GridBagLayout());

        final JLabel aLabel = new JLabel();
        aLabel.setText(FpRes.getString("name") + ":");
        aLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(aLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));
        add(queueNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));


        final JLabel nameLabel = new JLabel();
        nameLabel.setText(FpRes.getString("number.in.queue") + ":");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(nameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));
        add(numberInQueueLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));


        final JLabel averageLabel = new JLabel();
        averageLabel.setText(FpRes.getString("average.wait.time") +":");
        averageLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        add(averageLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));
        add(averageWaitTimeLabel, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));

        final JLabel lastLabel = new JLabel(FpRes.getString("last.queue.activity") + ":");
        add(lastLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 2, 5), 0, 0));
        add(lastCustomerLabel, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));
        lastLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        queueNameLabel.setText(queueName);
        numberInQueueLabel.setText(Integer.toString(numberInQueue));

        lastCustomerLabel.setText(lastCustomer);
        setAverageWaitTime(waitTime);
    }


    public void setNumberOfUsersInQueue(int numberOfUsers) {
        numberInQueueLabel.setText(Integer.toString(numberOfUsers));
    }

    public void setOldestEntryDate(Date date) {
        final SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        String theDate = simpleFormat.format(date);

        lastCustomerLabel.setText(theDate);
    }

    public void setAverageWaitTime(int waitTime) {
        int minutes = waitTime / 60;
        int seconds = waitTime % 60;
        String displayString;

        if (minutes != 0) {
            displayString = minutes + " min, " + seconds + " sec.";
        }
        else {
            displayString = seconds + " "+FpRes.getString("seconds");
        }
        averageWaitTimeLabel.setText(displayString);
    }

    public void setLastCustomer(String customer){
        lastCustomerLabel.setText(customer);
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
