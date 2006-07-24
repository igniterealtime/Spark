/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.transports;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;

import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 */
public class TransportViewerPanel extends JPanel {

    private JList list;
    private DefaultListModel model = new DefaultListModel();

    private static final String GATEWAY = "gateway";


    public TransportViewerPanel() {
        setLayout(new GridBagLayout());

        list = new JList(model);

        // Use JPanel Renderer
        list.setCellRenderer(new JPanelRenderer());
    }

    public void populateTransports(XMPPConnection con) throws Exception {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(con);

        DiscoverItems discoItems = manager.discoverItems(con.getServiceName());

        DiscoverItems.Item item;
        DiscoverInfo info;
        DiscoverInfo.Identity identity;

        Iterator it = discoItems.getItems();
        while (it.hasNext()) {
            item = (Item)it.next();
            info = manager.discoverInfo(item.getEntityID());
            Iterator itx = info.getIdentities();
            while (itx.hasNext()) {
                identity = (Identity)itx.next();

                if (identity.getCategory().equalsIgnoreCase(GATEWAY)) {
                    Presence presence = con.getRoster().getPresence(item.getEntityID());
                    boolean registered = presence != null && presence.getMode() != null;
                }
            }
        }

    }

    private void registerUser(XMPPConnection con, String gatewayDomain, String username, String password) throws XMPPException {
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("username", username);
        attributes.put("password", password);
        registration.setAttributes(attributes);

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

    private void getForm(XMPPConnection con, String gateway) throws Exception {
        Registration registration = new Registration();
        registration.setType(IQ.Type.GET);
        registration.setTo(gateway);

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

        System.out.println(response);
    }


    public static void main(String args[]) throws Exception {
        XMPPConnection con = new XMPPConnection("derek", 5222);
        con.login("derek", "test");

        final JFrame frame = new JFrame("Test");

        TransportViewerPanel panel = new TransportViewerPanel();
        panel.populateTransports(con);
        panel.getForm(con, "aim.derek");

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
