/**
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

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.ConnectionConfiguration.DnssecMode;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.parsing.ExceptionLoggingCallback;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smackx.bob.element.BoBDataExtension;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.ui.DataFormUI;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.SparkSSLSocketFactory;
import org.jivesoftware.sparkimpl.certificates.SparkSSLContextCreator;
import org.jivesoftware.sparkimpl.certificates.SparkTrustManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;
import org.minidns.dnsname.DnsName;

import javax.net.ssl.SSLContext;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.util.*;
import java.util.List;

import static java.awt.GridBagConstraints.*;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static org.jivesoftware.sparkimpl.certificates.SparkSSLContextCreator.Options.ONLY_SERVER_SIDE;

/**
 * Allows the creation of accounts on an XMPP server.
 */
public class AccountCreationWizard extends JPanel {
    private final JComboBox<String> serverField = new JComboBox<>();

    private final JButton startRegistrationButton = new JButton();

    private final JLabel instructionsLabel = new JLabel();

    private final FormPanel formPanel = new FormPanel();

    private final JPanel formPanelFields = new JPanel();

    private final JLabel captcha = new JLabel();

    private final JButton createAccountButton = new JButton();

    private JDialog dialog;

    private DataFormUI registrationForm;

    private boolean registered;
    private XMPPConnection connection = null;
    private final JProgressBar progressBar;

    static class FormPanel extends JPanel {
        private final JTextField usernameField = new JTextField();

        private final JPasswordField passwordField = new JPasswordField();

        private final JPasswordField confirmPasswordField = new JPasswordField();

