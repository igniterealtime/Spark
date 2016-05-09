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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.assistants.RoomInformation;
import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.workgroup.agent.Offer;
import org.jivesoftware.spark.component.LinkLabel;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;

public class ChatQueue extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel nameLabel = new JLabel();
    private RolloverButton acceptButton;
    private RolloverButton declineButton;
    private LinkLabel viewLabel;

    private Offer offer;
    private JProgressBar progressBar;

    public ChatQueue() {
        setLayout(new GridBagLayout());
        setBackground(Color.white);

        acceptButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.CIRCLE_CHECK_IMAGE));
        declineButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.SMALL_DELETE));

        ResourceUtils.resButton(acceptButton, FpRes.getString("button.accept"));
        ResourceUtils.resButton(declineButton, FpRes.getString("button.reject"));

        progressBar = new JProgressBar();
        progressBar.setFont(new Font("Dialog", Font.BOLD, 11));

        final JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);

        topPanel.setLayout(new GridBagLayout());

        topPanel.add(nameLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        add(topPanel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void showInformation() {
        if (offer == null) {
            return;
        }

        Map metadata = offer.getMetaData();
        RoomInformation roomInformation = new RoomInformation();
        roomInformation.showAllInformation(metadata);
        roomInformation.showRoomInformation();
    }

    public void offerRecieved(Offer offer) {
        this.offer = offer;

        // Retrieve workgroup form
        Form form;
        try {
            form = FastpathPlugin.getWorkgroup().getWorkgroupForm();
        }
        catch (XMPPException e) {
            Log.error("Unable to retrieve Workgroup form.", e);
            return;
        }

        final RequestUtils utils = new RequestUtils(offer.getMetaData());
        nameLabel.setText(FpRes.getString("message.incoming.chat.request", utils.getUsername()));
        nameLabel.setIcon(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));

        Color linkColor = new Color(69, 92, 137);

        int count = 1;
        Iterator fields = form.getFields();
        while (fields.hasNext()) {
            FormField field = (FormField)fields.next();
            String variable = field.getVariable();
            String label = field.getLabel();
            if (label != null) {
                final JLabel nameLabel = new JLabel(label);
                nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
                String value = utils.getValue(variable);
                if (value == null) {
                    value = "";
                }
                final WrappedLabel valueLabel = new WrappedLabel();
                valueLabel.setBackground(Color.white);
                valueLabel.setText(value);
                add(nameLabel, new GridBagConstraints(0, count, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                add(valueLabel, new GridBagConstraints(1, count, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                count++;
            }
        }

        add(progressBar, new GridBagConstraints(0, count, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));

        count++;
        viewLabel = new LinkLabel(FpRes.getString("message.view.more.information"), null, linkColor, Color.red);
        add(viewLabel, new GridBagConstraints(0, count, 1, 1, 0.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);

        add(buttonPanel, new GridBagConstraints(1, count, 2, 1, 1.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        viewLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showInformation();
            }
        });


        final Date endTime = offer.getExpiresDate();
        Date now = new Date();

        long mill = endTime.getTime() - now.getTime();
        int seconds = (int)(mill / 1000);
        progressBar.setMaximum(seconds);
        progressBar.setValue(seconds);


        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                while (true) {
                    Date now = new Date();
                    if (now.getTime() >= endTime.getTime()) {
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        Log.error(e);
                    }

                    progressBar.setValue(progressBar.getValue() - 1);
                    progressBar.setStringPainted(true);


                    int seconds = (int)(endTime.getTime() - now.getTime()) / 1000;
                    if (seconds <= 60) {
                        String timeString = seconds + " " + FpRes.getString("seconds");
                        progressBar.setString(timeString);
                    }
                    else {
                        long difference = endTime.getTime() - now.getTime();
                        String timeString = ModelUtil.getTimeFromLong(difference);

                        progressBar.setString(timeString);
                    }
                }
                return progressBar;
            }
        };

        worker.start();
    }

    public RolloverButton getAcceptButton() {
        return acceptButton;
    }

    public RolloverButton getDeclineButton() {
        return declineButton;
    }

    public Offer getOffer() {
        return offer;
    }
}
