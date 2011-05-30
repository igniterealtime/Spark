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

import org.jivesoftware.sparkplugin.callhistory.HistoryCall;
import org.jivesoftware.sparkplugin.callhistory.TelephoneUtils;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import net.java.sipmack.softphone.SoftPhoneManager;
import org.jivesoftware.spark.component.TimeTrackingLabel;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ModelUtil;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
public class PreviousConversationPanel extends JPanel {


	private static final long	serialVersionUID	= -3462528114523564888L;

	private final Color greenColor = new Color(91, 175, 41);

    private final JLabel currentCallLabel = new JLabel();
    private final JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));

    private final JLabel today = new JLabel();
    private final JLabel statusLabel = new JLabel();

    final JLabel previousLabel = new JLabel(PhoneRes.getIString("phone.previousconversations")+":");
    private final JLabel oldConversation = new JLabel();

    private final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy, h:mm a");


    private TimeTrackingLabel durationLabel;

    private Date startTime;


    public PreviousConversationPanel() {
        setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 5, 0, true, false));

        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230)));

        // Set Default Color for Current Call Label
        currentCallLabel.setText(PhoneRes.getIString("phone.currentcall")+":");
        currentCallLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        currentCallLabel.setForeground(greenColor);

        // Set default color for previous label.
        previousLabel.setForeground(new Color(64, 103, 162));
        previousLabel.setFont(new Font("Dialog", Font.BOLD, 13));

        statusLabel.setFont(new Font("Dialog", Font.BOLD, 12));

        // Add Duration timer
        durationLabel = new TimeTrackingLabel(new Date(), this);
        durationLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        durationLabel.stopTimer();

        // Build Time Panel

        timePanel.setOpaque(false);

        today.setForeground(Color.black);
        today.setFont(new Font("Dialog", Font.PLAIN, 12));

        today.setText(formatter.format(new Date()) + " - "+PhoneRes.getIString("phone.time")+": ");
        timePanel.add(today);
        timePanel.add(durationLabel);

        oldConversation.setForeground(new Color(211, 0, 0));
        oldConversation.setFont(new Font("Dialog", Font.BOLD, 12));

    }

    /**
     * Builds the previous history list.
     *
     * @param phoneNumber the phone number to use for history checking.
     */
    public void addPreviousConversations(String phoneNumber) {
        startTime = new Date();

        currentCallLabel.setText(PhoneRes.getIString("phone.currentcall")+":");
        currentCallLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        currentCallLabel.setForeground(greenColor);

        today.setText(formatter.format(new Date()) + " - "+PhoneRes.getIString("phone.time")+": ");

        // Add Current Call and Time Panel
        add(currentCallLabel);
        add(timePanel);
        add(statusLabel);
        statusLabel.setVisible(false);

        // Give some space.
        add(new JLabel());

        add(previousLabel);

        add(oldConversation);


        int count = 0;
        final List<HistoryCall> calls = new ArrayList<HistoryCall>(SoftPhoneManager.getInstance().getLogManager().getCallHistory());
        Collections.sort(calls, itemComparator);
        
        for (HistoryCall call : calls){
            String number = TelephoneUtils.removeInvalidChars(call.getNumber());
            if (number.equals(TelephoneUtils.removeInvalidChars(phoneNumber))) {
                count++;
                if (count > 4) {
                    break;
                }

                final Date callDate = new Date(call.getTime());
                final long duration = call.getCallLength();

                StringBuilder builder = new StringBuilder();
                builder.append(formatter.format(callDate));
                builder.append(" ");
                builder.append("(");
                builder.append(ModelUtil.getTimeFromLong(duration*1000));
                builder.append(")");

                final JLabel callLabel = new JLabel(builder.toString());
                callLabel.setForeground(Color.black);
                callLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
                add(callLabel);
            }
        }

        if (count == 0) {
            final JLabel label = new JLabel(PhoneRes.getIString("phone.noprevious"));
            label.setForeground(Color.gray);
            add(label);
        }

        durationLabel.resetTime();
        durationLabel.startTimer();

        invalidate();
        validate();
        repaint();
    }

    public void callEnded() {
        durationLabel.stopTimer();

        currentCallLabel.setForeground(Color.black);
        currentCallLabel.setText(PhoneRes.getIString("phone.callended"));

        today.setText(PhoneRes.getIString("phone.time")+": ");
    }

    public void transferring() {
        statusLabel.setText(PhoneRes.getIString("phone.transferring"));
        statusLabel.setVisible(true);
    }

    public void transfer(String user) {
        durationLabel.stopTimer();
        statusLabel.setVisible(false);

        currentCallLabel.setForeground(Color.black);
        currentCallLabel.setText(PhoneRes.getIString("phone.callended"));

        today.setText(PhoneRes.getIString("phone.time")+": ");
        currentCallLabel.setText(PhoneRes.getIString("phone.transferto") + " " + user);

        Date now = new Date();
        final SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm");


        String end = timeFormatter.format(now);

        oldConversation.setText(formatter.format(startTime) + " - " + end + " (" + durationLabel.getText() + ")");
    }

    final Comparator<HistoryCall> itemComparator = new Comparator<HistoryCall>() {
        public int compare(HistoryCall contactItemOne, HistoryCall contactItemTwo) {
            final HistoryCall time1 = contactItemOne;
            final HistoryCall time2 = contactItemTwo;
            if (time1.getTime() < time2.getTime()) {
                return 1;
            }
            else if (time1.getTime() > time2.getTime()) {
                return -1;
            }
            return 0;

        }
    };

}
