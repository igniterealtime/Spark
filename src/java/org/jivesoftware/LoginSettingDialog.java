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
package org.jivesoftware;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
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
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.Principal;
import java.util.Properties;
import java.io.File;

/**
 * Allows users to configure startup options.
 * 
 * @author Derek DeMoro
 * @author Jay Kline
 */
public class LoginSettingDialog implements PropertyChangeListener {

    private LocalPreferences localPreferences;

    private JDialog optionsDialog;
    private JOptionPane optionPane;

    private GeneralPanel generalPanel;
    private ProxyPanel proxyPanel;
    private PkiPanel pkiPanel;
    private SsoPanel ssoPanel;

    /**
     * Empty Constructor.
     */
    public LoginSettingDialog() {
	localPreferences = SettingsManager.getLocalPreferences();
	generalPanel = new GeneralPanel();
	proxyPanel = new ProxyPanel();
	ssoPanel = new SsoPanel();
	pkiPanel = new PkiPanel();
    }

    /**
     * Invokes the OptionsDialog.
     * 
     * @param owner
     *            the parent owner of this dialog. This is used for correct
     *            parenting.
     * @return true if the options have been changed.
     */
    public boolean invoke(JFrame owner) {
	JTabbedPane tabbedPane = new JTabbedPane();
	TitlePanel titlePanel;

	// Create the title panel for this dialog
	titlePanel = new TitlePanel(
		Res.getString("title.advanced.connection.preferences"), "",
		SparkRes.getImageIcon(SparkRes.BLANK_24x24), true);

	tabbedPane.addTab(Res.getString("tab.general"), generalPanel);
	if (!Default.getBoolean(Default.PROXY_DISABLED))
	    tabbedPane.addTab(Res.getString("tab.proxy"), proxyPanel);
	if (!Default.getBoolean(Default.SSO_DISABLED))
	    tabbedPane.addTab(Res.getString("tab.sso"), ssoPanel);
	if (!Default.getBoolean(Default.PKI_DISABLED))
	    tabbedPane.addTab(Res.getString("tab.pki"), pkiPanel);

	// Construct main panel w/ layout.
	final JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BorderLayout());
	mainPanel.add(titlePanel, BorderLayout.NORTH);

