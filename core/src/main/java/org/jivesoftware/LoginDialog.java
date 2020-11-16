/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.ConnectionConfiguration.DnssecMode;
import org.jivesoftware.smack.parsing.ExceptionLoggingCallback;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.sasl.javax.SASLExternalMechanism;
import org.jivesoftware.smack.sasl.javax.SASLGSSAPIMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.spark.PluginManager;
import org.jivesoftware.spark.SessionManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.sasl.SASLGSSAPIv3CompatMechanism;
import org.jivesoftware.spark.ui.login.GSSAPIConfiguration;
import org.jivesoftware.spark.ui.login.LoginSettingDialog;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.*;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettings;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;
import org.minidns.dnsname.DnsName;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.net.ssl.SSLContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.List;

import static org.jivesoftware.spark.util.StringUtils.modifyWildcards;

/**
 * Dialog to log in a user into the Spark Server. The LoginDialog is used only
 * for login in registered users into the Spark Server.
 */
public class LoginDialog {
    private JFrame loginDialog;
    private static final String BUTTON_PANEL = "buttonpanel"; // NOTRANS
    private static final String PROGRESS_BAR = "progressbar"; // NOTRANS
    private LocalPreferences localPref;
    private final ArrayList<String> _usernames = new ArrayList<>();
    private String loginUsername;
    private String loginPassword;
    private String loginServer;

    /**
     * Empty Constructor
     */
    public LoginDialog() {
        localPref = SettingsManager.getLocalPreferences();

        // Check if upgraded needed.
        try {
            checkForOldSettings();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Invokes the LoginDialog to be visible.
     *
     * @param parentFrame the parentFrame of the Login Dialog. This is used
     *                    for correct parenting.
     */
    public void invoke(final JFrame parentFrame) {
        // Before creating any connections. Update proxy if needed.
        try {
            updateProxyConfig();
        } catch (Exception e) {
            Log.error(e);
        }

        // Construct Dialog
        EventQueue.invokeLater(() -> {
            loginDialog = new JFrame(Default.getString(Default.APPLICATION_NAME));
            loginDialog.setIconImage(SparkManager.getApplicationImage().getImage());
            LoginPanel loginPanel = new LoginPanel();
            final JPanel mainPanel = new LoginBackgroundPanel();
            final GridBagLayout mainLayout = new GridBagLayout();
            mainPanel.setLayout(mainLayout);

            final ImagePanel imagePanel = new ImagePanel();

            mainPanel.add(imagePanel,
                new GridBagConstraints(0, 0, 4, 1,
                    1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

            final String showPoweredBy = Default.getString(Default.SHOW_POWERED_BY);
            if (ModelUtil.hasLength(showPoweredBy) && "true".equals(showPoweredBy)) {
                // Handle Powered By for custom clients.
                final JLabel poweredBy = new JLabel(SparkRes.getImageIcon(SparkRes.POWERED_BY_IMAGE));
                mainPanel.add(poweredBy,
                    new GridBagConstraints(0, 1, 4, 1,
                        1.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 2, 0), 0, 0));

            }

            loginPanel.setOpaque(false);
            mainPanel.add(loginPanel,
                new GridBagConstraints(0, 2, 2, 1,
                    1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                    new Insets(0, 0, 2, 0), 0, 20));

            loginDialog.setContentPane(mainPanel);
            loginDialog.setLocationRelativeTo(parentFrame);

            loginDialog.setResizable(false);
            loginDialog.pack();

            // Center dialog on screen
            GraphicUtils.centerWindowOnScreen(loginDialog);

            // Show dialog
            loginDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    quitLogin();
                }
            });
            if (loginPanel.getUsername().trim().length() > 0) {
                loginPanel.getPasswordField().requestFocus();
            }

            if (!localPref.isStartedHidden() || !localPref.isAutoLogin()) {
                // Make dialog top most.
                loginDialog.setVisible(true);
            }
        });

    }

    //This method can be overwritten by subclasses to provide additional validations
    //(such as certificate download functionality when connecting)
    protected boolean beforeLoginValidations() {
        return true;
    }

    protected void afterLogin() {
        // Make certain Enterprise features persist across future logins
        persistEnterprise();

        // Load plugins before Workspace initialization to avoid any UI delays during plugin rendering, but after
        // Enterprise initialization, which can pull in additional plugin configuration (eg: blacklist).
        PluginManager.getInstance().loadPlugins();

        // Initialize and write default values from "Advanced Connection Preferences" to disk
        initAdvancedDefaults();
    }

