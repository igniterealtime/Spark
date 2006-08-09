/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.plugin.gateways.TransportRegistrationPanel;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TransportManager {

    private static Map<String, Transport> transports = new HashMap<String, Transport>();


    private TransportManager() {

    }

    public static Transport getTransport(String serviceName) {
        // Return transport.
        if (transports.containsKey(serviceName)) {
            return transports.get(serviceName);
        }

        return null;
    }

    public static void addTransport(String serviceName, Transport transport) {
        transports.put(serviceName, transport);
    }

    public static Collection<Transport> getTransports() {
        return transports.values();
    }

    public static boolean isRegistered(XMPPConnection con, Transport transport) {
        Presence presence = con.getRoster().getPresence(transport.getServiceName());
        boolean registered = presence != null && presence.getMode() != null;
        if (presence == null || presence.getType() == Presence.Type.unavailable) {
            registered = false;
        }
        return registered;
    }

    public static void registerWithService(final XMPPConnection con, final String serviceName) {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        final TransportRegistrationPanel regPanel = new TransportRegistrationPanel(serviceName);
        mainPanel.add(regPanel, BorderLayout.CENTER);

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final RolloverButton registerButton = new RolloverButton("Register", null);
        final RolloverButton cancelButton = new RolloverButton("Cancel", null);

        ResourceUtils.resButton(registerButton, "&Register");
        ResourceUtils.resButton(cancelButton, "&Cancel");

        buttonPanel.add(registerButton);
        registerButton.requestFocus();
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create Dialog
        Transport transport = TransportManager.getTransport(serviceName);
        final JDialog dialog = new JDialog(SparkManager.getMainWindow(), transport.getTitle(), true);
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setSize(400, 200);

        GraphicUtils.centerWindowOnComponent(dialog, SparkManager.getMainWindow());


        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = regPanel.getScreenName();
                String password = regPanel.getPassword();
                if (!ModelUtil.hasLength(username) || !ModelUtil.hasLength(password)) {
                    JOptionPane.showMessageDialog(mainPanel, "Username and/or Password need to be supplied.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    registerUser(con, serviceName, username, password);

                    // Send updated presence.

                }
                catch (XMPPException e1) {
                    JOptionPane.showMessageDialog(mainPanel, "Unable to register with Transport.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                }

                dialog.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }


    public static void registerUser(XMPPConnection con, String gatewayDomain, String username, String password) throws XMPPException {
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

    public static void unregister(XMPPConnection con, String gatewayDomain) throws XMPPException {
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

}