	// The user should only be able to close this dialog.
	Object[] options = { Res.getString("ok"), Res.getString("cancel"),
		Res.getString("use.default") };
	optionPane = new JOptionPane(tabbedPane, JOptionPane.PLAIN_MESSAGE,
		JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

	mainPanel.add(optionPane, BorderLayout.CENTER);

	optionsDialog = new JDialog(owner, Res.getString("title.preferences"),
		true);
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
     * @param e
     *            the property change event.
     */
    public void propertyChange(PropertyChangeEvent e) {
	String value = (String) optionPane.getValue();
	if (Res.getString("cancel").equals(value)) {
	    optionsDialog.setVisible(false);
	} else if (Res.getString("ok").equals(value)) {

	    boolean valid = generalPanel.validate_settings();
	    valid = valid && proxyPanel.validate_settings();
	    valid = valid && ssoPanel.validate_settings();
	    valid = valid && pkiPanel.validate_settings();

	    if (valid) {
		generalPanel.saveSettings();
		proxyPanel.saveSettings();
		ssoPanel.saveSettings();
		pkiPanel.saveSettings();
		SettingsManager.saveSettings();
		optionsDialog.setVisible(false);
	    } else {
		optionPane.removePropertyChangeListener(this);
		optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
		optionPane.addPropertyChangeListener(this);
	    }
	} else {
	    // Some unknown operation happened
	    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	}
    }

    /**
     * Internal class to set General settings
     */
    private class GeneralPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -3628642430429935901L;
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
	private JCheckBox compressionBox = new JCheckBox();
	private JCheckBox debuggerBox = new JCheckBox();

	public GeneralPanel() {
	    ResourceUtils.resLabel(portLabel, portField,
		    Res.getString("label.port"));
	    ResourceUtils.resLabel(timeOutLabel, timeOutField,
		    Res.getString("label.response.timeout"));
	    ResourceUtils.resButton(autoLoginBox,
		    Res.getString("label.auto.login"));
	    ResourceUtils.resButton(useSSLBox, Res.getString("label.old.ssl"));
	    ResourceUtils.resLabel(xmppHostLabel, xmppHostField,
		    Res.getString("label.host"));
	    ResourceUtils.resButton(autoDiscoverBox,
		    Res.getString("checkbox.auto.discover.port"));
	    ResourceUtils.resLabel(resourceLabel, resourceField,
		    Res.getString("label.resource"));
	    ResourceUtils.resButton(compressionBox,
		    Res.getString("checkbox.use.compression"));
	    ResourceUtils.resButton(debuggerBox,
		    Res.getString("checkbox.use.debugger.on.startup"));

	    portField.setText(Integer.toString(localPreferences.getXmppPort()));
	    timeOutField
		    .setText(Integer.toString(localPreferences.getTimeOut()));
	    autoLoginBox.setSelected(localPreferences.isAutoLogin());
	    useSSLBox.setSelected(localPreferences.isSSL());
	    xmppHostField.setText(localPreferences.getXmppHost());
	    resourceField.setText(localPreferences.getResource());

	    autoDiscoverBox.addActionListener(this);

	    autoDiscoverBox.setSelected(!localPreferences
		    .isHostAndPortConfigured());
	    updateAutoDiscovery();

	    compressionBox.setSelected(localPreferences.isCompressionEnabled());

	    debuggerBox.setSelected(localPreferences.isDebuggerEnabled());

	    final JPanel connectionPanel = new JPanel();
	    connectionPanel.setLayout(new GridBagLayout());
	    connectionPanel.setBorder(BorderFactory.createTitledBorder(Res
		    .getString("group.connection")));

	    setLayout(new GridBagLayout());
	    add(autoDiscoverBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    connectionPanel.add(xmppHostLabel, new GridBagConstraints(0, 0, 2,
		    1, 0.0, 0.0, GridBagConstraints.WEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    connectionPanel.add(xmppHostField, new GridBagConstraints(2, 0, 1,
		    1, 0.0, 0.0, GridBagConstraints.WEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 200, 0));
	    connectionPanel.add(portLabel, new GridBagConstraints(0, 1, 2, 1,
		    0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    connectionPanel.add(portField, new GridBagConstraints(2, 1, 1, 1,
		    0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 50, 0));
	    add(connectionPanel, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0,
		    GridBagConstraints.WEST, GridBagConstraints.BOTH,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(resourceLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(resourceField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 100, 0));
	    add(timeOutLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(timeOutField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 50, 0));
	    add(useSSLBox, new GridBagConstraints(0, 4, 2, 1, 0.0, 1.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(compressionBox, new GridBagConstraints(0, 5, 2, 1, 0.0, 1.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(debuggerBox, new GridBagConstraints(0, 6, 2, 1, 0.0, 1.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));
	}

	/**
	 * Updates local preferences with auto discovery settings.
	 */
	private void updateAutoDiscovery() {
	    boolean isSelected = autoDiscoverBox.isSelected();
	    xmppHostField.setEnabled(!isSelected);
	    portField.setEnabled(!isSelected);
	    localPreferences.setHostAndPortConfigured(!isSelected);
	    SettingsManager.saveSettings();
	}

	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == autoDiscoverBox) {
		updateAutoDiscovery();
	    }
	}

	public boolean validate_settings() {
	    String timeOut = timeOutField.getText();
	    String port = portField.getText();
	    String resource = resourceField.getText();

	    boolean valid = true;

	    try {
		Integer.valueOf(timeOut);
	    } catch (NumberFormatException numberFormatException) {
		JOptionPane
			.showMessageDialog(optionsDialog,
				Res.getString("message.supply.valid.timeout"),
				Res.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
		timeOutField.requestFocus();
		valid = false;
	    }

	    try {
		Integer.valueOf(port);
	    } catch (NumberFormatException numberFormatException) {
		JOptionPane
			.showMessageDialog(optionsDialog,
				Res.getString("message.supply.valid.port"),
				Res.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
		portField.requestFocus();
		valid = false;
	    }

	    if (!ModelUtil.hasLength(resource)) {
		JOptionPane
			.showMessageDialog(optionsDialog,
				Res.getString("message.supply.resource"),
				Res.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
		resourceField.requestFocus();
		valid = false;
	    }

	    return valid;
	}

	public void saveSettings() {

	    localPreferences
		    .setTimeOut(Integer.parseInt(timeOutField.getText()));
	    localPreferences.setXmppPort(Integer.parseInt(portField.getText()));
	    localPreferences.setSSL(useSSLBox.isSelected());
	    localPreferences.setXmppHost(xmppHostField.getText());
	    localPreferences.setCompressionEnabled(compressionBox.isSelected());
	    localPreferences.setDebuggerEnabled(debuggerBox.isSelected());
	    localPreferences.setResource(resourceField.getText());
	    SettingsManager.saveSettings();
	}
    }

    /**
     * Internal class to allow setting of proxies within Spark.
     */
    private class ProxyPanel extends JPanel {
	private static final long serialVersionUID = 4652063977305639878L;
	private JCheckBox useProxyBox = new JCheckBox();
	private JComboBox protocolBox = new JComboBox();
	private JTextField hostField = new JTextField();
	private JTextField portField = new JTextField();
	private JTextField usernameField = new JTextField();
	private JPasswordField passwordField = new JPasswordField();

	/**
	 * Construct UI.
	 */
	public ProxyPanel() {
	    JLabel protocolLabel = new JLabel();
	    JLabel hostLabel = new JLabel();
	    JLabel portLabel = new JLabel();
	    JLabel usernameLabel = new JLabel();
	    JLabel passwordLabel = new JLabel();

	    protocolBox.addItem("SOCKS");
	    protocolBox.addItem("HTTP");

	    // Add ResourceUtils
	    ResourceUtils.resButton(useProxyBox,
		    Res.getString("checkbox.use.proxy.server"));
	    ResourceUtils.resLabel(protocolLabel, protocolBox,
		    Res.getString("label.protocol"));
	    ResourceUtils.resLabel(hostLabel, hostField,
		    Res.getString("label.host"));
	    ResourceUtils.resLabel(portLabel, portField,
		    Res.getString("label.port"));
	    ResourceUtils.resLabel(usernameLabel, usernameField,
		    Res.getString("label.username"));
	    ResourceUtils.resLabel(passwordLabel, passwordField,
		    Res.getString("label.password"));

	    setLayout(new GridBagLayout());
	    add(useProxyBox, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(protocolLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(protocolBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(hostLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(hostField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(portLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(portField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(usernameLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(usernameField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(passwordLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(passwordField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    new Insets(5, 5, 5, 5), 0, 0));

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
	    
	    if(Default.getString("PROXY_PROTOCOL").length()>0)
	    {
		protocolBox.setSelectedItem(Default.getString("PROXY_PROTOCOL"));
		protocolBox.setEnabled(false);
		useProxyBox.setSelected(true);
		useProxyBox.setVisible(false);
	    }
	    if(Default.getString("PROXY_HOST").length()>0)
	    {
		hostField.setText(Default.getString("PROXY_HOST"));
		hostField.setEnabled(false);
		useProxyBox.setSelected(true);
		useProxyBox.setVisible(false);
	    }
	    if(Default.getString("PROXY_PORT").length()>0)
	    {
		portField.setText(Default.getString("PROXY_PORT"));
		portField.setEnabled(false);
	    }

	}

	/**
	 * Enables the fields of the proxy panel.
	 * 
	 * @param enable
	 *            true if all fields should be enabled, otherwise false.
	 */
	private void enableFields(boolean enable) {
	    Component[] comps = getComponents();
	    for (Component comp1 : comps) {
		if (comp1 instanceof JTextField || comp1 instanceof JComboBox) {
		    JComponent comp = (JComponent) comp1;
		    comp.setEnabled(enable);
		}
	    }
	}

	/**
	 * Returns true if a proxy is set.
	 * 
	 * @return true if a proxy is set.
	 */
	//TODO REMOVE
	@SuppressWarnings("unused")
	public boolean useProxy() {
	    return useProxyBox.isSelected();
	}

	/**
	 * Returns the protocol to use for this proxy.
	 * 
	 * @return the protocol.
	 */
	public String getProtocol() {
	    return (String) protocolBox.getSelectedItem();
	}

	/**
	 * Returns the host to use for this proxy.
	 * 
	 * @return the host.
	 */
	public String getHost() {
	    return hostField.getText();
	}

	/**
	 * Returns the port to use with this proxy.
	 * 
	 * @return the port to use.
	 */
	public String getPort() {
	    return portField.getText();
	}

	/**
	 * Returns the username to use with this proxy.
	 * 
	 * @return the username.
	 */
	public String getUsername() {
	    return usernameField.getText();
	}

	/**
	 * Returns the password to use with this proxy.
	 * 
	 * @return the password.
	 */
	public String getPassword() {
	    return new String(passwordField.getPassword());
	}

	public boolean validate_settings() {
	    boolean valid = true;

	    if (useProxyBox.isSelected()) {
		try {
		    Integer.valueOf(portField.getText());
		} catch (NumberFormatException numberFormatException) {
		    JOptionPane.showMessageDialog(optionsDialog,
			    Res.getString("message.supply.valid.port"),
			    Res.getString("title.error"),
			    JOptionPane.ERROR_MESSAGE);
		    portField.requestFocus();
		    valid = false;
		}

		if (!ModelUtil.hasLength(hostField.getText())) {
		    JOptionPane.showMessageDialog(optionsDialog,
			    Res.getString("message.supply.valid.host"),
			    Res.getString("title.error"),
			    JOptionPane.ERROR_MESSAGE);
		    hostField.requestFocus();
		    valid = false;
		}
	    }
	    return valid;
	}

	/**
	 * Persist the proxy settings to local preferences.
	 */
	public void saveSettings() {
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
	    } else {
		String host = localPreferences.getHost();
		String port = localPreferences.getPort();
		String protocol = localPreferences.getProtocol();

		boolean isValid = ModelUtil.hasLength(host)
			&& ModelUtil.hasLength(port);

		if (isValid) {
		    if (protocol.equals("SOCKS")) {
			System.setProperty("socksProxyHost", host);
			System.setProperty("socksProxyPort", port);
		    } else {
			System.setProperty("http.proxySet", "true");

			// Set https settings
			System.setProperty("https.proxyHost", host);
			System.setProperty("https.proxyPort", port);

			// Set http settings
			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", port);
		    }
		} else {
		    localPreferences.setProxyEnabled(false);
		}
	    }
	    SettingsManager.saveSettings();
	}
    }

    /**
     * Internal class to set SSO settings
     */
    private class SsoPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3661245275095536202L;
	private JCheckBox useSSOBox = new JCheckBox();
	private JPanel settingsPanel = new JPanel();
	private JCheckBox showAdvBox = new JCheckBox();
	private JLabel ssoRealmLabel = new JLabel();
	private JTextField ssoRealmField = new JTextField();
	private JLabel ssoKDCLabel = new JLabel();
	private JTextField ssoKDCField = new JTextField();
	private JLabel ssoMethodFileLabel = new JLabel();
	private JRadioButton ssoMethodFileRadio = new JRadioButton();
	private JLabel ssoMethodDNSLabel = new JLabel();
	private JRadioButton ssoMethodDNSRadio = new JRadioButton();
	private JLabel ssoMethodManualLabel = new JLabel();
	private JRadioButton ssoMethodManualRadio = new JRadioButton();
	private ButtonGroup ssoMethodRadio = new ButtonGroup();

	public SsoPanel() {
	    ResourceUtils.resButton(useSSOBox,
		    Res.getString("title.advanced.connection.usesso"));
	    ResourceUtils.resButton(showAdvBox,
		    Res.getString("title.advanced.connection.preferences"));

	    useSSOBox.addActionListener(this);
	    showAdvBox.addActionListener(this);

	    final WrappedLabel wrappedLabel = new WrappedLabel();
	    String principalName = null;
	    try {
		principalName = getPrincipalName();
	    } catch (Exception e) {
		// Ignore
	    }

	    if (ModelUtil.hasLength(principalName)) {
		wrappedLabel.setText(Res.getString("title.advanced.connection.sso.account",principalName));
	    } else {
		wrappedLabel.setText(Res.getString("title.advanced.connection.sso.noprincipal"));
	    }
	    wrappedLabel.setBackground(Color.white);

	    String method = localPreferences.getSSOMethod();
	    if (ModelUtil.hasLength(method)) {
		if (method.equals("file")) {
		    ssoMethodFileRadio.setSelected(true);
		} else if (method.equals("dns")) {
		    ssoMethodDNSRadio.setSelected(true);
		} else if (method.equals("manual")) {
		    ssoMethodManualRadio.setSelected(true);
		} else {
		    ssoMethodFileRadio.setSelected(true);
		}
	    } else {
		ssoMethodFileRadio.setSelected(true);
	    }

	    if (ModelUtil.hasLength(localPreferences.getSSORealm())) {
		ssoRealmField.setText(localPreferences.getSSORealm());
	    }
	    if (ModelUtil.hasLength(localPreferences.getSSOKDC())) {
		ssoKDCField.setText(localPreferences.getSSOKDC());
	    }

	    ssoMethodFileLabel.setText(Res.getString("checkbox.use.krbconf"));
	    ssoMethodDNSLabel.setText(Res.getString("checkbox.use.krb.dns"));
	    ssoMethodManualLabel.setText(Res
		    .getString("checkbox.use.specify.below"));
	    ssoRealmLabel.setText("    " + Res.getString("label.krb.realm"));
	    ssoKDCLabel.setText("    " + Res.getString("label.krb.kdc"));

	    ssoMethodRadio.add(ssoMethodFileRadio);
	    ssoMethodRadio.add(ssoMethodDNSRadio);
	    ssoMethodRadio.add(ssoMethodManualRadio);

	    useSSOBox.setSelected(localPreferences.isSSOEnabled());

	    ssoMethodFileRadio.setEnabled(localPreferences.isSSOEnabled());
	    ssoMethodDNSRadio.setEnabled(localPreferences.isSSOEnabled());
	    ssoMethodManualRadio.setEnabled(localPreferences.isSSOEnabled());
	    ssoRealmField.setEnabled(localPreferences.isSSOEnabled());
	    ssoKDCField.setEnabled(localPreferences.isSSOEnabled());

	    showAdvBox.setSelected(localPreferences.getSSOAdv());
	    settingsPanel.setVisible(localPreferences.getSSOAdv());

	    setLayout(new GridBagLayout());
	    add(useSSOBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(showAdvBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(wrappedLabel,
		    new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
			    GridBagConstraints.NORTHWEST,
			    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
				    5), 0, 0));
	    settingsPanel.setLayout(new GridBagLayout());
	    settingsPanel.add(ssoMethodFileLabel, new GridBagConstraints(0, 0,
		    1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    settingsPanel.add(ssoMethodFileRadio,
		    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			    GridBagConstraints.NORTHWEST,
			    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
				    5), 0, 0));
	    settingsPanel.add(ssoMethodDNSLabel, new GridBagConstraints(0, 1,
		    1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    settingsPanel.add(ssoMethodDNSRadio,
		    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			    GridBagConstraints.NORTHWEST,
			    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
				    5), 0, 0));
	    settingsPanel.add(ssoMethodManualLabel, new GridBagConstraints(0,
		    2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    settingsPanel.add(ssoMethodManualRadio,
		    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
			    GridBagConstraints.NORTHWEST,
			    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
				    5), 0, 0));
	    settingsPanel.add(ssoRealmLabel, new GridBagConstraints(0, 3, 1, 1,
		    0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    settingsPanel.add(ssoRealmField,
		    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
			    GridBagConstraints.NORTHWEST,
			    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
				    5), 0, 0));
	    settingsPanel.add(ssoKDCLabel, new GridBagConstraints(0, 4, 1, 1,
		    0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    settingsPanel.add(ssoKDCField,
		    new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
			    GridBagConstraints.NORTHWEST,
			    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
				    5), 0, 0));
	    add(settingsPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST,
		    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 100,
		    0));

	}

	/**
	 * Returns the principal name if one exists.
	 * 
	 * @return the name (ex. derek) of the principal.
	 * @throws Exception
	 *             thrown if a Principal was not found.
	 */
	private String getPrincipalName() throws Exception {
	    if (localPreferences.getDebug()) {
		System.setProperty("java.security.krb5.debug", "true");
	    }
	    System.setProperty("javax.security.auth.useSubjectCredsOnly",
		    "false");
	    GSSAPIConfiguration config = new GSSAPIConfiguration(false);
	    Configuration.setConfiguration(config);

	    LoginContext lc;
	    try {
		lc = new LoginContext("com.sun.security.jgss.krb5.initiate");
		lc.login();
	    } catch (LoginException le) {
		Log.debug(le.getMessage());
		return null;
	    }

	    Subject mySubject = lc.getSubject();

	    for (Principal p : mySubject.getPrincipals()) {
		String name = p.getName();
		int indexOne = name.indexOf("@");
		if (indexOne != -1) {
		    return name;
		}
	    }
	    return null;
	}

	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == useSSOBox) {
		ssoMethodFileRadio.setEnabled(useSSOBox.isSelected());
		ssoMethodDNSRadio.setEnabled(useSSOBox.isSelected());
		ssoMethodManualRadio.setEnabled(useSSOBox.isSelected());
		ssoRealmField.setEnabled(useSSOBox.isSelected());
		ssoKDCField.setEnabled(useSSOBox.isSelected());
	    } else if (e.getSource() == showAdvBox) {
		settingsPanel.setVisible(showAdvBox.isSelected());
	    }
	}

	public boolean validate_settings() {

	    boolean valid = true;

	    if (useSSOBox.isSelected() && showAdvBox.isSelected()) {
		if (ssoMethodManualRadio.isSelected()) {
		    if (!ModelUtil.hasLength(ssoRealmField.getText())) {
			JOptionPane.showMessageDialog(optionsDialog,
				"You must specify a realm",
				Res.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
			ssoRealmField.requestFocus();
			valid = false;
		    }
		    if (!ModelUtil.hasLength(ssoKDCField.getText())) {
			JOptionPane.showMessageDialog(optionsDialog,
				"You must specify a KDC",
				Res.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
			ssoKDCField.requestFocus();
			valid = false;
		    }
		}
	    }

	    return valid;
	}

	public void saveSettings() {
	    localPreferences.setSSOEnabled(useSSOBox.isSelected());
	    if (ssoMethodFileRadio.isSelected()) {
		localPreferences.setSSOMethod("file");
	    } else if (ssoMethodDNSRadio.isSelected()) {
		localPreferences.setSSOMethod("dns");
	    } else if (ssoMethodManualRadio.isSelected()) {
		localPreferences.setSSOMethod("manual");
		localPreferences.setSSORealm(ssoRealmField.getText());
		localPreferences.setSSOKDC(ssoKDCField.getText());
	    } else {
		localPreferences.setSSOMethod("file");
	    }
	    SettingsManager.saveSettings();
	}
    }

    /**
     * Internal class to set PKI settings
     */

    private class PkiPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 2872543055208753622L;
	private JLabel usePKILabel = new JLabel();
	private JCheckBox usePKIBox = new JCheckBox();
	private JLabel pkiStoreLabel = new JLabel();
	private JComboBox pkiStore = new JComboBox();
	private JFileChooser fileChooser = new JFileChooser();
	private JButton fileButton = new JButton();
	private JTextField fileField = new JTextField();
	private JPanel filePanel = new JPanel();
	private JLabel trustStorePasswordLabel = new JLabel();
	private JPasswordField trustStorePassword = new JPasswordField();
	private JTextField trustStoreField = new JTextField();
	private JButton trustStoreButton = new JButton();
	private JPanel trustStorePanel = new JPanel();

	public PkiPanel() {
	    ResourceUtils.resButton(usePKIBox,
		    Res.getString("checkbox.use.pki.authentication"));
	    ResourceUtils.resLabel(pkiStoreLabel, pkiStore,
		    Res.getString("label.which.pki.method"));
	    ResourceUtils.resButton(fileButton,
		    Res.getString("label.choose.file"));
	    ResourceUtils.resButton(trustStoreButton,
		    Res.getString("label.choose.file"));
	    ResourceUtils.resLabel(trustStorePasswordLabel, trustStorePassword,
		    Res.getString("label.trust.store.password"));

	    pkiStore.addItem("Java Keystore");
	    pkiStore.addItem("PKCS#11");
	    // pkiStore.addItem("X.509 PEM File");
	    pkiStore.addItem("Apple KeyChain");

	    usePKIBox.setSelected(localPreferences.isPKIEnabled());

	    if (ModelUtil.hasLength(localPreferences.getPKIStore())) {
		if (localPreferences.getPKIStore().equals("PKCS11")) {
		    pkiStore.setSelectedItem("PKCS#11");
		    if (ModelUtil
			    .hasLength(localPreferences.getPKCS11Library())) {
			fileField.setText(localPreferences.getPKCS11Library());
		    } else {
			fileField.setText("");
		    }
		} else if (localPreferences.getPKIStore().equals("X509")) {
		    pkiStore.setSelectedItem("X.509 PEM File");
		    // if(ModelUtil.hasLength(localPreferences.getPEMFile())) {
		    // fileField.setText(localPreferences.getPEMFile());
		    // }
		    // else {
		    fileField.setText("");
		    // }
		} else if (localPreferences.getPKIStore().equals(
			"Apple KeyChain")) {
		    fileField.setText("");
		} else {
		    pkiStore.setSelectedItem("Java Keystore");
		    if (ModelUtil.hasLength(localPreferences.getJKSPath())) {
			fileField.setText(localPreferences.getJKSPath());
		    } else {
			fileField.setText("");
		    }
		}
	    } else {
		pkiStore.setSelectedItem("Java Keystore");
		if (ModelUtil.hasLength(localPreferences.getJKSPath())) {
		    fileField.setText(localPreferences.getJKSPath());
		} else {
		    fileField.setText("");
		}
	    }

	    if (ModelUtil.hasLength(localPreferences.getTrustStorePath())) {
		trustStoreField.setText(localPreferences.getTrustStorePath());
	    }

	    if (ModelUtil.hasLength(localPreferences.getTrustStorePassword())) {
		trustStorePassword.setText(localPreferences
			.getTrustStorePassword());
	    }

	    pkiStore.setEnabled(usePKIBox.isSelected());
	    filePanel.setEnabled(usePKIBox.isSelected());
	    fileField.setEnabled(usePKIBox.isSelected());
	    fileButton.setEnabled(usePKIBox.isSelected());

	    setLayout(new GridBagLayout());

	    add(usePKIBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(usePKILabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(pkiStoreLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(pkiStore, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));

	    filePanel.setLayout(new GridBagLayout());
	    filePanel.setBorder(BorderFactory.createTitledBorder(Res
		    .getString("label.keystore.location")));
	    filePanel.add(fileField, new GridBagConstraints(0, 0, 1, 1, 0.0,
		    0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 100,
		    0));
	    filePanel.add(fileButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
		    0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		    new Insets(5, 5, 5, 5), 0, 0));
	    add(filePanel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST,
		    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 150,
		    0));

	    trustStorePanel.setLayout(new GridBagLayout());
	    trustStorePanel.setBorder(BorderFactory.createTitledBorder(Res
		    .getString("label.truststore.location")));
	    trustStorePanel.add(trustStoreField, new GridBagConstraints(0, 0,
		    1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 100,
		    0));
	    trustStorePanel.add(trustStoreButton, new GridBagConstraints(1, 0,
		    1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    trustStorePanel.add(trustStorePasswordLabel,
		    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			    GridBagConstraints.NORTHWEST,
			    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0,
			    0));
	    trustStorePanel.add(trustStorePassword, new GridBagConstraints(1,
		    1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
		    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 100,
		    0));
	    add(trustStorePanel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
		    GridBagConstraints.NORTHWEST,
		    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 150,
		    0));

	    usePKIBox.addActionListener(this);
	    pkiStore.addActionListener(this);
	    fileButton.addActionListener(this);
	    trustStoreButton.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {

	    if (e.getSource() == usePKIBox) {
		pkiStore.setEnabled(usePKIBox.isSelected());
		filePanel.setEnabled(usePKIBox.isSelected());
		fileField.setEnabled(usePKIBox.isSelected());
		fileButton.setEnabled(usePKIBox.isSelected());
	    } else if (e.getSource() == pkiStore) {
		if ((pkiStore.getSelectedItem()).equals("PKCS#11")) {
		    filePanel.setBorder(BorderFactory.createTitledBorder(Res
			    .getString("label.pkcs.library.file")));
		    if (ModelUtil
			    .hasLength(localPreferences.getPKCS11Library())) {
			fileField.setText(localPreferences.getPKCS11Library());
		    } else {
			fileField.setText("");
		    }
		} else if ((pkiStore.getSelectedItem())
			.equals("X.509 PEM File")) {
		    filePanel.setBorder(BorderFactory.createTitledBorder(Res
			    .getString("label.x509.certificate")));
		    // if(ModelUtil.hasLength(localPreferences.getPEMFile())) {
		    // fileField.setText(localPreferences.getPEMFile());
		    // } else {
		    // fileField.setText("");
		    // }
		} else if ((pkiStore.getSelectedItem())
			.equals("Apple KeyChain")) {
		    filePanel.setBorder(BorderFactory.createTitledBorder(Res
			    .getString("label.apple.keychain")));
		} else {
		    filePanel.setBorder(BorderFactory.createTitledBorder(Res
			    .getString("label.keystore.location")));
		    if (ModelUtil.hasLength(localPreferences.getJKSPath())) {
			fileField.setText(localPreferences.getJKSPath());
		    } else {
			fileField.setText("");
		    }
		}
	    } else if (e.getSource() == fileButton) {
		int retval = fileChooser.showOpenDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
		    File file = fileChooser.getSelectedFile();
		    fileField.setText(file.getAbsolutePath());
		}
	    } else if (e.getSource() == trustStoreButton) {
		int retval = fileChooser.showOpenDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
		    File file = fileChooser.getSelectedFile();
		    trustStoreField.setText(file.getAbsolutePath());
		}
	    }
	}

	public boolean validate_settings() {

	    boolean valid = true;

	    if (usePKIBox.isSelected()) {
		if (!ModelUtil.hasLength(fileField.getText())) {
		    JOptionPane.showMessageDialog(optionsDialog,
			    "You must specify a file location",
			    Res.getString("title.error"),
			    JOptionPane.ERROR_MESSAGE);
		    fileField.requestFocus();
		    valid = false;
		}
	    }
	    return valid;
	}

	public void saveSettings() {

	    localPreferences.setPKIEnabled(usePKIBox.isSelected());
	    localPreferences.setPKIStore((String) pkiStore.getSelectedItem());
	    if ((pkiStore.getSelectedItem()).equals("PKCS#11")) {
		localPreferences.setPKIStore("PKCS11");
		localPreferences.setPKCS11Library(fileField.getText());
	    } else if ((pkiStore.getSelectedItem()).equals("X.509 Certificate")) {
		localPreferences.setPKIStore("X509");
		// localPreferences.setPEMFile(fileField.getText());
	    } else if ((pkiStore.getSelectedItem()).equals("Apple KeyChain")) {
		localPreferences.setPKIStore("Apple");
	    } else {
		localPreferences.setPKIStore("JKS");
		localPreferences.setJKSPath(fileField.getText());
	    }
	    localPreferences.setTrustStorePath(trustStoreField.getText());
	    localPreferences.setTrustStorePassword(new String(
		    trustStorePassword.getPassword()));
	    SettingsManager.saveSettings();
	}
    }
}