    protected XMPPTCPConnectionConfiguration retrieveConnectionConfiguration() {
        int port = localPref.getXmppPort();

        int checkForPort = loginServer.indexOf(":");
        if (checkForPort != -1) {
            String portString = loginServer.substring(checkForPort + 1);
            if (ModelUtil.hasLength(portString)) {
                // Set new port.
                port = Integer.parseInt(portString);
            }
        }

        ConnectionConfiguration.SecurityMode securityMode = localPref.getSecurityMode();
        boolean useOldSSL = localPref.isSSL();
        boolean hostPortConfigured = localPref.isHostAndPortConfigured();

        ProxyInfo proxyInfo = null;
        if (localPref.isProxyEnabled()) {
            ProxyInfo.ProxyType pType = localPref.getProtocol().equals("SOCKS") ?
                ProxyInfo.ProxyType.SOCKS5 : ProxyInfo.ProxyType.HTTP;
            String pHost = ModelUtil.hasLength(localPref.getHost()) ?
                localPref.getHost() : null;
            int pPort = ModelUtil.hasLength(localPref.getPort()) ?
                Integer.parseInt(localPref.getPort()) : 0;
            String pUser = ModelUtil.hasLength(localPref.getProxyUsername()) ?
                localPref.getProxyUsername() : null;
            String pPass = ModelUtil.hasLength(localPref.getProxyPassword()) ?
                localPref.getProxyPassword() : null;

            if (pHost != null && pPort != 0) {

                if (pUser == null || pPass == null) {

                    proxyInfo = new ProxyInfo(pType, pHost, pPort, null, null);
                } else {

                    proxyInfo = new ProxyInfo(pType, pHost, pPort, pUser, pPass);

                }
            } else {
                Log.error("No proxy info found but proxy type is enabled!");
            }
        }

        DomainBareJid xmppDomain;
        try {
            xmppDomain = JidCreate.domainBareFrom(loginServer);
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }

        final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(loginUsername, loginPassword)
            .setXmppDomain(xmppDomain)
            .setPort(port)
            .setSendPresence(false)
            .setCompressionEnabled(localPref.isCompressionEnabled())
            .setSecurityMode(securityMode);

        if (securityMode != ConnectionConfiguration.SecurityMode.disabled && localPref.isDisableHostnameVerification()) {
            TLSUtils.disableHostnameVerificationForTlsCertificates(builder);
        }
        if (localPref.isDebuggerEnabled()) {
            builder.enableDefaultDebugger();
        }

        if (hostPortConfigured) {
            builder.setHost(localPref.getXmppHost());
        }

        if (localPref.isProxyEnabled()) {
            builder.setProxyInfo(proxyInfo);
        }

        if (securityMode != ConnectionConfiguration.SecurityMode.disabled && !useOldSSL) {
            // This use STARTTLS which starts initially plain connection to upgrade it to TLS, it use the same port as
            // plain connections which is 5222.
            SparkSSLContextCreator.Options options;
            if (localPref.isAllowClientSideAuthentication()) {
                options = SparkSSLContextCreator.Options.BOTH;
            } else {
                options = SparkSSLContextCreator.Options.ONLY_SERVER_SIDE;
            }
            try {
                SSLContext context = SparkSSLContextCreator.setUpContext(options);
                builder.setCustomSSLContext(context);
                builder.setSecurityMode(securityMode);
            } catch (NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException | KeyStoreException | NoSuchProviderException e) {
                Log.warning("Couldnt establish secured connection", e);
            }
        }

        if (securityMode != ConnectionConfiguration.SecurityMode.disabled && useOldSSL) {
            if (!hostPortConfigured) {
                // SMACK 4.1.9 does not support XEP-0368, and does not apply a port change, if the host is not changed too.
                // Here, we force the host to be set (by doing a DNS lookup), and force the port to 5223 (which is the
                // default 'old-style' SSL port).
                DnsName serverNameDnsName = DnsName.from(loginServer);
                builder.setHost(DNSUtil.resolveXMPPServiceDomain(serverNameDnsName, null, DnssecMode.disabled).get(0).getFQDN());
                builder.setPort(5223);
            }
            SparkSSLContextCreator.Options options;
            if (localPref.isAllowClientSideAuthentication()) {
                options = SparkSSLContextCreator.Options.BOTH;
            } else {
                options = SparkSSLContextCreator.Options.ONLY_SERVER_SIDE;
            }
            builder.setSocketFactory(new SparkSSLSocketFactory(options));
            // SMACK 4.1.9  does not recognize an 'old-style' SSL socket as being secure, which will cause a failure when
            // the 'required' Security Mode is defined. Here, we work around this by replacing that security mode with an
            // 'if-possible' setting.
            builder.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
        }

        if (securityMode != ConnectionConfiguration.SecurityMode.disabled) {
            SASLAuthentication.registerSASLMechanism(new SASLExternalMechanism());
        }

        // SPARK-1747: Don't use the GSS-API SASL mechanism when SSO is disabled.
        SASLAuthentication.unregisterSASLMechanism(SASLGSSAPIMechanism.class.getName());
        SASLAuthentication.unregisterSASLMechanism(SASLGSSAPIv3CompatMechanism.class.getName());

        // Add the mechanism only when SSO is enabled (which allows us to register the correct one).
        if (localPref.isSSOEnabled()) {
            // SPARK-1740: Register a mechanism that's compatible with Smack 3, when requested.
            if (localPref.isSaslGssapiSmack3Compatible()) {
                // SPARK-1747: Don't use the GSSAPI mechanism when SSO is disabled.
                SASLAuthentication.registerSASLMechanism(new SASLGSSAPIv3CompatMechanism());
            } else {
                SASLAuthentication.registerSASLMechanism(new SASLGSSAPIMechanism());
            }
        }

        if (localPref.isLoginAnonymously() && !localPref.isSSOEnabled()) {
            //later login() is called without arguments
            builder.performSaslAnonymousAuthentication();
        }

        // TODO These were used in Smack 3. Find Smack 4 alternative.
//        config.setRosterLoadedAtLogin(true);
//        if(ModelUtil.hasLength(localPref.getTrustStorePath())) {
//        	config.setTruststorePath(localPref.getTrustStorePath());
//        	config.setTruststorePassword(localPref.getTrustStorePassword());
//        }
        return builder.build();
    }

    /**
     * Define Login Panel implementation.
     */
    private final class LoginPanel extends JPanel implements KeyListener, ActionListener, FocusListener, CallbackHandler {
        private static final long serialVersionUID = 2445523786538863459L;
        private final JLabel usernameLabel = new JLabel();
        private final JTextField usernameField = new JTextField();

        private final JLabel passwordLabel = new JLabel();
        private final JPasswordField passwordField = new JPasswordField();

        private final JLabel serverLabel = new JLabel();
        private final JTextField serverField = new JTextField();

        private final JCheckBox savePasswordBox = new JCheckBox();
        private final JCheckBox autoLoginBox = new JCheckBox();
        private final RolloverButton loginButton = new RolloverButton();
        private final RolloverButton advancedButton = new RolloverButton();
        private final RolloverButton quitButton = new RolloverButton();
        private final JCheckBox loginAsInvisibleBox = new JCheckBox();
        private final JCheckBox loginAnonymouslyBox = new JCheckBox();

        private final RolloverButton createAccountButton = new RolloverButton();
        private final RolloverButton passwordResetButton = new RolloverButton();

        private final JLabel progressBar = new JLabel();

        // Panel used to hold buttons
        private final CardLayout cardLayout = new CardLayout(0, 5);
        final JPanel cardPanel = new JPanel(cardLayout);

        final JPanel buttonPanel = new JPanel(new GridBagLayout());
        private final GridBagLayout GRIDBAGLAYOUT = new GridBagLayout();
        private AbstractXMPPConnection connection = null;

        private final JLabel headerLabel = new JLabel();
        private final JLabel accountLabel = new JLabel();
        private final JLabel accountNameLabel = new JLabel();
        private final JLabel serverNameLabel = new JLabel();
        private final JLabel ssoServerLabel = new JLabel();

        private final RolloverButton otherUsers = new RolloverButton(SparkRes.getImageIcon(SparkRes.PANE_UP_ARROW_IMAGE));


