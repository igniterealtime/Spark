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

import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.internal.LiveTitlePane;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.LinkLabel;
import org.jivesoftware.spark.component.WrappedLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RoomInformation extends JPanel {

	private static final long serialVersionUID = 7298969616727251504L;


	public RoomInformation() {

    }

    public void showAllInformation(Map map) {
        if (map == null) {
            map = new HashMap();
        }

        LiveTitlePane titlePanel = new LiveTitlePane(FpRes.getString("title.request.information"), FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_24x24));

        final RequestUtils utils = new RequestUtils(map);


        setLayout(new GridBagLayout());
        setBackground(Color.white);

        //  add(titlePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        Iterator<String> iter = map.keySet().iterator();
        int row = 1;
        while (iter.hasNext()) {
            String key = iter.next();
            String value = utils.getValue(key);

            JLabel nameLabel = new JLabel(key);
            nameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            WrappedLabel valueLabel = new WrappedLabel();
            valueLabel.setBackground(Color.white);

            valueLabel.setText(value);

            add(nameLabel, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(valueLabel, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            row++;
        }

        add(new JLabel(""), new GridBagConstraints(1, row, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

    }

    public void showFormInformation(Form form, final RequestUtils utils) {
        setLayout(new GridBagLayout());
        setBackground(Color.white);

        int count = 1;
        Iterator<FormField> fields = form.getFields();
        while (fields.hasNext()) {
            FormField field = fields.next();
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
                add(nameLabel, new GridBagConstraints(0, count, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
                add(valueLabel, new GridBagConstraints(1, count, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
                count++;
            }
        }

        final Color linkColor = new Color(69, 92, 137);

        LinkLabel viewLabel = new LinkLabel(FpRes.getString("message.view.more.information"), null, linkColor, Color.red);
        viewLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                RoomInformation roomInformation = new RoomInformation();
                roomInformation.showAllInformation(utils.getMap());
                roomInformation.showRoomInformation();
            }
        });

        add(viewLabel, new GridBagConstraints(0, count, 3, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));

    }


    public void showRoomInformation() {
        final JFrame frame = new JFrame(FpRes.getString("title.information"));
        frame.setIconImage(SparkManager.getMainWindow().getIconImage());
        frame.getContentPane().setLayout(new BorderLayout());


        frame.getContentPane().add(new JScrollPane(this), BorderLayout.CENTER);
        frame.pack();
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(SparkManager.getChatManager().getChatContainer());
        frame.setVisible(true);
    }


    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }

}
