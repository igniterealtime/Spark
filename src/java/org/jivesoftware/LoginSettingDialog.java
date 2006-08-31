/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware;


import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

/**
 * Allows users to configure startup options.
 *
 * @author Derek DeMoro
 */
public class LoginSettingDialog implements PropertyChangeListener {
    private JOptionPane optionPane;
    private JDialog optionsDialog;

    private TitlePanel titlePanel;

    private JCheckBox autoDiscoverBox = new JCheckBox();

    private JLabel portLabel = new JLabel();
    private JTextField portField = new JTextField();

    private JLabel xmppHostLabel = new JLabel();
    private JTextField xmppHostField = new JTextField();

    private JLabel timeOutLabel = new JLabel();
    private JTextField timeOutField = new JTextField();

    private JLabel resourceLabel = new JLabel();
    private JTextField resourceField = new JTextField();

    private JCheckBox autoLoginBox = new JCheckBox();


    private JCheckBox useSSLBox = new JCheckBox();
    private JLabel sslLabel = new JLabel();

    private LocalPreferences localPreferences;

    private ProxyPanel proxyPanel;

    /**
     * Empty Constructor.
     */
    public LoginSettingDialog() {
        localPreferences = SettingsManager.getLocalPreferences();
        proxyPanel = new ProxyPanel();
    }