        LoginPanel() {
            //setBorder(BorderFactory.createTitledBorder("Sign In Now"));
            ResourceUtils.resButton(savePasswordBox, Res.getString("checkbox.save.password"));
            ResourceUtils.resButton(autoLoginBox, Res.getString("checkbox.auto.login"));
            ResourceUtils.resLabel(serverLabel, serverField, Res.getString("label.server"));
            ResourceUtils.resButton(createAccountButton, Res.getString("label.accounts"));
            ResourceUtils.resButton(passwordResetButton, Res.getString("label.passwordreset"));
            ResourceUtils.resButton(loginAsInvisibleBox, Res.getString("checkbox.login.as.invisible"));
            ResourceUtils.resButton(loginAnonymouslyBox, Res.getString("checkbox.login.anonymously"));

            savePasswordBox.setOpaque(false);
            autoLoginBox.setOpaque(false);
            loginAsInvisibleBox.setOpaque(false);
            loginAnonymouslyBox.setOpaque(false);
            setLayout(GRIDBAGLAYOUT);

            // Set default visibility
            headerLabel.setVisible(false);
            accountLabel.setVisible(false);
            accountNameLabel.setVisible(false);
            serverNameLabel.setVisible(false);

            headerLabel.setText(Res.getString("title.advanced.connection.sso"));
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
            accountLabel.setText("Account:");
            ssoServerLabel.setText("Server:");
            accountNameLabel.setFont(accountLabel.getFont().deriveFont(Font.BOLD));
            serverNameLabel.setFont(ssoServerLabel.getFont().deriveFont(Font.BOLD));

            accountNameLabel.setForeground(new Color(106, 127, 146));
            serverNameLabel.setForeground(new Color(106, 127, 146));

            otherUsers.setFocusable(false);

            add(usernameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
            add(usernameField,
                new GridBagConstraints(1, 0, 2, 1,
                    1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 0, 0), 0, 0));

            add(otherUsers, new GridBagConstraints(3, 0, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));

            add(accountLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
            add(accountNameLabel,
                new GridBagConstraints(1, 1, 1, 1,
                    1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 0, 5), 0, 0));

            add(passwordLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 5, 0));
            add(passwordField,
                new GridBagConstraints(1, 1, 2, 1,
                    1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 0, 0), 0, 0));