        public FormPanel() {
            super();
            JLabel usernameLabel = new JLabel();
            ResourceUtils.resLabel( usernameLabel, usernameField, Res.getString("label.username") + ":");
            JLabel passwordLabel = new JLabel();
            ResourceUtils.resLabel( passwordLabel, passwordField, Res.getString("label.password") + ":");
            JLabel confirmPasswordLabel = new JLabel();
            ResourceUtils.resLabel( confirmPasswordLabel, confirmPasswordField, Res.getString("label.confirm.password") + ":");

            setLayout(new GridBagLayout());
            Insets insets = new Insets(5, 5, 5, 5);
            add( usernameLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
            add(usernameField, new GridBagConstraints(1, 1, 3, 1, 1, 0, WEST, HORIZONTAL, insets, 150, 0));

            add( passwordLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
            add(passwordField, new GridBagConstraints(1, 2, 3, 1, 0, 0, WEST, HORIZONTAL, insets, 0, 0));

            add( confirmPasswordLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
            add(confirmPasswordField, new GridBagConstraints(1, 3, 3, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        }
    }

    public AccountCreationWizard() {
        // Associate Mnemonics
        serverField.setEditable(true);
        List<String> providers = XmppProviders.getXmppProvidersModel();
        for (String provider : providers) {
            serverField.addItem(provider);
        }
        // Randomly pre-select a provider
        int randomProviderIdx = new Random().nextInt(providers.size());
        serverField.setSelectedIndex(randomProviderIdx);

        ResourceUtils.resButton(startRegistrationButton, Res.getString("button.start.registration"));
        startRegistrationButton.addActionListener( actionEvent -> startRegistration() );

        instructionsLabel.setVisible(false);

        formPanel.setVisible(false);
        formPanelFields.setVisible(false);

        captcha.setPreferredSize(new java.awt.Dimension(250, 80));
        captcha.setRequestFocusEnabled(false);
        captcha.setHorizontalAlignment(SwingConstants.CENTER);
        instructionsLabel.setVisible(false);

        JLabel serverLabel = new JLabel();
        ResourceUtils.resLabel( serverLabel, serverField, Res.getString("label.server") + ":");
        ResourceUtils.resButton(createAccountButton, Res.getString("button.create.account"));
        createAccountButton.setEnabled(false);
        createAccountButton.addActionListener( actionEvent -> createAccount() );

        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        JButton closeButton = new JButton();
        ResourceUtils.resButton( closeButton, Res.getString("button.close"));
        closeButton.addActionListener( actionEvent -> dialog.dispose() );

        setLayout(new GridBagLayout());
        // Add component to UI
        Insets insets = new Insets(5, 5, 5, 5);
        add( serverLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(serverField, new GridBagConstraints(1, 0, 3, 1, 0, 0, WEST, HORIZONTAL, insets, 0, 0));
        add(startRegistrationButton, new GridBagConstraints(1, 1, 3, 1, 0, 0, WEST, HORIZONTAL, insets, 0, 0));
        add(instructionsLabel, new GridBagConstraints(0, 2, 4, 1, 1, 0, WEST, BOTH, insets, 0, 0));
        add(formPanel, new GridBagConstraints(0, 3, 4, 1, 1, 0, WEST, BOTH, insets, 0, 0));
        add(formPanelFields, new GridBagConstraints(0, 4, 4, 1, 1, 0, WEST, BOTH, insets, 0, 0));
        add(captcha, new GridBagConstraints(0, 5, 4, 1, 1, 0, CENTER, BOTH, insets, 0, 0));
        add(progressBar, new GridBagConstraints(1, 6, 4, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        add(createAccountButton, new GridBagConstraints(2, 7, 1, 1, 1, 0, EAST, NONE, insets, 0, 0));
        add( closeButton, new GridBagConstraints(3, 7, 1, 1, 0, 0, EAST, NONE, insets, 0, 0));
    }

    /**
     * Returns the username to use for the new account.
     */
    public String getUsername() {
        return XmppStringUtils.escapeLocalpart(formPanel.usernameField.getText().toLowerCase());
    }

    /**
     * Returns the username to use for the new account.
     */
    public String getUsernameWithoutEscape() {
        return formPanel.usernameField.getText();
    }
    
    /**
     * Returns the password to use for the new account.
     */
    public String getPassword() {
        return new String(formPanel.passwordField.getPassword());
    }

    /**
     * Returns the confirmation password to use for the new account.
     */
    public String getConfirmPassword() {
        return new String(formPanel.confirmPasswordField.getPassword());
    }

    /**
     * Returns the server to use with the new account.
     */
    public String getServer() {
        String selectedServer = (String) serverField.getSelectedItem();
        return selectedServer != null ? selectedServer.trim() : "";
    }

    /**
     * Returns true if the passwords match.
     */
    public boolean isPasswordValid() {
        return getPassword().equals(getConfirmPassword());
    }

    /**
     * Start registration and fetch signup form.
     */
    private void startRegistration() {
        final Component ui = this;
        try {
            connection = getConnection();
        } catch (SmackException | IOException | XMPPException e) {
            String th = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            JOptionPane.showMessageDialog(ui, Res.getString("message.connection.failed", getServer())
                + "\n" + th, Res.getString("title.create.problem"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            final AccountManager accountManager = AccountManager.getInstance(connection);
            if (accountManager.supportsAccountCreation()) {
                formPanel.setVisible(true);
                createAccountButton.setEnabled(true);
                String instructions = null;
                Icon captchaIcon = null;
                Registration info = accountManager.getRegistrationInfo();
                if (info != null) {
                    // try to get the CAPTCHA image from <data>
                    //  <data type="image/png" max-age="0" cid="sha1+HASH_HERE@bob.xmpp.org" xmlns="urn:xmpp:bob">BASE64_OF_PNG_HERE</data>
                    BoBDataExtension captchaBob = info.getExtension(BoBDataExtension.class);
                    if (captchaBob != null && captchaBob.getBobData().getType().startsWith("image/")) {
                        byte[] imageData = captchaBob.getBobData().getContent();
                        captchaIcon = new ImageIcon(imageData);
                    }
                    DataForm regFields = info.getExtension(DataForm.class);
                    if (regFields != null) {
                        registrationForm = getRegistrationForm(regFields, captchaIcon != null);
                        instructions = String.join("\n", regFields.getInstructions());
                    } else {
                        instructions = info.getInstructions();
                    }
                }
                if (registrationForm != null) {
                    formPanelFields.add(registrationForm);
                    formPanelFields.setVisible(true);
                }
                if (instructions != null) {
                    instructionsLabel.setText(instructions);
                    instructionsLabel.setVisible(true);
                }
                if (captchaIcon != null) {
                    captcha.setIcon(captchaIcon);
                    instructionsLabel.setVisible(true);
                }
            } else {
                String message = Res.getString("message.create.account.not.allowed");
                JOptionPane.showMessageDialog(this, message, Res.getString("title.create.problem"), JOptionPane.ERROR_MESSAGE);
            }
        } catch (XMPPException | SmackException | InterruptedException e) {
            StanzaError.Condition condition = null;
            if (e instanceof XMPPException.XMPPErrorException) {
                condition = ((XMPPException.XMPPErrorException) e).getStanzaError().getCondition();
            }
            if (condition == null) {
                condition = StanzaError.Condition.internal_server_error;
            }
            accountCreationFailed(condition);
        }
    }

    private DataFormUI getRegistrationForm(DataForm regFields, boolean noCaptcha) {
        // Create a new form without username and password that we will render ourselves
        DataForm.Builder extRegFields = regFields.asBuilder()
            .removeField("username")
            .removeField("password");
        if (noCaptcha) {
            extRegFields.removeField("captcha-fallback-url");
            extRegFields.removeField("captcha-fallback-text");
        }
        DataFormUI dataFormUI = new DataFormUI(extRegFields.build());
        return dataFormUI;
    }

    /**
     * Creates the new account using the supplied information.
     */
    private void createAccount() {
        boolean errors = false;
        String errorMessage = "";

        String server = getServer();
        if (!ModelUtil.hasLength(getUsername())) {
            errors = true;
            formPanel.usernameField.requestFocus();
            errorMessage = Res.getString("message.username.error");
        }
        else if (!ModelUtil.hasLength(getPassword())) {
            errors = true;
            errorMessage = Res.getString("message.password.error");
        }
        else if (!ModelUtil.hasLength(getConfirmPassword())) {
            errors = true;
            errorMessage = Res.getString("message.confirmation.password.error");
        }
        else if (!ModelUtil.hasLength(server)) {
            errors = true;
            errorMessage = Res.getString("message.account.error");
        }
        else if (!isPasswordValid()) {
            errors = true;
            errorMessage = Res.getString("message.confirmation.password.error");
        }

        if (errors) {
        	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            JOptionPane.showMessageDialog(this, errorMessage, Res.getString("title.create.problem"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        final Component ui = this;
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString(Res.getString("message.registering", server));
        progressBar.setVisible(true);

        final SwingWorker worker = new SwingWorker() {
            StanzaError.Condition condition = null;
            String th;


            @Override
			public Object construct() {
                try {
                    createAccountButton.setEnabled(false);
                    connection = getConnection();
                }
                catch (SmackException | IOException | XMPPException e) {
                    th = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                    return e;
                }
                try {
                    Map<String, String> attrs = getRegistrationAttributes();
                    Localpart localpart = Localpart.from(getUsername());
                    final AccountManager accountManager = AccountManager.getInstance(connection);
                    accountManager.createAccount(localpart, getPassword(), attrs);
                }
                catch (XMPPException | SmackException | InterruptedException | XmppStringprepException e) {
                    if ( e instanceof XMPPException.XMPPErrorException ) {
                        condition = ( (XMPPException.XMPPErrorException) e ).getStanzaError().getCondition();
                    }
                    if ( condition == null ) {
                        condition = StanzaError.Condition.internal_server_error;
                    }
                }
                return "ok";
            }

            @Override
			public void finished() {
                progressBar.setVisible(false);
                if (connection == null) {
                    if (ui.isShowing()) {
                        createAccountButton.setEnabled(true);
                        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                        JOptionPane.showMessageDialog(ui, Res.getString("message.connection.failed", server)
                            + "\n" + th, Res.getString("title.create.problem"), JOptionPane.ERROR_MESSAGE);
                        createAccountButton.setEnabled(true);
                    }
                    return;
                }

                if (condition == null) {
                    accountCreationSuccessful();
                }
                else {
                    accountCreationFailed(condition);
                }
            }
        };

        worker.start();
    }

    private Map<String, String> getRegistrationAttributes() {
        if (registrationForm == null) {
            return Map.of();
        }
        List<FormField> fields = registrationForm.getFilledForm().getFields();
        Map<String, String> attrs = new HashMap<>(fields.size());
        for (FormField f : fields) {
            attrs.put(f.getFieldName(), f.getFirstValue());
        }
        return attrs;
    }

    /**
     * Called if the account creation failed.
     *
     * @param condition the error code.
     */
    private void accountCreationFailed( StanzaError.Condition condition ) {
        String message;
        if (condition == StanzaError.Condition.conflict) {
            message = Res.getString("message.already.exists");
            formPanel.usernameField.setText("");
            formPanel.usernameField.requestFocus();
        } else if (condition == StanzaError.Condition.not_allowed || condition == StanzaError.Condition.forbidden || condition == StanzaError.Condition.service_unavailable) {
            message = Res.getString("message.create.account.not.allowed");
        } else {
            message = Res.getString("message.create.account");
        }
        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
        JOptionPane.showMessageDialog(this, message, Res.getString("title.create.problem"), JOptionPane.ERROR_MESSAGE);
        createAccountButton.setEnabled(true);
    }

    /**
     * Called if the account was created successfully.
     */
    private void accountCreationSuccessful() {
        registered = true;
        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
        JOptionPane.showMessageDialog(this, Res.getString("message.account.created"), Res.getString("title.account.created"), JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
    }

    /**
     * Invokes the AccountCreationWizard.
     *
     * @param parent the parent frame to use.
     */
    public void invoke(JFrame parent) {
        dialog = new JDialog(parent, Res.getString("title.create.new.account"), true);

        TitlePanel titlePanel = new TitlePanel(Res.getString("title.account.create.registration"), Res.getString("message.account.create"), null, true);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(titlePanel, BorderLayout.NORTH);
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.pack();
        dialog.setSize(400, 580);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Creates an XMPPConnection based on the users settings.
     */
    private XMPPConnection getConnection() throws SmackException, IOException, XMPPException
    {
        final LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
        int port = localPreferences.getXmppPort();
        String serverName = getServer();
        int checkForPort = serverName.indexOf(":");
        if (checkForPort != -1) {
            String portString = serverName.substring(checkForPort + 1);
            if (ModelUtil.hasLength(portString)) {
                // Set new port.
                port = Integer.parseInt(portString);
            }
        }

        ConnectionConfiguration.SecurityMode securityMode = localPreferences.getSecurityMode();
        boolean useDirectTls = localPreferences.isDirectTls();
        boolean hostPortConfigured = localPreferences.isHostAndPortConfigured();

        final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword( "username", "password" )
                .setXmppDomain( serverName )
                .setPort( port )
                .setCompressionEnabled( localPreferences.isCompressionEnabled() )
                .setSecurityMode( securityMode );

        if ( hostPortConfigured )
        {
            builder.setHost( localPreferences.getXmppHost() );
        }
        configureConnectionTls(builder, securityMode, useDirectTls, hostPortConfigured, serverName);
        final XMPPTCPConnectionConfiguration configuration = builder.build();
        final AbstractXMPPConnection connection = new XMPPTCPConnection( configuration );
        connection.setParsingExceptionCallback( new ExceptionLoggingCallback() );
        try {
            connection.connect();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return connection;
    }

    private void configureConnectionTls(XMPPTCPConnectionConfiguration.Builder builder, ConnectionConfiguration.SecurityMode securityMode, boolean useDirectTls, boolean hostPortConfigured, String serverName) throws SmackException.SmackMessageException {
        if (securityMode != ConnectionConfiguration.SecurityMode.disabled) {
            if (!useDirectTls) {
                // This use STARTTLS which starts initially plain connection to upgrade it to TLS, it use the same port as
                // plain connections which is 5222.
                SparkSSLContextCreator.Options options = ONLY_SERVER_SIDE;
                try {
                    SSLContext context = SparkSSLContextCreator.setUpContext(options);
                    builder.setSslContextFactory(() -> context);
                    builder.setSecurityMode(securityMode);
                    builder.setCustomX509TrustManager(new SparkTrustManager());
                } catch (NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException | KeyStoreException | NoSuchProviderException e) {
                    Log.warning("Could not establish secured connection", e);
                }
            } else { // useDirectTls
                if (!hostPortConfigured) {
                    // SMACK 4.1.9 does not support XEP-0368, and does not apply a port change, if the host is not changed too.
                    // Here, we force the host to be set (by doing a DNS lookup), and force the port to 5223 (which is the
                    // default 'old-style' SSL port).
                    DnsName serverNameDnsName = DnsName.from(serverName);
                    java.util.List<InetAddress> resolvedAddresses = DNSUtil.getDNSResolver().lookupHostAddress(serverNameDnsName, null, DnssecMode.disabled);
                    if (resolvedAddresses.isEmpty()) {
                        throw new SmackException.SmackMessageException("Could not resolve " + serverNameDnsName);
                    }
                    builder.setHost( resolvedAddresses.get( 0 ).getHostName() );
                    builder.setPort( 5223 );
                }
                SparkSSLContextCreator.Options options = ONLY_SERVER_SIDE;
                builder.setSocketFactory( new SparkSSLSocketFactory(options) );
                // SMACK 4.1.9  does not recognize an 'old-style' SSL socket as being secure, which will cause a failure when
                // the 'required' Security Mode is defined. Here, we work around this by replacing that security mode with an
                // 'if-possible' setting.
                builder.setSecurityMode( ConnectionConfiguration.SecurityMode.ifpossible );
            }
        }
    }

    /**
     * Returns true if the user is registered.
     */
    public boolean isRegistered() {
        return registered;
    }
}