    /**
     * Invokes the OptionsDialog.
     *
     * @param owner the parent owner of this dialog. This is used for correct parenting.
     * @return true if the options have been changed.
     */
    public boolean invoke(JFrame owner) {
        // Load local localPref
        JTabbedPane tabbedPane = new JTabbedPane();

        portField.setText(Integer.toString(localPreferences.getXmppPort()));
        timeOutField.setText(Integer.toString(localPreferences.getTimeOut()));
        autoLoginBox.setSelected(localPreferences.isAutoLogin());
        useSSLBox.setSelected(localPreferences.isSSL());
        xmppHostField.setText(localPreferences.getXmppHost());
        resourceField.setText(localPreferences.getResource());
        if (localPreferences.getResource() == null) {
            resourceField.setText("spark");
        }

        final JPanel inputPanel = new JPanel();
        tabbedPane.addTab(Res.getString("tab.general"), inputPanel);
        tabbedPane.addTab(Res.getString("tab.proxy"), proxyPanel);
        inputPanel.setLayout(new GridBagLayout());

        ResourceUtils.resLabel(portLabel, portField, Res.getString("label.port"));
        ResourceUtils.resLabel(timeOutLabel, timeOutField, Res.getString("label.response.timeout"));
        ResourceUtils.resButton(autoLoginBox, Res.getString("label.auto.login"));
        ResourceUtils.resLabel(sslLabel, useSSLBox, Res.getString("label.old.ssl"));
        ResourceUtils.resLabel(xmppHostLabel, xmppHostField, Res.getString("label.host"));
        ResourceUtils.resButton(autoDiscoverBox, Res.getString("checkbox.auto.discover.port"));
        ResourceUtils.resLabel(resourceLabel, resourceField, Res.getString("label.resource"));

        inputPanel.add(autoDiscoverBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        autoDiscoverBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAutoDiscovery();
            }
        });

        autoDiscoverBox.setSelected(!localPreferences.isHostAndPortConfigured());
        updateAutoDiscovery();

        final JPanel connectionPanel = new JPanel();
        connectionPanel.setLayout(new GridBagLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder(Res.getString("group.connection")));

        connectionPanel.add(xmppHostLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        connectionPanel.add(xmppHostField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 200, 0));

        connectionPanel.add(portLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        connectionPanel.add(portField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));

        inputPanel.add(connectionPanel, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        inputPanel.add(resourceLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        inputPanel.add(resourceField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));

        inputPanel.add(timeOutLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        inputPanel.add(timeOutField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));

        inputPanel.add(sslLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
        inputPanel.add(useSSLBox, new GridBagConstraints(1, 4, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Create the title panel for this dialog
        titlePanel = new TitlePanel("Advanced Connection Preferences", "", SparkRes.getImageIcon(SparkRes.BLANK_24x24), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("ok"), Res.getString("cancel"), Res.getString("use.default")};
        optionPane = new JOptionPane(tabbedPane, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(optionPane, BorderLayout.CENTER);

        optionsDialog = new JDialog(owner, Res.getString("title.preferences"), true);
        optionsDialog.setContentPane(mainPanel);
        optionsDialog.pack();


        optionsDialog.setLocationRelativeTo(owner);
        optionPane.addPropertyChangeListener(this);

        optionsDialog.setResizable(true);
        optionsDialog.setVisible(true);
        optionsDialog.toFront();
        optionsDialog.requestFocus();

        return true;
    }

    /**
     * PropertyChangeEvent is called when the user either clicks the Cancel or
     * OK button.
     *
     * @param e the property change event.
     */
    public void propertyChange(PropertyChangeEvent e) {
        String value = (String)optionPane.getValue();
        if (Res.getString("cancel").equals(value)) {
            optionsDialog.setVisible(false);
        }
        else if (Res.getString("ok").equals(value)) {
            String timeOut = timeOutField.getText();
            String port = portField.getText();
            String resource = resourceField.getText();

            boolean errors = false;

            try {
                Integer.valueOf(timeOut);
            }
            catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(optionsDialog, Res.getString("message.supply.valid.timeout"),
                        Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                timeOutField.requestFocus();
                errors = true;
            }

            try {
                Integer.valueOf(port);
            }
            catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(optionsDialog, Res.getString("message.supply.valid.port"),
                        Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                portField.requestFocus();
                errors = true;
            }

            if (!ModelUtil.hasLength(resource)) {
                JOptionPane.showMessageDialog(optionsDialog, Res.getString("message.supply.resource"),
                        Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                resourceField.requestFocus();
                errors = true;
            }

            if (!errors) {
                localPreferences.setTimeOut(Integer.parseInt(timeOut));
                localPreferences.setXmppPort(Integer.parseInt(port));
                localPreferences.setSSL(useSSLBox.isSelected());
                localPreferences.setXmppHost(xmppHostField.getText());
                optionsDialog.setVisible(false);
                localPreferences.setResource(resource);
                proxyPanel.save();
            }
            else {
                optionPane.removePropertyChangeListener(this);
                optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                optionPane.addPropertyChangeListener(this);
            }
        }
        else {
            localPreferences.setTimeOut(30);
            localPreferences.setXmppPort(5222);
            localPreferences.setSSL(false);
            portField.setText("5222");
            timeOutField.setText("30");
            useSSLBox.setSelected(false);
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
        }
    }

    private class ProxyPanel extends JPanel {
        private JCheckBox useProxyBox = new JCheckBox();
        private JComboBox protocolBox = new JComboBox();
        private JTextField hostField = new JTextField();
        private JTextField portField = new JTextField();
        private JTextField usernameField = new JTextField();
        private JPasswordField passwordField = new JPasswordField();

        public ProxyPanel() {
            JLabel protocolLabel = new JLabel();
            JLabel hostLabel = new JLabel();
            JLabel portLabel = new JLabel();
            JLabel usernameLabel = new JLabel();
            JLabel passwordLabel = new JLabel();

            protocolBox.addItem("SOCKS");
            protocolBox.addItem("HTTP");

            // Add ResourceUtils
            ResourceUtils.resButton(useProxyBox, Res.getString("checkbox.use.proxy.server"));
            ResourceUtils.resLabel(protocolLabel, protocolBox, Res.getString("label.protocol"));
            ResourceUtils.resLabel(hostLabel, hostField, Res.getString("label.host"));
            ResourceUtils.resLabel(portLabel, portField, Res.getString("label.port"));
            ResourceUtils.resLabel(usernameLabel, usernameField, Res.getString("label.username"));
            ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password"));

            setLayout(new GridBagLayout());
            add(useProxyBox, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            add(protocolLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(protocolBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            add(hostLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(hostField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            add(portLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(portField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            add(usernameLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(usernameField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            add(passwordLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(passwordField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));


            useProxyBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enableFields(useProxyBox.isSelected());
                }
            });

            // Check localSettings
            if (localPreferences.isProxyEnabled()) {
                useProxyBox.setSelected(true);
            }

            enableFields(useProxyBox.isSelected());

            if (ModelUtil.hasLength(localPreferences.getHost())) {
                hostField.setText(localPreferences.getHost());
            }

            if (ModelUtil.hasLength(localPreferences.getPort())) {
                portField.setText(localPreferences.getPort());
            }

            if (ModelUtil.hasLength(localPreferences.getProxyPassword())) {
                passwordField.setText(localPreferences.getProxyPassword());
            }

            if (ModelUtil.hasLength(localPreferences.getProxyUsername())) {
                usernameField.setText(localPreferences.getProxyUsername());
            }

            if (ModelUtil.hasLength(localPreferences.getProtocol())) {
                protocolBox.setSelectedItem(localPreferences.getProtocol());
            }

        }

        private void enableFields(boolean enable) {
            Component[] comps = getComponents();
            for (int i = 0; i < comps.length; i++) {
                if (comps[i] instanceof JTextField || comps[i] instanceof JComboBox) {
                    JComponent comp = (JComponent)comps[i];
                    comp.setEnabled(enable);
                }
            }
        }

        public boolean useProxy() {
            return useProxyBox.isSelected();
        }

        public String getProtocol() {
            return (String)protocolBox.getSelectedItem();
        }

        public String getHost() {
            return hostField.getText();
        }

        public String getPort() {
            return portField.getText();
        }

        public String getUsername() {
            return usernameField.getText();
        }

        public String getPassword() {
            return new String(passwordField.getPassword());
        }

        public void save() {
            localPreferences.setProxyEnabled(useProxyBox.isSelected());
            if (ModelUtil.hasLength(getProtocol())) {
                localPreferences.setProtocol(getProtocol());
            }

            if (ModelUtil.hasLength(getHost())) {
                localPreferences.setHost(getHost());
            }

            if (ModelUtil.hasLength(getPort())) {
                localPreferences.setPort(getPort());
            }

            if (ModelUtil.hasLength(getUsername())) {
                localPreferences.setProxyUsername(getUsername());
            }

            if (ModelUtil.hasLength(getPassword())) {
                localPreferences.setProxyPassword(getPassword());
            }

            if (!localPreferences.isProxyEnabled()) {
                Properties props = System.getProperties();
                props.remove("socksProxyHost");
                props.remove("socksProxyPort");
                props.remove("http.proxyHost");
                props.remove("http.proxyPort");
                props.remove("http.proxySet");
            }
            else {
                String host = localPreferences.getHost();
                String port = localPreferences.getPort();
                String protocol = localPreferences.getProtocol();

                boolean isValid = ModelUtil.hasLength(host) && ModelUtil.hasLength(port);

                if (isValid) {
                    if (protocol.equals("SOCKS")) {
                        System.setProperty("socksProxyHost", host);
                        System.setProperty("socksProxyPort", port);
                    }
                    else {
                        System.setProperty("http.proxySet", "true");
                        System.setProperty("http.proxyHost", host);
                        System.setProperty("http.proxyPort", port);
                    }
                }
                else {
                    localPreferences.setProxyEnabled(false);
                }
            }

            SettingsManager.saveSettings();
        }
    }


    private void updateAutoDiscovery() {
        boolean isSelected = autoDiscoverBox.isSelected();
        xmppHostField.setEnabled(!isSelected);
        portField.setEnabled(!isSelected);
        localPreferences.setHostAndPortConfigured(!isSelected);
    }
}