            // Add Server Field Properties
            add(serverLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 5, 0));
            add(serverField,
                new GridBagConstraints(1, 2, 2, 1,
                    1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 0, 0), 0, 0));

            add(serverNameLabel,
                new GridBagConstraints(1, 2, 2, 1,
                    1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 0, 5), 0, 0));

            add(headerLabel,
                new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            if (!Default.getBoolean(Default.HIDE_SAVE_PASSWORD_AND_AUTO_LOGIN) && localPref.getPswdAutologin()) {
                add(savePasswordBox,
                    new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
                add(autoLoginBox,
                    new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
            }

            // Add option to hide "Login as invisible" selection on the login screen
            if (!Default.getBoolean(Default.HIDE_LOGIN_AS_INVISIBLE) && localPref.getInvisibleLogin()) {
                add(loginAsInvisibleBox,
                    new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
            }

            // Add option to hide "Login anonymously" selection on the login screen
            if (!Default.getBoolean(Default.HIDE_LOGIN_ANONYMOUSLY) && localPref.getAnonymousLogin()) {
                add(loginAnonymouslyBox,
                    new GridBagConstraints(1, 8, 2, 1, 1.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
            }

            // Add button but disable the login button initially
            savePasswordBox.addActionListener(this);
            autoLoginBox.addActionListener(this);
            loginAsInvisibleBox.addActionListener(this);
            loginAnonymouslyBox.addActionListener(this);

            if (!Default.getBoolean(Default.ACCOUNT_DISABLED) && localPref.getAccountsReg()) {
                buttonPanel.add(createAccountButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
            }

            if (Default.getBoolean(Default.PASSWORD_RESET_ENABLED)) {
                buttonPanel.add(passwordResetButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
                passwordResetButton.addActionListener(new ActionListener() {
                    final String url = Default.getString(Default.PASSWORD_RESET_URL);
                    private static final long serialVersionUID = 2680369963282231348L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            BrowserLauncher.openURL(url);
                        } catch (Exception e) {
                            Log.error("Unable to load password " +
                                "reset.", e);
                        }
                    }
                });
            }

            if (!Default.getBoolean(Default.ADVANCED_DISABLED) && localPref.getAdvancedConfig()) {
                buttonPanel.add(advancedButton,
                    new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
            }

            buttonPanel.add(loginButton,
                new GridBagConstraints(3, 0, 4, 1, 1.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));

            cardPanel.add(buttonPanel, BUTTON_PANEL);

            cardPanel.setOpaque(false);
            buttonPanel.setOpaque(false);

            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("images/ajax-loader.gif"));
            progressBar.setIcon(icon);
            cardPanel.add(progressBar, PROGRESS_BAR);


            add(cardPanel, new GridBagConstraints(0, 9, 4, 1,
                1.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
            loginButton.setEnabled(false);

            // Add KeyListener
            usernameField.addKeyListener(this);
            passwordField.addKeyListener(this);
            serverField.addKeyListener(this);

            passwordField.addFocusListener(this);
            usernameField.addFocusListener(this);
            serverField.addFocusListener(this);

            // Add ActionListener
            quitButton.addActionListener(this);
            loginButton.addActionListener(this);
            advancedButton.addActionListener(this);

            otherUsers.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getPopup().show(otherUsers, e.getX(), e.getY());
                }
            });

            // Make same size
            GraphicUtils.makeSameSize(usernameField, passwordField);

            // Set progress bar description
            progressBar.setText(Res.getString("message.authenticating"));
            progressBar.setVerticalTextPosition(JLabel.BOTTOM);
            progressBar.setHorizontalTextPosition(JLabel.CENTER);
            progressBar.setHorizontalAlignment(JLabel.CENTER);

            // Set Resources
            ResourceUtils.resLabel(usernameLabel, usernameField, Res.getString("label.username"));
            ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password"));
            ResourceUtils.resButton(quitButton, Res.getString("button.quit"));
            ResourceUtils.resButton(loginButton, Res.getString("button.login"));
            ResourceUtils.resButton(advancedButton, Res.getString("button.advanced"));

            // Load previous instances
            String userProp = localPref.getLastUsername();
            String serverProp = localPref.getServer();

            File file = new File(Spark.getSparkUserHome(), "/user/");
            File[] userprofiles = file.listFiles();

            for (File f : userprofiles) {
                if (f.getName().contains("@")) {
                    _usernames.add(f.getName());
                } else {
                    Log.error("Profile contains wrong format: \"" + f.getName()
                        + "\" located at: " + f.getAbsolutePath());
                }
            }

            if (userProp != null) {
                usernameField.setText(XmppStringUtils.unescapeLocalpart(userProp));
            }
            if (serverProp != null) {
                serverField.setText(serverProp);
                serverNameLabel.setText(serverProp);
            }

            // Check Settings
            if (localPref.isSavePassword()) {
                String encryptedPassword = localPref.getPasswordForUser(getBareJid());
                if (encryptedPassword != null) {
                    passwordField.setText(encryptedPassword);
                }
                savePasswordBox.setSelected(true);
                loginButton.setEnabled(true);
            }
            autoLoginBox.setSelected(localPref.isAutoLogin());
            loginAsInvisibleBox.setSelected(localPref.isLoginAsInvisible());
            loginAnonymouslyBox.setSelected(localPref.isLoginAnonymously());
            usernameField.setEnabled(!loginAnonymouslyBox.isSelected());
            passwordField.setEnabled(!loginAnonymouslyBox.isSelected());
            useSSO(localPref.isSSOEnabled());
            if (autoLoginBox.isSelected()) {
                validateLogin();
                return;
            }

            // Handle arguments
            String username = Spark.getArgumentValue("username");
            String password = Spark.getArgumentValue("password");
            String server = Spark.getArgumentValue("server");

            if (username != null) {
                usernameField.setText(username);
            }

            if (password != null) {
                passwordField.setText(password);
            }

            if (server != null) {
                serverField.setText(server);
            }

            if (username != null && server != null && password != null) {
                validateLogin();
            }

            createAccountButton.addActionListener(this);

            final String lockedDownURL = Default.getString(Default.HOST_NAME);
            if (ModelUtil.hasLength(lockedDownURL)) {
                serverField.setText(lockedDownURL);
            }

            setComponentsAvailable(true);
        }

        /**
         * Returns the username the user defined.
         *
         * @return the username.
         */
        private String getUsername() {
            return XmppStringUtils.escapeLocalpart(usernameField.getText().trim());
        }

        /**
         * Returns the resulting bareJID from username and server
         * @return
         */
        private String getBareJid() {
            return usernameField.getText() + "@" + serverField.getText();
        }

        /**
         * Returns the password specified by the user.
         *
         * @return the password.
         */
        private String getPassword() {
            return new String(passwordField.getPassword());
        }

        /**
         * Returns the server name specified by the user.
         *
         * @return the server name.
         */
        private String getServerName() {
            return serverField.getText().trim();
        }

        /**
         * Return whether user wants to login as invisible or not.
         *
         * @return the true if user wants to login as invisible.
         */
        boolean isLoginAsInvisible() {
            return loginAsInvisibleBox.isSelected();
        }

        /**
         * ActionListener implementation.
         *
         * @param e the ActionEvent
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == quitButton) {
                quitLogin();
            } else if (e.getSource() == createAccountButton) {
                AccountCreationWizard createAccountPanel = new AccountCreationWizard();
                createAccountPanel.invoke(loginDialog);

                if (createAccountPanel.isRegistered()) {
                    usernameField.setText(createAccountPanel.getUsernameWithoutEscape());
                    passwordField.setText(createAccountPanel.getPassword());
                    serverField.setText(createAccountPanel.getServer());
                    loginButton.setEnabled(true);
                }
            } else if (e.getSource() == loginButton) {
                validateLogin();

            } else if (e.getSource() == advancedButton) {
                final LoginSettingDialog loginSettingsDialog = new LoginSettingDialog();
                loginSettingsDialog.invoke(loginDialog);
                useSSO(localPref.isSSOEnabled());
            } else if (e.getSource() == savePasswordBox) {
                autoLoginBox.setEnabled(savePasswordBox.isSelected());

                if (!savePasswordBox.isSelected()) {
                    autoLoginBox.setSelected(false);
                }
            } else if (e.getSource() == autoLoginBox) {
                if ((autoLoginBox.isSelected() && (!localPref.isSSOEnabled()))) {
                    savePasswordBox.setSelected(true);
                }
            } else if (e.getSource() == loginAnonymouslyBox) {
                usernameField.setEnabled(!loginAnonymouslyBox.isSelected());
                passwordField.setEnabled(!loginAnonymouslyBox.isSelected());
                validateDialog();
            }
        }

        private JPopupMenu getPopup() {
            JPopupMenu popup = new JPopupMenu();
            for (final String key : _usernames) {

                JMenuItem menu = new JMenuItem(key);

                final String username = key.split("@")[0];
                final String host = key.split("@")[1];
                menu.addActionListener(e -> {
                    usernameField.setText(username);
                    serverField.setText(host);

                    try {
                        passwordField.setText(localPref.getPasswordForUser(getBareJid()));
                        if (passwordField.getPassword().length < 1) {
                            loginButton.setEnabled(loginAnonymouslyBox.isSelected());
                        } else {
                            loginButton.setEnabled(true);
                        }
                    } catch (Exception e1) {
                        Log.error(e1);
                    }

                });

                popup.add(menu);
            }
            return popup;
        }

        /**
         * KeyListener implementation.
         *
         * @param e the KeyEvent to process.
         */
        @Override
        public void keyTyped(KeyEvent e) {
            validate(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT &&
                ((JTextField) e.getSource()).getCaretPosition() == ((JTextField) e.getSource()).getText().length()) {
                getPopup().show(otherUsers, 0, 0);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            validateDialog();
        }

        /**
         * Checks the users input and enables/disables the login button depending on state.
         */
        private void validateDialog() {
            loginButton.setEnabled(loginAnonymouslyBox.isSelected() ||
                ModelUtil.hasLength(getUsername()) &&
                    (ModelUtil.hasLength(getPassword()) || localPref.isSSOEnabled()) &&
                    ModelUtil.hasLength(getServerName()));
        }

        /**
         * Validates key input.
         *
         * @param e the keyEvent.
         */
        private void validate(KeyEvent e) {
            if (loginButton.isEnabled() && e.getKeyChar() == KeyEvent.VK_ENTER) {
                validateLogin();
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            Object o = e.getSource();
            if (o instanceof JTextComponent) {
                ((JTextComponent) o).selectAll();
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
        }

        /**
         * Enables/Disables the editable components in the login screen.
         *
         * @param available true to enable components, otherwise false to disable.
         */
        private void setComponentsAvailable(boolean available) {
            savePasswordBox.setEnabled(available);
            autoLoginBox.setEnabled(available);
            loginAsInvisibleBox.setEnabled(available);
            loginAnonymouslyBox.setEnabled(available);

            // Need to set both editable and enabled for best behavior.
            usernameField.setEditable(available);
            usernameField.setEnabled(available && !loginAnonymouslyBox.isSelected());

            passwordField.setEditable(available);
            passwordField.setEnabled(available && !loginAnonymouslyBox.isSelected());

            if (Default.getBoolean(Default.HOST_NAME_CHANGE_DISABLED) || !localPref.getHostNameChange()) {
                serverField.setEditable(false);
                serverField.setEnabled(false);
            } else {
                serverField.setEditable(available);
                serverField.setEnabled(available);
            }

            if (available) {
                // Reapply focus to password field
                passwordField.requestFocus();
            }
        }

        /**
         * Displays the progress bar.
         *
         * @param visible true to display progress bar, false to hide it.
         */
        private void setProgressBarVisible(boolean visible) {
            if (visible) {
                cardLayout.show(cardPanel, PROGRESS_BAR);
                // progressBar.setIndeterminate(true);
            } else {
                cardLayout.show(cardPanel, BUTTON_PANEL);
            }
        }

        /**
         * Validates the users login information.
         */
        private void validateLogin() {
            final SwingWorker loginValidationThread = new SwingWorker() {
                @Override
                public Object construct() {
                    setLoginUsername(getUsername());
                    setLoginPassword(getPassword());
                    setLoginServer(getServerName());
                    boolean loginSuccessfull = beforeLoginValidations() && login();
                    if (loginSuccessfull) {
                        afterLogin();
                        progressBar.setText(Res.getString("message.connecting.please.wait"));

                        // Startup Spark
                        startSpark();

                        // dispose login dialog
                        loginDialog.dispose();

                        // Show ChangeLog if we need to.
                        // new ChangeLogDialog().showDialog();
                    } else {
                        EventQueue.invokeLater(() -> {
                            setComponentsAvailable(true);
                            setProgressBarVisible(false);
                        });

                    }
                    return loginSuccessfull;
                }
            };

            // Start the login process in separate thread.
            // Disable text fields
            setComponentsAvailable(false);

            // Show progressbar
            setProgressBarVisible(true);

            loginValidationThread.start();
        }

        public JPasswordField getPasswordField() {
            return passwordField;
        }

        @Override
        public Dimension getPreferredSize() {
            final Dimension dim = super.getPreferredSize();
            dim.height = 230;
            return dim;
        }

        public void useSSO(boolean use) {
            if (use) {
                usernameLabel.setVisible(true);
                usernameField.setVisible(true);

                passwordLabel.setVisible(false);
                passwordField.setVisible(false);

                savePasswordBox.setVisible(false);
                savePasswordBox.setSelected(false);

                accountLabel.setVisible(true);
                accountNameLabel.setVisible(true);

                serverField.setVisible(true);

                autoLoginBox.setVisible(true);
                serverLabel.setVisible(true);
                loginAsInvisibleBox.setVisible(true);
                loginAnonymouslyBox.setVisible(false);

                headerLabel.setVisible(true);

                if (localPref.getDebug()) {
                    System.setProperty("java.security.krb5.debug", "true");
                    System.setProperty("sun.security.krb5.debug", "true");
                } else {
                    System.setProperty("java.security.krb5.debug", "false");
                    System.setProperty("sun.security.krb5.debug", "false");
                }

                String ssoMethod = localPref.getSSOMethod();
                if (!ModelUtil.hasLength(ssoMethod)) {
                    ssoMethod = "file";
                }

                System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
                GSSAPIConfiguration config = new GSSAPIConfiguration(ssoMethod.equals("file"));
                Configuration.setConfiguration(config);

                LoginContext lc;
                String princName = localPref.getLastUsername();
                String princRealm = null;
                try {
                    lc = new LoginContext("com.sun.security.jgss.krb5.initiate");
                    lc.login();
                    Subject mySubject = lc.getSubject();


                    for (Principal p : mySubject.getPrincipals()) {
                        //TODO: check if principal is a kerberos principal first...
                        String name = p.getName();
                        int indexOne = name.indexOf("@");
                        if (indexOne != -1) {
                            princName = name.substring(0, indexOne);
                            accountNameLabel.setText(name);
                            princRealm = name.substring(indexOne + 1);
                        }
                        loginButton.setEnabled(true);
                    }
                } catch (LoginException le) {
                    Log.debug(le.getMessage());
                    accountNameLabel.setText(Res.getString("title.login.no.account"));
                    //useSSO(false);
                }

                String ssoKdc;
                if (ssoMethod.equals("dns")) {
                    if (princRealm != null) { //princRealm is null if we got a LoginException above.
                        ssoKdc = getDnsKdc(princRealm);
                        System.setProperty("java.security.krb5.realm", princRealm);
                        System.setProperty("java.security.krb5.kdc", ssoKdc);
                    }
                } else if (ssoMethod.equals("manual")) {
                    princRealm = localPref.getSSORealm();
                    ssoKdc = localPref.getSSOKDC();
                    System.setProperty("java.security.krb5.realm", princRealm);
                    System.setProperty("java.security.krb5.kdc", ssoKdc);
                } else {
                    //Assume "file" method.  We don't have to do anything special,
                    //java takes care of it for us. Unset the props if they are set
                    System.clearProperty("java.security.krb5.realm");
                    System.clearProperty("java.security.krb5.kdc");
                }

                String userName = localPref.getLastUsername();
                if (ModelUtil.hasLength(userName)) {
                    usernameField.setText(userName);
                } else {
                    usernameField.setText(princName);
                }
            } else {
                autoLoginBox.setVisible(true);
                usernameField.setVisible(true);
                passwordField.setVisible(true);
                savePasswordBox.setVisible(true);
                usernameLabel.setVisible(true);
                passwordLabel.setVisible(true);
                serverLabel.setVisible(true);
                serverField.setVisible(true);
                loginAsInvisibleBox.setVisible(true);
                loginAnonymouslyBox.setVisible(true);

                headerLabel.setVisible(false);
                accountLabel.setVisible(false);
                serverNameLabel.setVisible(false);
                accountNameLabel.setVisible(false);

                Configuration.setConfiguration(null);

                validateDialog();
            }
        }

        /**
         * Login to the specified server using username, password, and workgroup.
         * Handles error representation as well as logging.
         *
         * @return true if login was successful, false otherwise
         */
        private boolean login() {
            localPref = SettingsManager.getLocalPreferences();
            localPref.setLoginAsInvisible(loginAsInvisibleBox.isSelected());
            localPref.setLoginAnonymously(loginAnonymouslyBox.isSelected());

            if (localPref.isDebuggerEnabled()) {
                SmackConfiguration.DEBUG = true;
            }

            SmackConfiguration.setDefaultReplyTimeout(localPref.getTimeOut() * 1000);

            try {
                // TODO: SPARK-2140 - add support to Spark for stream management. Challenges expected around reconnection logic!
                XMPPTCPConnection.setUseStreamManagementDefault(false);

                connection = new XMPPTCPConnection(retrieveConnectionConfiguration());
                connection.setParsingExceptionCallback(new ExceptionLoggingCallback());

                // If we want to launch the Smack debugger, we have to check if we are on the dispatch thread, because Smack will create an UI.
                if (localPref.isDebuggerEnabled() && !EventQueue.isDispatchThread()) {
                    // Exception handling should be no different from the regular flow.
                    final Exception[] exception = new Exception[1];
                    EventQueue.invokeAndWait(() -> {
                        try {
                            connection.connect();
                        } catch (IOException | SmackException | XMPPException | InterruptedException e) {
                            exception[0] = e;
                        }
                    });
                    if (exception[0] != null) {
                        throw exception[0];
                    }
                } else {
                    connection.connect();
                }

                if (localPref.isLoginAnonymously() && !localPref.isSSOEnabled()) {
                    // ConnectionConfiguration.performSaslAnonymousAuthentication() used earlier in connection configuration builder,
                    // so now we can just login()
                    connection.login();
                } else {
                    String resource = localPref.getResource();
                    if (Default.getBoolean(Default.HOSTNAME_AS_RESOURCE) || localPref.isUseHostnameAsResource()) {
                        try {
                            resource = InetAddress.getLocalHost().getHostName();
                        } catch (UnknownHostException e) {
                            Log.warning("Cannot set hostname as resource - unable to retrieve hostname.", e);
                        }
                    } else if (Default.getBoolean(Default.VERSION_AS_RESOURCE) || localPref.isUseVersionAsResource()) {
                        resource = JiveInfo.getName() + " " + JiveInfo.getVersion();
                    }

                    Resourcepart resourcepart = Resourcepart.from(modifyWildcards(resource).trim());
                    connection.login(getLoginUsername(), getLoginPassword(), resourcepart);
                }

                final SessionManager sessionManager = SparkManager.getSessionManager();
                sessionManager.setServerAddress(connection.getXMPPServiceDomain());
                sessionManager.initializeSession(connection, getLoginUsername(), getLoginPassword());
                sessionManager.setJID(connection.getUser());

                final ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                reconnectionManager.setFixedDelay(localPref.getReconnectDelay());
                reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
                reconnectionManager.enableAutomaticReconnection();

                final CarbonManager carbonManager = CarbonManager.getInstanceFor(connection);
                if (carbonManager.isSupportedByServer()) {
                    carbonManager.enableCarbons();
                }
            } catch (Exception xee) {
                Log.error("Exception in Login:", xee);

                final String errorMessage;
                if (localPref.isSSOEnabled()) {
                    errorMessage = Res.getString("title.advanced.connection.sso.unable");
                } else if (xee.getMessage() != null && xee.getMessage().contains("not-authorized")) {
                    errorMessage = Res.getString("message.invalid.username.password");
                } else if (xee.getMessage() != null && (xee.getMessage().contains("java.net.UnknownHostException:") || xee.getMessage().contains("Network is unreachable") || xee.getMessage().contains("java.net.ConnectException: Connection refused:"))) {
                    errorMessage = Res.getString("message.server.unavailable");
                } else if (xee.getMessage() != null && xee.getMessage().contains("Hostname verification of certificate failed")) {
                    errorMessage = Res.getString("message.cert.hostname.verification.failed");
                } else if (xee.getMessage() != null && xee.getMessage().contains("unable to find valid certification path to requested target")) {
                    errorMessage = Res.getString("message.cert.verification.failed");
                } else if (xee.getMessage() != null && xee.getMessage().contains("StanzaError: conflict")) {
                    errorMessage = Res.getString("label.conflict.error");
                } else if (xee instanceof SmackException) {
                    errorMessage = xee.getLocalizedMessage();
                } else {
                    errorMessage = Res.getString("message.unrecoverable.error");
                }

                EventQueue.invokeLater(() -> {
                    progressBar.setVisible(false);

                    // Show error dialog
                    UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
                    if (!loginDialog.isVisible()) {
                        loginDialog.setVisible(true);
                    }
                    if (loginDialog.isVisible()) {
                        if (xee.getMessage() != null && xee.getMessage().contains("Self Signed certificate")) {
                            // Handle specific case: if server certificate is self-signed, but self-signed certs are not allowed, show a popup allowing the user to override.
                            // Prompt user if they'd like to add the failed chain to the trust store.
                            final Object[] options = {
                                Res.getString("yes"),
                                Res.getString("no")
                            };

                            final int userChoice = JOptionPane.showOptionDialog(this,
                                Res.getString("dialog.certificate.ask.allow.self-signed"),
                                Res.getString("title.certificate"),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[1]);

                            if (userChoice == JOptionPane.YES_OPTION) {
                                // Toggle the preference.
                                localPref.setAcceptSelfSigned(true);
                                SettingsManager.saveSettings();

                                // Attempt to login again.
                                validateLogin();
                            }
                        } else {
                            final X509Certificate[] lastFailedChain = SparkTrustManager.getLastFailedChain();
                            final SparkTrustManager sparkTrustManager = (SparkTrustManager) SparkTrustManager.getTrustManagerList()[0];
                            // Handle specific case: if path validation failed because of an unrecognized CA, show popup allowing the user to add the certificate.
                            if (lastFailedChain != null && ((xee.getMessage() != null && xee.getMessage().contains("Certificate not in the TrustStore")) || !sparkTrustManager.containsTrustAnchorFor(lastFailedChain))) {
                                // Prompt user if they'd like to add the failed chain to the trust store.
                                final CertificateModel certModel = new CertificateModel(lastFailedChain[0]);
                                final Object[] options = {
                                    Res.getString("yes"),
                                    Res.getString("no")
                                };

                                final int userChoice = JOptionPane.showOptionDialog(this,
                                    new UnrecognizedServerCertificatePanel(certModel),
                                    Res.getString("title.certificate"),
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1]);

                                if (userChoice == JOptionPane.YES_OPTION) {
                                    // Add the certificate chain to the truststore.
                                    sparkTrustManager.addChain(lastFailedChain);

                                    // Attempt to login again.
                                    validateLogin();
                                }
                            } else {
                                // For anything else, show a generic error dialog.
                                MessageDialog.showErrorDialog(loginDialog, errorMessage, xee);
                            }
                        }
                    }
                });

                setEnabled(true);
                return false;
            }

            // Since the connection and workgroup are valid. Add a ConnectionListener
            connection.addConnectionListener(SparkManager.getSessionManager());

            // Initialize chat state notification mechanism in smack
            ChatStateManager.getInstance(SparkManager.getConnection());

            // Persist information
            localPref.setLastUsername(getLoginUsername());

            // Check to see if the password should be saved or cleared from file.
            if (savePasswordBox.isSelected()) {
                try {
                    localPref.setPasswordForUser(getBareJid(), getPassword());
                } catch (Exception e) {
                    Log.error("Error encrypting password.", e);
                }
            } else {
                try {
                    localPref.clearPasswordForAllUsers();
                } catch (Exception e) {
                    Log.debug("Unable to clear saved password..." + e);
                }
            }

            localPref.setSavePassword(savePasswordBox.isSelected());
            localPref.setAutoLogin(autoLoginBox.isSelected());

            localPref.setServer(serverField.getText());
            SettingsManager.saveSettings();

            return true;
        }

        @Override
        public void handle(Callback[] callbacks) {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    NameCallback ncb = (NameCallback) callback;
                    ncb.setName(getLoginUsername());
                } else if (callback instanceof PasswordCallback) {
                    PasswordCallback pcb = (PasswordCallback) callback;
                    pcb.setPassword(getPassword().toCharArray());
                } else {
                    Log.error("Unknown callback requested: " + callback.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * If the user quits, just shut down the
     * application.
     */
    private void quitLogin() {
        System.exit(1);
    }

    /**
     * Initializes Spark and initializes all plugins.
     */
    private void startSpark() {
        // Invoke the MainWindow.
        try {
            EventQueue.invokeLater(() -> {
                final MainWindow mainWindow = MainWindow.getInstance();

             /*
             if (tray != null) {
                 // Remove trayIcon
                 tray.removeTrayIcon(trayIcon);
             }
             */
                // Creates the Spark  Workspace and add to MainWindow
                Workspace workspace = Workspace.getInstance();

                LayoutSettings settings = LayoutSettingsManager.getLayoutSettings();

                LocalPreferences pref = SettingsManager.getLocalPreferences();
                if (pref.isDockingEnabled()) {
                    JSplitPane splitPane = mainWindow.getSplitPane();
                    workspace.getCardPanel().setMinimumSize(null);
                    splitPane.setLeftComponent(workspace.getCardPanel());
                    SparkManager.getChatManager().getChatContainer().setMinimumSize(null);
                    splitPane.setRightComponent(SparkManager.getChatManager().getChatContainer());
                    int dividerLoc = settings.getSplitPaneDividerLocation();
                    if (dividerLoc != -1) {
                        mainWindow.getSplitPane().setDividerLocation(dividerLoc);
                    } else {
                        mainWindow.getSplitPane().setDividerLocation(240);
                    }

                    mainWindow.getContentPane().add(splitPane, BorderLayout.CENTER);
                } else {
                    mainWindow.getContentPane().add(workspace.getCardPanel(), BorderLayout.CENTER);
                }

                final Rectangle mainWindowBounds = settings.getMainWindowBounds();
                if (mainWindowBounds == null || mainWindowBounds.width <= 0 || mainWindowBounds.height <= 0) {
                    // Use Default size
                    mainWindow.setSize(310, 520);

                    // Center Window on Screen
                    GraphicUtils.centerWindowOnScreen(mainWindow);
                } else {
                    mainWindow.setBounds(mainWindowBounds);
                }

                if (loginDialog.isVisible()) {
                    mainWindow.setVisible(true);
                }

                loginDialog.setVisible(false);

                // Build the layout in the workspace
                workspace.buildLayout();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Updates System properties with Proxy configuration.
     *
     */
    private void updateProxyConfig() {
        if (ModelUtil.hasLength(Default.getString(Default.PROXY_PORT)) && ModelUtil.hasLength(Default.getString(Default.PROXY_HOST))) {
            String port = Default.getString(Default.PROXY_PORT);
            String host = Default.getString(Default.PROXY_HOST);
            System.setProperty("socksProxyHost", host);
            System.setProperty("socksProxyPort", port);
            return;
        }

        boolean proxyEnabled = localPref.isProxyEnabled();
        if (proxyEnabled) {
            String host = localPref.getHost();
            String port = localPref.getPort();
            String username = localPref.getProxyUsername();
            String password = localPref.getProxyPassword();
            String protocol = localPref.getProtocol();

            if (protocol.equals("SOCKS")) {
                System.setProperty("socksProxyHost", host);
                System.setProperty("socksProxyPort", port);

                if (ModelUtil.hasLength(username) && ModelUtil.hasLength(password)) {
                    System.setProperty("java.net.socks.username", username);
                    System.setProperty("java.net.socks.password", password);
                }
            } else {
                System.setProperty("http.proxyHost", host);
                System.setProperty("http.proxyPort", port);
                System.setProperty("https.proxyHost", host);
                System.setProperty("https.proxyPort", port);

                if (ModelUtil.hasLength(username) && ModelUtil.hasLength(password)) {
                    System.setProperty("http.proxyUser", username);
                    System.setProperty("http.proxyPassword", password);
                }

            }
        }
    }

    /**
     * Defines the background to use with the Login panel.
     */
    public static class LoginBackgroundPanel extends JPanel {
        private static final long serialVersionUID = -2449309600851007447L;
        final ImageIcon icons = Default.getImageIcon(Default.LOGIN_DIALOG_BACKGROUND_IMAGE);

        /**
         * Empty constructor.
         */
        public LoginBackgroundPanel() {
        }

        /**
         * Uses an image to paint on background.
         *
         * @param g the graphics.
         */
        @Override
        public void paintComponent(Graphics g) {
            Image backgroundImage = icons.getImage();
            double scaleX = getWidth() / (double) backgroundImage.getWidth(null);
            double scaleY = getHeight() / (double) backgroundImage.getHeight(null);
            AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
            ((Graphics2D) g).drawImage(backgroundImage, xform, this);
        }
    }

    /**
     * The image panel to display the Spark Logo.
     */
    public static class ImagePanel extends JPanel {
        private static final long serialVersionUID = -1778389077647562606L;
        private final ImageIcon icons = Default.getImageIcon(Default.MAIN_IMAGE);

        /**
         * Uses the Spark logo to paint as the background.
         *
         * @param g the graphics to use.
         */
        @Override
        public void paintComponent(Graphics g) {
            final Image backgroundImage = icons.getImage();
            final double scaleX = getWidth() / (double) backgroundImage.getWidth(null);
            final double scaleY = getHeight() / (double) backgroundImage.getHeight(null);
            AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
            ((Graphics2D) g).drawImage(backgroundImage, xform, this);
        }

        @Override
        public Dimension getPreferredSize() {
            final Dimension size = super.getPreferredSize();
            size.width = icons.getIconWidth();
            size.height = icons.getIconHeight();
            return size;
        }
    }

    /**
     * Checks for historic Spark settings and upgrades the user.
     *
     * @throws Exception thrown if an error occurs.
     */
    private void checkForOldSettings() throws Exception {
        // Check for old settings.xml
        File settingsXML = new File(Spark.getSparkUserHome(), "/settings.xml");
        if (settingsXML.exists()) {
            SAXReader saxReader = new SAXReader();
            Document pluginXML;
            try {
                // SPARK-2147: Disable certain features for security purposes (CVE-2020-10683)
                saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
                saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

                pluginXML = saxReader.read(settingsXML);
            } catch (DocumentException e) {
                Log.error(e);
                return;
            }

            List<?> plugins = pluginXML.selectNodes("/settings");
            for (Object plugin1 : plugins) {
                Element plugin = (Element) plugin1;

                String username = plugin.selectSingleNode("username").getText();
                localPref.setLastUsername(username);

                String server = plugin.selectSingleNode("server").getText();
                localPref.setServer(server);

                String autoLogin = plugin.selectSingleNode("autoLogin").getText();
                localPref.setAutoLogin(Boolean.parseBoolean(autoLogin));

                String savePassword = plugin.selectSingleNode("savePassword").getText();
                localPref.setSavePassword(Boolean.parseBoolean(savePassword));

                String password = plugin.selectSingleNode("password").getText();
                localPref.setPasswordForUser(username + "@" + server, password);

                SettingsManager.saveSettings();
            }

            // Delete settings File
            settingsXML.delete();
        }
    }

    /**
     * Use DNS to lookup a KDC
     * @param realm The realm to look up
     * @return the KDC hostname
     */
    private String getDnsKdc(String realm) {
        //Assumption: the KDC will be found with the SRV record
        // _kerberos._udp.$realm
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext context = new InitialDirContext(env);
            Attributes dnsLookup = context.getAttributes("_kerberos._udp." + realm, new String[]{"SRV"});

            ArrayList<Integer> priorities = new ArrayList<>();
            HashMap<Integer, List<String>> records = new HashMap<>();
            for (Enumeration<?> e = dnsLookup.getAll(); e.hasMoreElements(); ) {
                Attribute record = (Attribute) e.nextElement();
                for (Enumeration<?> e2 = record.getAll(); e2.hasMoreElements(); ) {
                    String sRecord = (String) e2.nextElement();
                    String[] sRecParts = sRecord.split(" ");
                    Integer pri = Integer.valueOf(sRecParts[0]);
                    if (priorities.contains(pri)) {
                        List<String> recs = records.get(pri);
                        if (recs == null) recs = new ArrayList<>();
                        recs.add(sRecord);
                    } else {
                        priorities.add(pri);
                        List<String> recs = new ArrayList<>();
                        recs.add(sRecord);
                        records.put(pri, recs);
                    }
                }
            }
            Collections.sort(priorities);
            List<String> l = records.get(priorities.get(0));
            String toprec = l.get(0);
            String[] sRecParts = toprec.split(" ");
            return sRecParts[3];
        } catch (NamingException e) {
            return "";
        }
    }

    protected String getLoginUsername() {
        return loginUsername;
    }

    protected void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    protected String getLoginPassword() {
        return loginPassword;
    }

    protected void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    protected String getLoginServer() {
        return loginServer;
    }

    protected void setLoginServer(String loginServer) {
        this.loginServer = loginServer;
    }

    protected ArrayList<String> getUsernames() {
        return _usernames;
    }

    private void persistEnterprise() {
        new Enterprise();
        localPref.setAccountsReg(Enterprise.containsFeature(Enterprise.ACCOUNTS_REG_FEATURE));
        localPref.setAdvancedConfig(Enterprise.containsFeature(Enterprise.ADVANCED_CONFIG_FEATURE));
        localPref.setHostNameChange(Enterprise.containsFeature(Enterprise.HOST_NAME_FEATURE));
        localPref.setInvisibleLogin(Enterprise.containsFeature(Enterprise.INVISIBLE_LOGIN_FEATURE));
        localPref.setAnonymousLogin(Enterprise.containsFeature(Enterprise.ANONYMOUS_LOGIN_FEATURE));
        localPref.setPswdAutologin(Enterprise.containsFeature(Enterprise.SAVE_PASSWORD_FEATURE));
        localPref.setUseHostnameAsResource(Enterprise.containsFeature(Enterprise.HOSTNAME_AS_RESOURCE_FEATURE));
        localPref.setUseVersionAsResource(Enterprise.containsFeature(Enterprise.VERSION_AS_RESOURCE_FEATURE));
    }

    private void initAdvancedDefaults() {
        localPref.setCompressionEnabled(localPref.isCompressionEnabled());
        localPref.setDebuggerEnabled(localPref.isDebuggerEnabled());
        localPref.setDisableHostnameVerification(localPref.isDisableHostnameVerification());
        localPref.setHostAndPortConfigured(localPref.isHostAndPortConfigured());
        localPref.setProtocol("SOCKS");
        localPref.setProxyEnabled(localPref.isProxyEnabled());
        //  localPref.setProxyPassword("");
        //  localPref.setProxyUsername("");
        localPref.setResource("Spark");
        localPref.setSaslGssapiSmack3Compatible(localPref.isSaslGssapiSmack3Compatible());
        localPref.setSSL(localPref.isSSL());
        localPref.setSecurityMode(localPref.getSecurityMode());
        localPref.setSSOEnabled(localPref.isSSOEnabled());
        localPref.setSSOMethod("file");
        localPref.setTimeOut(localPref.getTimeOut());
        //  localPref.setTrustStorePassword("");
        //  localPref.setTrustStorePath("");
        localPref.setUseHostnameAsResource(localPref.isUseHostnameAsResource());
        localPref.setUseVersionAsResource(localPref.isUseVersionAsResource());
        //  localPref.setXmppHost("");
        localPref.setXmppPort(localPref.getXmppPort());

        SettingsManager.saveSettings();
    }
}
