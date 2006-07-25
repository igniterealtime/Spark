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

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportFactory;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

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


    private RegistrationDialog registrationDialog;


    public Transports(final XMPPConnection con) {
        setLayout(new GridBagLayout());

        list = new JList(model);

        // Use JPanel Renderer
        list.setCellRenderer(new JPanelRenderer());


        registrationDialog = new RegistrationDialog();

        TitlePanel titlePanel = new TitlePanel("Available Transports", "Register with these available transports.", null, true);
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
                        int confirm = JOptionPane.showConfirmDialog(item, "Would you like to disable this active transport?", "Disable Transport", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                unregister(con, item.getTransport().getServiceName());
                            }
                            catch (XMPPException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    else {
                        registrationDialog.registerWithService(con, item.getTransport().getServiceName());
                    }
                }
            }
        });


        for (Transport transport : TransportFactory.getTransports()) {
            final TransportItem transportItem = new TransportItem(transport, TransportFactory.isRegistered(con, transport), transport.getServiceName());
            model.addElement(transportItem);
        }
    }


    private void unregister(XMPPConnection con, String gatewayDomain) throws XMPPException {
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);
        Map map = new HashMap();
        map.put("remove", "");
        registration.setAttributes(map);


        PacketCollector collector = con.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        con.sendPacket(registration);

        IQ response = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (response == null) {
            throw new XMPPException("Server timed out");
        }
        if (response.getType() == IQ.Type.ERROR) {
            throw new XMPPException("Error registering user", response.getError());
        }

    }

    public void showTransports() {
        final JFrame frame = new JFrame("Transports");

        Transports panel = new Transports(SparkManager.getConnection());


        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(400, 200);
        GraphicUtils.centerWindowOnScreen(frame);
        frame.setVisible(true);

    }
}
