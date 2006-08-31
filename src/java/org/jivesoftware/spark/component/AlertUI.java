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

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.GraphicUtils;

import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Simple notification UI within Spark.
 *
 * @author Derek DeMoro
 */
public class AlertUI extends JPanel implements ActionListener {

    /**
     * Buidls an AlertUI
     *
     * @param message
     * @param description
     */
    public AlertUI(final String message, final String description) {
        final RolloverButton closeButton;

        // Set Layout
        setLayout(new GridBagLayout());

        // Build UI
        final JLabel titleLabel = new JLabel();
        final JLabel descriptionLabel = new JLabel();

        final JLabel dateLabel = new JLabel();
        final JLabel dateLabelValue = new JLabel();

        add(titleLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(descriptionLabel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));

        add(dateLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));
        add(dateLabelValue, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));

        titleLabel.setFont(new Font("dialog", Font.BOLD, 11));
        titleLabel.setText(message);

        descriptionLabel.setForeground(Color.gray);

        // Set Date Label
        dateLabel.setText(Res.getString("label.received") + ":");
        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        final String date = formatter.format(new Date());
        dateLabelValue.setText(date);

        // Add accept and reject buttons
        closeButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.CLOSE_IMAGE));

        add(closeButton, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));


        closeButton.addActionListener(this);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        setBackground(Color.white);

        titleLabel.setToolTipText(GraphicUtils.createToolTip(message, 200));

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Show message in dialog
                    MessageDialog.showAlert(description, Res.getString("title.alert"), null);
                }
            }
        });

        descriptionLabel.setText(description);

        SparkManager.getWorkspace().addAlert(this);
        SparkManager.getAlertManager().flashWindowStopOnFocus(SparkManager.getMainWindow());
        setBackground(new Color(195, 217, 255));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        SparkManager.getWorkspace().removeAlert(this);
    }


}
