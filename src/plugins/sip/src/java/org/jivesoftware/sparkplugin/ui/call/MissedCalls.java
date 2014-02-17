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
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.java.sipmack.common.Log;
import net.java.sipmack.softphone.SoftPhoneManager;

import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

/**
 * Responsible for the handling and displaying of missed calls. The toaster should remain visible until the user explicitly closes it.
 *
 * @author Derek DeMoro
 */
public class MissedCalls implements ActionListener {

    private SparkToaster toaster;
    private final DefaultListModel model;
    private final JList list;
    private JPanel gui;
    private RolloverButton callBackButton;
    private RolloverButton deleteButton;
    private String callID;

    public MissedCalls() {
        model = new DefaultListModel();
        list = new JList(model);
        gui = getMissedCallPanel();


        list.setCellRenderer(new MissedCallRenderer());
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    placeCall((MissedCall)list.getSelectedValue());
                }
            }
        });

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                int selectedIndex = list.getSelectedIndex();
                boolean enabled = selectedIndex != -1;
                callBackButton.setEnabled(enabled);
                deleteButton.setEnabled(enabled);
            }
        });
    }

    public JPanel getMissedCallPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.add(new JScrollPane(list), new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        callBackButton = new RolloverButton(PhoneRes.getIString("phone.callback"), PhoneRes.getImageIcon("PHONE_CALL_24x24_IMAGE"));
        deleteButton = new RolloverButton(PhoneRes.getIString("phone.delete"), PhoneRes.getImageIcon("DELETE_24x24_IMAGE"));
        callBackButton.setHorizontalAlignment(JLabel.CENTER);
        deleteButton.setHorizontalAlignment(JLabel.CENTER);
        final Font buttonFont = new Font("Dialog", Font.BOLD, 13);
        callBackButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);

        // Add Action Listener
        callBackButton.addActionListener(this);
        deleteButton.addActionListener(this);

        // Set as disabled by default, and only have them enabled if their is a valid
        // selection.
        callBackButton.setEnabled(false);
        deleteButton.setEnabled(false);

        final JPanel flowPanel = new JPanel(new FlowLayout());
        flowPanel.setOpaque(false);
        flowPanel.add(callBackButton);
        flowPanel.add(deleteButton);

        panel.setOpaque(false);
        panel.add(flowPanel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        return panel;
    }

    /**
     * Called whenever the user misses a call. The information will be displayed in the Spark Toaster until the user
     * explicitly closes the window.
     *
     * @param callerID the callerID
     * @param number   the number the user dialed from.
     */
    public void addMissedCall(String callerID, final String number) {
   	  callID = callerID;
        VCard vcard = SparkManager.getVCardManager().searchPhoneNumber(number);
        if (vcard != null) {
            String firstName = vcard.getFirstName();
            String lastName = vcard.getLastName();
            if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            	callID = firstName + " " + lastName;
            }
            else if (ModelUtil.hasLength(firstName)) {
            	callID = firstName;
            }
        }

        try {
      	  EventQueue.invokeAndWait(new Runnable(){
      		  public void run()
      		  {
      			  final MissedCall missedCall = new MissedCall(callID, new Date(), number);
      	        model.insertElementAt(missedCall, 0);
      	        
      	        if (toaster == null || !list.isShowing()) {
      	      	  toaster = new SparkToaster();
      	      	  toaster.setToasterHeight(230);
      	      	  toaster.setToasterWidth(300);
      	      	  toaster.setDisplayTime(500000000);
      	      	  toaster.showToaster(PhoneRes.getIString("phone.missedcalls"), gui);
      	        }
      		  }
      	  });
        }
        catch(Exception e) {
      	  Log.error(e);
        }
    }


    /**
     * Represents a single entry into the phone history list.
     */
    private class MissedCall extends JPanel {

		private static final long	serialVersionUID	= -6155295091292349158L;
		private String number;

        public MissedCall(String title, Date time, String number) {
            setLayout(new GridBagLayout());

            this.number =  number;

            final JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));

            final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");    

            StringBuilder builder = new StringBuilder();
            builder.append(formatter.format(time));
            builder.append(" ");

            final JLabel descriptionLabel = new JLabel(builder.toString());
            descriptionLabel.setForeground(Color.gray);
            descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));

            // Add Title Label
            add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

            // Add Number Label
            final JLabel numberLabel = new JLabel();
            numberLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
            numberLabel.setText(getNumber());
            add(numberLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 2), 0, 0));

            // Add call description
            add(descriptionLabel, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        }

        public String getNumber() {
            return number;
        }
    }

    private void placeCall(MissedCall missedCall) {
        SoftPhoneManager.getInstance().getDefaultGuiManager().dial(missedCall.getNumber());
        SparkManager.getMainWindow().setVisible(true);
        SparkManager.getMainWindow().toFront();
    }

    public void actionPerformed(ActionEvent e) {
        final MissedCall missedCall = (MissedCall)list.getSelectedValue();

        if (e.getSource() == callBackButton) {
            placeCall(missedCall);
        }
        else {
            model.removeElement(missedCall);
        }
    }

    /**
     * Internal ListRenderer for MissedCallRenderer
     */
    private static class MissedCallRenderer extends JPanel implements ListCellRenderer {

		private static final long	serialVersionUID	= -3128542669141396537L;

		public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            MissedCall panel = (MissedCall)value;
            panel.setFocusable(false);

            if (isSelected) {
                panel.setForeground((Color)UIManager.get("List.selectionForeground"));
                panel.setBackground((Color)UIManager.get("List.selectionBackground"));
                panel.setBorder(BorderFactory.createLineBorder((Color)UIManager.get("List.selectionBorder")));
            }
            else {
                panel.setBackground(new Color(255, 224, 224));

                panel.setForeground(list.getForeground());
                panel.setBorder(BorderFactory.createLineBorder((Color)UIManager.get("List.background")));
            }

            list.setBackground((Color)UIManager.get("List.background"));


            return panel;
        }
    }

}
