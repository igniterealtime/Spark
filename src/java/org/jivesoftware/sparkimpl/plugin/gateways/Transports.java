/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 */
public class Transports extends JPanel {

    private JList list;
    private DefaultListModel model = new DefaultListModel();


    public Transports(final XMPPConnection con) {
        setLayout(new GridBagLayout());

        list = new JList(model);

        // Use JPanel Renderer
        list.setCellRenderer(new JPanelRenderer());


        TitlePanel titlePanel = new TitlePanel(Res.getString("title.available.transports"), Res.getString("message.register.transports"), null, true);
        add(titlePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final JScrollPane pane = new JScrollPane(list);
        add(pane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TransportItem item = (TransportItem)list.getSelectedValue();
                    Presence presence = con.getRoster().getPresence(item.getTransport().getServiceName());
                    boolean registered = presence != null && presence.getMode() != null;
                    if (registered) {
                        int confirm = JOptionPane.showConfirmDialog(item, Res.getString("message.disable.transport"), Res.getString("title.disable.transport"), JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                TransportUtils.unregister(con, item.getTransport().getServiceName());
                            }
                            catch (XMPPException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    else {
                        // registrationDialog.registerWithService(con, item.getTransport().getServiceName());
                    }
                }
            }
        });


        for (Transport transport : TransportUtils.getTransports()) {
            final TransportItem transportItem = new TransportItem(transport, TransportUtils.isRegistered(con, transport), transport.getServiceName());
            model.addElement(transportItem);
        }
    }


    public void showTransports() {
        final JFrame frame = new JFrame(Res.getString("transports"));

        Transports panel = new Transports(SparkManager.getConnection());


        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(400, 200);
        GraphicUtils.centerWindowOnScreen(frame);
        frame.setVisible(true);

    }
}
