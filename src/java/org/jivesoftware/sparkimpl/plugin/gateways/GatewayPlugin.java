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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.AIMTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.MSNTransport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 */
public class GatewayPlugin implements Plugin {
    public static final String GATEWAY = "gateway";


    public void initialize() {
        try {
            populateTransports(SparkManager.getConnection());
        }
        catch (Exception e) {
            return;
        }

        // Add to Menu Item
        // Register with action menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName("Actions");
        JMenuItem transportsMenu = new JMenuItem("Transports", SparkRes.getImageIcon(SparkRes.AIM_TRANSPORT_ACTIVE_IMAGE));
        ResourceUtils.resButton(transportsMenu, "&Transports");
        actionsMenu.add(transportsMenu);
        transportsMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Transports transports = new Transports(SparkManager.getConnection());
                transports.showTransports();
            }
        });
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {
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
                    if (item.getEntityID().startsWith("aim.")) {
                        AIMTransport aim = new AIMTransport(item.getEntityID());
                        TransportFactory.addTransport(item.getEntityID(), aim);
                    }
                    else if (item.getEntityID().startsWith("msn.")) {
                        MSNTransport msn = new MSNTransport(item.getEntityID());
                        TransportFactory.addTransport(item.getEntityID(), msn);
                    }
                }
            }
        }

    }
}
