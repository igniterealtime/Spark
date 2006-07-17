/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * UI for editing Local Preferences.
 */
public class LocalPreferencePanel extends JPanel {
    private JLabel portLabel = new JLabel();
    private JTextField portField = new JTextField();

    private JLabel timeOutLabel = new JLabel();
    private JTextField timeOutField = new JTextField();

    private JCheckBox autoLoginBox = new JCheckBox();
    private JCheckBox savePasswordBox = new JCheckBox();

    private JCheckBox idleBox = new JCheckBox();
    private JLabel idleLabel = new JLabel();
    private JTextField idleField = new JTextField();

    private JCheckBox launchOnStartupBox = new JCheckBox();
    private JCheckBox startMinimizedBox = new JCheckBox();

    /**
     * Construct Local Preference UI.
     */
    public LocalPreferencePanel() {
        setLayout(new VerticalFlowLayout());

        // Load local localPref
        LocalPreferences localPref = SettingsManager.getLocalPreferences();
        portField.setText(Integer.toString(localPref.getXmppPort()));
        timeOutField.setText(Integer.toString(localPref.getTimeOut()));
        autoLoginBox.setSelected(localPref.isAutoLogin());
        savePasswordBox.setSelected(localPref.isSavePassword());
        startMinimizedBox.setSelected(localPref.isStartedHidden());

        savePasswordBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoLoginBox.setEnabled(savePasswordBox.isSelected());
                if (!savePasswordBox.isSelected()) {
                    autoLoginBox.setSelected(false);
                }
            }
        });

        autoLoginBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoLoginBox.isSelected()) {
                    savePasswordBox.setSelected(true);
                }
            }
        });

        idleBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                idleField.setEnabled(idleBox.isSelected());
            }
        });

        idleBox.setSelected(localPref.isIdleOn());
        idleField.setText(Integer.toString(localPref.getIdleTime()));

        final JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Login Information"));

        ResourceUtils.resLabel(portLabel, portField, "&XMPP Port:");
        ResourceUtils.resLabel(timeOutLabel, timeOutField, "&Response Time Out(sec):");
        ResourceUtils.resButton(autoLoginBox, "&Auto Login");
        ResourceUtils.resButton(savePasswordBox, "&Save Password");
        ResourceUtils.resLabel(idleLabel, idleField, "&Time till Idle(minutes):");
        ResourceUtils.resButton(idleBox, "&Idle enabled");
        ResourceUtils.resButton(launchOnStartupBox, "&Launch on Startup");
        ResourceUtils.resButton(startMinimizedBox, "&Start in System Tray");

        inputPanel.add(portLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        inputPanel.add(portField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        inputPanel.add(timeOutLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        inputPanel.add(timeOutField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        inputPanel.add(idleLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        inputPanel.add(idleField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));

        inputPanel.add(idleBox, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        inputPanel.add(savePasswordBox, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        inputPanel.add(autoLoginBox, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));

        /*
        if (Spark.isWindows()) {
            inputPanel.add(launchOnStartupBox, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
            checkRegistry();
            launchOnStartupBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    launchOnStartup(launchOnStartupBox.isSelected());
                }
            });
        }
        */

        inputPanel.add(startMinimizedBox, new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 0));
        inputPanel.add(new JLabel(), new GridBagConstraints(0, 8, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 50, 0));


        add(inputPanel);
    }

    /**
     * Sets the XMPP port to comminucate on.
     *
     * @param port the XMPP port to communicate on.
     */
    public void setPort(String port) {
        portField.setText(port);
    }

    /**
     * Return the XMPP Port to communicate on.
     *
     * @return the XMPP Port to communicate on.
     */
    public String getPort() {
        return portField.getText();
    }

    /**
     * Sets the XMPP Timeout(in seconds).
     *
     * @param timeOut the XMPP Timeout(in seconds).
     */
    public void setTimeOut(String timeOut) {
        timeOutField.setText(timeOut);
    }

    /**
     * Return the XMPP Timeout variable.
     *
     * @return the XMPP Timeout variable.
     */
    public String getTimeout() {
        return timeOutField.getText();
    }

    /**
     * Sets Auto Login on and off.
     *
     * @param auto true if Auto Login is on.
     */
    public void setAutoLogin(boolean auto) {
        autoLoginBox.setSelected(auto);
    }

    /**
     * Return true if Auto Login is on.
     *
     * @return true if Auto Login is on.
     */
    public boolean getAutoLogin() {
        return autoLoginBox.isSelected();
    }

    /**
     * Set true if the password should be encoded and saved.
     *
     * @param save true if the password should be encoded and saved.
     */
    public void setSavePassword(boolean save) {
        savePasswordBox.setSelected(save);
    }

    /**
     * Return true if the password should be saved.
     *
     * @return true if the password should be saved.
     */
    public boolean isSavePassword() {
        return savePasswordBox.isSelected();
    }

    /**
     * Returns true if IDLE is on.
     *
     * @return true if IDLE is on.
     */
    public boolean isIdleOn() {
        return idleBox.isSelected();
    }

    /**
     * Sets the IDLE on or off.
     *
     * @param on true if IDLE should be on.
     */
    public void setIdleOn(boolean on) {
        idleBox.setSelected(on);
    }

    /**
     * Sets the Idle Time in minutes.
     *
     * @param time the Idle time in minutes.
     */
    public void setIdleTime(int time) {
        String idleTime = Integer.toString(time);
        idleField.setText(idleTime);
    }

    /**
     * Return the time to IDLE.
     *
     * @return the time to IDLE.
     */
    public String getIdleTime() {
        return idleField.getText();
    }

    public void startInSystemTray(boolean startInTray) {
        startMinimizedBox.setSelected(startInTray);
    }

    public boolean startInSystemTray() {
        return startMinimizedBox.isSelected();
    }

    /*
    private void checkRegistry() {
        try {
            RegistryKeyValues values = RegistryKey.CURRENT_USER.openSubKey("Software").openSubKey("Microsoft").openSubKey("Windows").openSubKey("CurrentVersion").openSubKey("Run").values();
            for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry)iterator.next();
                String key = (String)entry.getKey();
                if (key.equals("Spark")) {
                    launchOnStartupBox.setSelected(true);
                }
            }
        }
        catch (Exception e) {
            Log.error("Unable to retrieve registry settings.", e);
        }
    }

    private void launchOnStartup(boolean launch) {
        try {
            if (launch) {
                // Add to Registery
                RegistryKeyValues values = RegistryKey.CURRENT_USER.openSubKey("Software").openSubKey("Microsoft").openSubKey("Windows").openSubKey("CurrentVersion").openSubKey("Run", true).values();
                File starter = new File(Spark.getBinDirectory().getParentFile(), "Spark.exe");
                values.put("Spark", starter.getAbsolutePath());
            }
            else {
                // Add to Registery
                RegistryKey key = RegistryKey.CURRENT_USER.openSubKey("Software").openSubKey("Microsoft").openSubKey("Windows").openSubKey("CurrentVersion").openSubKey("Run", true);
                key.values().remove("Spark");
            }
        }
        catch (Exception e) {
            Log.error("Unable to retrieve registry settings.", e);
        }
    }
    */
}
