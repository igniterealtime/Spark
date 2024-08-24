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
package org.jivesoftware.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.AccountCreationWizard;
import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
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
import org.jivesoftware.smackx.iqregister.AccountManager;
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

import static org.jivesoftware.spark.util.StringUtils.modifyWildcards;
import static org.jivesoftware.sparkimpl.certificates.SparkSSLContextCreator.Options.BOTH;
import static org.jivesoftware.sparkimpl.certificates.SparkSSLContextCreator.Options.ONLY_SERVER_SIDE;

import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.CertificateModel;
import org.jivesoftware.sparkimpl.certificates.SparkSSLContextCreator;
import org.jivesoftware.sparkimpl.certificates.SparkSSLSocketFactory;
import org.jivesoftware.sparkimpl.certificates.SparkTrustManager;
import org.jivesoftware.sparkimpl.certificates.UnrecognizedServerCertificatePanel;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettings;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;
import org.minidns.dnsname.DnsName;
import org.minidns.record.A;

/**
 * Dialog to log in a user into the XMPP server.
 * The LoginDialog is used only for login in registered users into the XMPP server.
 * @author KeepToo
 */
public class LoginUIPanel extends javax.swing.JPanel implements KeyListener, ActionListener, FocusListener, CallbackHandler {

    private JFrame loginDialog;
    private static final String BUTTON_PANEL = "buttonpanel"; // NOTRANS
    private static final String PROGRESS_BAR = "progressbar"; // NOTRANS
    private LocalPreferences localPref;
    private ArrayList<String> _usernames = new ArrayList<>();
    private String loginUsername;
    private String loginPassword;
    private String loginServer;
    private static final long serialVersionUID = 2445523786538863459L;

    // Panel used to hold buttons
    private final CardLayout cardLayout = new CardLayout(0, 5);
    final JPanel cardPanel = new JPanel(cardLayout);

    final JPanel buttonPanel = new JPanel(new GridBagLayout());
    private AbstractXMPPConnection connection = null;

    private RolloverButton otherUsers = new RolloverButton(SparkRes.getImageIcon(SparkRes.PANE_DOWN_ARROW_IMAGE));

    /**
     * Creates new form LoginWindow
     */
    public LoginUIPanel() {
        initComponents();

        localPref = SettingsManager.getLocalPreferences();

        //SPARK-2115 SPARK-2295 Allow account creation or password changes over an insecure (e.g. unencrypted) connections.
        AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);

        init();
        // Check if upgraded needed.
        try {
            checkForOldSettings();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    // Should be called only from the Event Dispatcher Thread.
    private void init() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }

        ResourceUtils.resButton(cbSavePassword, Res.getString("checkbox.save.password"));
        ResourceUtils.resButton(cbAutoLogin, Res.getString("checkbox.auto.login"));
        ResourceUtils.resButton(btnSignUp, Res.getString("label.accounts"));
        ResourceUtils.resButton(cbLoginInvisible, Res.getString("checkbox.login.as.invisible"));
        ResourceUtils.resButton(cbAnonymous, Res.getString("checkbox.login.anonymously"));
        ResourceUtils.resButton(btnReset, Res.getString("label.passwordreset"));
        configureVisibility();

        lblProgress.setVisible(false);
        cbSavePassword.setOpaque(false);
        cbAutoLogin.setOpaque(false);
        cbLoginInvisible.setOpaque(false);
        cbAnonymous.setOpaque(false);
        // btnReset.setVisible(false);

        // Add button but disable the login button initially
        cbSavePassword.addActionListener(this);
        cbAutoLogin.addActionListener(this);
        cbLoginInvisible.addActionListener(this);
        cbAnonymous.addActionListener(this);

        // Add KeyListener
        tfUsername.addKeyListener(this);
        tfPassword.addKeyListener(this);
        tfDomain.addKeyListener(this);

        tfPassword.addFocusListener(this);
        tfUsername.addFocusListener(this);
        tfDomain.addFocusListener(this);

        // Add ActionListener
        btnLogin.addActionListener(this);
        btnAdvanced.addActionListener(this);

        otherUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getPopup().show(otherUsers, e.getX(), e.getY());
            }
        });

        // Make same size
        GraphicUtils.makeSameSize(tfUsername, tfPassword);

        // Set progress bar description
        lblProgress.setText(Res.getString("message.authenticating"));
        lblProgress.setVerticalTextPosition(JLabel.BOTTOM);
        lblProgress.setHorizontalTextPosition(JLabel.CENTER);
        lblProgress.setHorizontalAlignment(JLabel.CENTER);

        // Set Resources
        // ResourceUtils.resLabel(usernameLabel, tfUsername, Res.getString("label.username"));
        //ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password"));
        ResourceUtils.resButton(btnLogin, Res.getString("button.login"));
        ResourceUtils.resButton(btnAdvanced, Res.getString("button.advanced"));

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
            tfUsername.setText(XmppStringUtils.unescapeLocalpart(userProp));
        }
        if (serverProp != null) {
            tfDomain.setText(serverProp);
        }

        // Check Settings
        if (localPref.isSavePassword()) {
            String encryptedPassword = localPref.getPasswordForUser(getBareJid());
            if (encryptedPassword != null) {
                tfPassword.setText(encryptedPassword);
            }
            cbSavePassword.setSelected(true);
            btnLogin.setEnabled(true);
        }
        cbAutoLogin.setSelected(localPref.isAutoLogin());
        cbLoginInvisible.setSelected(localPref.isLoginAsInvisible());
        cbAnonymous.setSelected(localPref.isLoginAnonymously());
        tfUsername.setEnabled(!cbAnonymous.isSelected());
        tfPassword.setEnabled(!cbAnonymous.isSelected());
        //Add clear button for username,password and domain field
        tfUsername.putClientProperty("JTextField.showClearButton",true);
        tfDomain.putClientProperty("JTextField.showClearButton",true);
        tfPassword.putClientProperty("JTextField.showClearButton",true);
        useSSO(localPref.isSSOEnabled());
        if (cbAutoLogin.isSelected()) {
            TaskEngine.getInstance().submit(this::login);
            return;
        }

        // Handle arguments
        String username = Spark.getArgumentValue("username");
        String password = Spark.getArgumentValue("password");
        String server = Spark.getArgumentValue("server");

        if (username != null) {
            tfUsername.setText(username);
        }

        if (password != null) {
            tfPassword.setText(password);
        }

        if (server != null) {
            tfDomain.setText(server);
        }

        if (username != null && server != null && password != null) {
            TaskEngine.getInstance().submit(this::login);
        }

        btnSignUp.addActionListener(this);

        final String lockedDownURL = Default.getString(Default.HOST_NAME);
        if (ModelUtil.hasLength(lockedDownURL)) {
            tfDomain.setText(lockedDownURL);
        }

        //reset ui
        //btnAdvanced.setUI(new BasicButtonUI());
        //btnSignUp.setUI(new BasicButtonUI());
        tfDomain.putClientProperty("JTextField.placeholderText", Res.getString("hint.login.domain"));
        tfPassword.putClientProperty("JTextField.placeholderText", Res.getString("hint.login.password"));
        tfUsername.putClientProperty("JTextField.placeholderText", Res.getString("hint.login.username"));

        //If users account > 1 then show account list button "otherUsers"
        if(_usernames.size() > 1){
            tfUsername.putClientProperty("JTextField.trailingComponent",otherUsers);
        }
        
        setComponentsAvailable(true);

    }

    // Should be called only from the Event Dispatcher Thread.
    private void configureVisibility() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }

        int height = filler3.getPreferredSize().height;
        if (Default.getBoolean(Default.HIDE_SAVE_PASSWORD_AND_AUTO_LOGIN) || !localPref.getPswdAutologin()) {

            pnlCheckboxes.remove(cbAutoLogin);
            pnlCheckboxes.remove(cbSavePassword);
            height = height + 20;
        }
        // Add option to hide "Login as invisible" selection on the login screen
        if (Default.getBoolean(Default.HIDE_LOGIN_AS_INVISIBLE) || !localPref.getInvisibleLogin()) {
            pnlCheckboxes.remove(cbLoginInvisible);
            height = height + 10;
        }

        // Add option to hide "Login anonymously" selection on the login screen
        if (Default.getBoolean(Default.HIDE_LOGIN_ANONYMOUSLY) || !localPref.getAnonymousLogin()) {
            pnlCheckboxes.remove(cbAnonymous);
            height = height + 10;
        }

        if (Default.getBoolean(Default.ACCOUNT_DISABLED) || !localPref.getAccountsReg()) {
            pnlBtns.remove(btnSignUp);
            height = height + 15;
        }

        if (!Default.getBoolean(Default.PASSWORD_RESET_ENABLED)) {
            pnlBtns.remove(btnReset);
        }

        if (Default.getBoolean(Default.ADVANCED_DISABLED) || !localPref.getAdvancedConfig()) {
            pnlBtns.remove(btnAdvanced);
            height = height + 15;
        }
        filler3.setPreferredSize(new Dimension(220, height));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLeft = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 50), new java.awt.Dimension(250, 50), new java.awt.Dimension(32767, 50));
        lblLogo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pnlCenter = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(220, 20), new java.awt.Dimension(0, 32767));
        pnlInputs = new javax.swing.JPanel();
        tfUsername = new javax.swing.JTextField();
        tfDomain = new javax.swing.JTextField();
        tfPassword = new javax.swing.JPasswordField();
        pnlCheckboxes = new javax.swing.JPanel();
        cbSavePassword = new javax.swing.JCheckBox();
        cbAutoLogin = new javax.swing.JCheckBox();
        cbLoginInvisible = new javax.swing.JCheckBox();
        cbAnonymous = new javax.swing.JCheckBox();
        pnlBtns = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        btnSignUp = new javax.swing.JButton();
        btnAdvanced = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        pnlLeft.setPreferredSize(new java.awt.Dimension(260, 0));
        pnlLeft.add(filler1);

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spark-64x64.png"))); // NOI18N
        lblLogo.setPreferredSize(new java.awt.Dimension(250, 80));
        lblLogo.setRequestFocusEnabled(false);
        pnlLeft.add(lblLogo);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Spark");
        jLabel1.setPreferredSize(new java.awt.Dimension(250, 22));
        pnlLeft.add(jLabel1);

        jLabel2.setText("Instant Messenger");
        pnlLeft.add(jLabel2);

        lblProgress.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProgress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ripple.gif"))); // NOI18N
        lblProgress.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblProgress.setPreferredSize(new java.awt.Dimension(250, 90));
        lblProgress.setRequestFocusEnabled(false);
        lblProgress.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlLeft.add(lblProgress);

        add(pnlLeft, java.awt.BorderLayout.WEST);

        pnlCenter.setBackground(new java.awt.Color(255, 255, 255));
        pnlCenter.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlCenter.setPreferredSize(new java.awt.Dimension(250, 0));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout();
        flowLayout1.setAlignOnBaseline(true);
        pnlCenter.setLayout(flowLayout1);
        pnlCenter.add(filler3);

        pnlInputs.setBackground(new java.awt.Color(255, 255, 255));
        pnlInputs.setPreferredSize(new java.awt.Dimension(220, 110));

        tfUsername.setPreferredSize(new java.awt.Dimension(200, 30));
        tfUsername.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tfUsernameMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tfUsernameMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tfUsernameMousePressed(evt);
            }
        });
        pnlInputs.add(tfUsername);

        tfDomain.setPreferredSize(new java.awt.Dimension(200, 30));
        pnlInputs.add(tfDomain);

        tfPassword.setPreferredSize(new java.awt.Dimension(200, 30));
        pnlInputs.add(tfPassword);

        pnlCenter.add(pnlInputs);

        pnlCheckboxes.setBackground(new java.awt.Color(255, 255, 255));
        pnlCheckboxes.setLayout(new javax.swing.BoxLayout(pnlCheckboxes, javax.swing.BoxLayout.Y_AXIS));

        cbSavePassword.setBackground(new java.awt.Color(255, 255, 255));
        cbSavePassword.setText("Save Password");
        cbSavePassword.setPreferredSize(new java.awt.Dimension(200, 20));
        pnlCheckboxes.add(cbSavePassword);

        cbAutoLogin.setBackground(new java.awt.Color(255, 255, 255));
        cbAutoLogin.setText("Auto login");
        cbAutoLogin.setPreferredSize(new java.awt.Dimension(200, 20));
        pnlCheckboxes.add(cbAutoLogin);

        cbLoginInvisible.setBackground(new java.awt.Color(255, 255, 255));
        cbLoginInvisible.setText("Login as invisible");
        cbLoginInvisible.setPreferredSize(new java.awt.Dimension(200, 20));
        pnlCheckboxes.add(cbLoginInvisible);

        cbAnonymous.setBackground(new java.awt.Color(255, 255, 255));
        cbAnonymous.setText("Login anonymously");
        cbAnonymous.setPreferredSize(new java.awt.Dimension(200, 20));
        pnlCheckboxes.add(cbAnonymous);

        pnlCenter.add(pnlCheckboxes);

        pnlBtns.setBackground(new java.awt.Color(255, 255, 255));
        pnlBtns.setPreferredSize(new java.awt.Dimension(220, 120));

        btnLogin.setBackground(new java.awt.Color(241, 100, 34));
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.setEnabled(false);
        btnLogin.setPreferredSize(new java.awt.Dimension(210, 30));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        pnlBtns.add(btnLogin);

        btnSignUp.setBackground(new java.awt.Color(255, 255, 255));
        btnSignUp.setText("Account");
        btnSignUp.setBorderPainted(false);
        btnSignUp.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnSignUp.setOpaque(false);
        btnSignUp.setPreferredSize(new java.awt.Dimension(95, 28));
        pnlBtns.add(btnSignUp);

        btnAdvanced.setBackground(new java.awt.Color(255, 255, 255));
        btnAdvanced.setText("Advanced");
        btnAdvanced.setBorderPainted(false);
        btnAdvanced.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnAdvanced.setOpaque(false);
        btnAdvanced.setPreferredSize(new java.awt.Dimension(110, 28));
        pnlBtns.add(btnAdvanced);

        btnReset.setBackground(new java.awt.Color(255, 255, 255));
        btnReset.setText("Reset Password");
        btnReset.setBorderPainted(false);
        btnReset.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnReset.setOpaque(false);
        btnReset.setPreferredSize(new java.awt.Dimension(210, 28));
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        pnlBtns.add(btnReset);

        pnlCenter.add(pnlBtns);

        add(pnlCenter, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLoginActionPerformed

    private void tfUsernameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tfUsernameMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            getPopup().show(tfUsername, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tfUsernameMousePressed

    private void tfUsernameMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tfUsernameMouseEntered
        // getPopup().show(tfUsername, evt.getX(), evt.getY());
    }//GEN-LAST:event_tfUsernameMouseEntered

    private void tfUsernameMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tfUsernameMouseExited
        // getPopup().setVisible(false);
    }//GEN-LAST:event_tfUsernameMouseExited

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        final String url = Default.getString(Default.PASSWORD_RESET_URL);
        try {
            BrowserLauncher.openURL(url);
        } catch (Exception e) {
            Log.error("Unable to load password reset.", e);
        }
    }//GEN-LAST:event_btnResetActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdvanced;
    private javax.swing.JButton btnSignUp;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnReset;
    private javax.swing.JCheckBox cbAnonymous;
    private javax.swing.JCheckBox cbAutoLogin;
    private javax.swing.JCheckBox cbLoginInvisible;
    private javax.swing.JCheckBox cbSavePassword;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblLogo;
    public final javax.swing.JLabel lblProgress = new javax.swing.JLabel();
    private javax.swing.JPanel pnlBtns;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlCheckboxes;
    private javax.swing.JPanel pnlInputs;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JTextField tfDomain;
    private javax.swing.JPasswordField tfPassword;
    private javax.swing.JTextField tfUsername;
    // End of variables declaration//GEN-END:variables

    public JTextField getUsernameField() {
        return tfUsername;
    }

    public JPasswordField getPasswordField() {
        return tfPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JCheckBox getCbAutoLogin() {
        return cbAutoLogin;
    }

    /**
     * Invokes the LoginDialog to be visible.
     *
     * @param parentFrame the parentFrame of the Login Dialog. This is used for
     * correct parenting.
     */
    public void invoke(final JFrame parentFrame) {
        // Before creating any connections. Update proxy if needed.
        try {
            updateProxyConfig();
        } catch (Exception e) {
            Log.error(e);
        }
        loginDialog = new JFrame(JiveInfo.getName());

        // Construct Dialog
        EventQueue.invokeLater(() -> {
            loginDialog.setIconImage(SparkManager.getApplicationImage().getImage());

            loginDialog.setContentPane(this);
            loginDialog.setLocationRelativeTo(parentFrame);

            loginDialog.setResizable(false);
            loginDialog.pack();
            loginDialog.setSize(550, 390);
            // Center dialog on screen
            GraphicUtils.centerWindowOnScreen(loginDialog);

            // Show dialog
            loginDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    quitLogin();
                }
            });
            if (ModelUtil.hasLength(getUsernameField().getText())) {
                getPasswordField().requestFocus();
            }

            if (!localPref.isStartedHidden() || !localPref.isAutoLogin()) {
                // Make dialog top most.
                loginDialog.setVisible(true);
            }
        });

    }

    protected void afterLogin() {
        Log.debug("Performing post-login tasks");

        // The Event Dispatcher Thread should only be used for fast tasks. The tasks in this method potentially are not.
        if (SmackConfiguration.DEBUG && EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must NOT be called on the Event Dispatcher Thread (but was)");
        }

        // Make certain Enterprise features persist across future logins
        persistEnterprise();

        // Load plugins before Workspace initialization to avoid any UI delays during plugin rendering, but after
        // Enterprise initialization, which can pull in additional plugin configuration (eg: blacklist).
        PluginManager.getInstance().loadPlugins();

        // Initialize and write default values from "Advanced Connection Preferences" to disk
        initAdvancedDefaults();

        Log.debug("Finished post-login tasks");
    }

    protected XMPPTCPConnectionConfiguration retrieveConnectionConfiguration() {
        int port = localPref.getXmppPort();

        int checkForPort = loginServer.indexOf(":");
        if (checkForPort != -1) {
            if (checkForPort != loginServer.length() - 2) { // : at end of string, so no port
                String portString = loginServer.substring(checkForPort + 1);
                // Set new port.
                port = Integer.parseInt(portString);
            }
            // strip the port from loginServer hostname
            loginServer = loginServer.substring(0, checkForPort);
        }

        ConnectionConfiguration.SecurityMode securityMode = localPref.getSecurityMode();
        boolean useDirectTls = localPref.isDirectTls();
        boolean hostPortConfigured = localPref.isHostAndPortConfigured();

        ProxyInfo proxyInfo = null;
        if (localPref.isProxyEnabled()) {
            ProxyInfo.ProxyType pType = localPref.getProtocol().equals("SOCKS")
                    ? ProxyInfo.ProxyType.SOCKS5 : ProxyInfo.ProxyType.HTTP;
            String pHost = ModelUtil.hasLength(localPref.getHost())
                    ? localPref.getHost() : null;
            int pPort = ModelUtil.hasLength(localPref.getPort())
                    ? Integer.parseInt(localPref.getPort()) : 0;
            String pUser = ModelUtil.hasLength(localPref.getProxyUsername())
                    ? localPref.getProxyUsername() : null;
            String pPass = ModelUtil.hasLength(localPref.getProxyPassword())
                    ? localPref.getProxyPassword() : null;

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

        if (localPref.isDebuggerEnabled()) {
            builder.enableDefaultDebugger();
        }

        if (hostPortConfigured) {
            builder.setHost(localPref.getXmppHost());
        }

        if (localPref.isProxyEnabled()) {
            builder.setProxyInfo(proxyInfo);
        }
        configureConnectionTls(builder, securityMode, useDirectTls, hostPortConfigured, loginServer);

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

    private void configureConnectionTls(XMPPTCPConnectionConfiguration.Builder builder, ConnectionConfiguration.SecurityMode securityMode, boolean useDirectTls, boolean hostPortConfigured, String serverName) {
        if (securityMode != ConnectionConfiguration.SecurityMode.disabled) {
            if (localPref.isDisableHostnameVerification()) {
                TLSUtils.disableHostnameVerificationForTlsCertificates(builder);
            }
            if (!useDirectTls) {
                // This use STARTTLS which starts initially plain connection to upgrade it to TLS.
                // It will use the same port as plain connections which is 5222.
                SparkSSLContextCreator.Options options = localPref.isAllowClientSideAuthentication() ? BOTH : ONLY_SERVER_SIDE;
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
                    List<InetAddress> resolvedAddresses = DNSUtil.getDNSResolver().lookupHostAddress(serverNameDnsName, null, DnssecMode.disabled);
                    if (resolvedAddresses.isEmpty()) {
                        throw new RuntimeException("Could not resolve " + serverNameDnsName);
                    }
                    builder.setHost(resolvedAddresses.get(0).getHostName());
                    builder.setPort(5223);
                }
                SparkSSLContextCreator.Options options = localPref.isAllowClientSideAuthentication() ? BOTH : ONLY_SERVER_SIDE;
                builder.setSocketFactory(new SparkSSLSocketFactory(options));
                // SMACK 4.1.9  does not recognize an 'old-style' SSL socket as being secure, which will cause a failure when
                // the 'required' Security Mode is defined. Here, we work around this by replacing that security mode with an
                // 'if-possible' setting.
                builder.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
            }
            SASLAuthentication.registerSASLMechanism(new SASLExternalMechanism());
        }
    }

    /**
     * Returns the username the user defined.
     *
     * Should be called only from the Event Dispatcher Thread.
     *
     * @return the username.
     */
    private String getUsername() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }
        return XmppStringUtils.escapeLocalpart(tfUsername.getText().trim());
    }

    /**
     * Returns the resulting bareJID from username and server
     *
     * Should be called only from the Event Dispatcher Thread.
     *
     * @return the bare JID that's being logged in with.
     */
    private String getBareJid() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }
        return tfUsername.getText() + "@" + tfDomain.getText();
    }

    /**
     * Returns the password specified by the user.
     *
     * Should be called only from the Event Dispatcher Thread.
     *
     * @return the password.
     */
    private String getPassword() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }
        return new String(tfPassword.getPassword());
    }

    /**
     * Returns the server name specified by the user.
     *
     * Should be called only from the Event Dispatcher Thread.
     *
     * @return the server name.
     */
    private String getServerName() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }
        return tfDomain.getText().trim();
    }

    /**
     * Return whether user wants to login as invisible or not.
     *
     * Should be called only from the Event Dispatcher Thread.
     *
     * @return the true if user wants to login as invisible.
     */
    boolean isLoginAsInvisible() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }
        return cbLoginInvisible.isSelected();
    }

    /**
     * ActionListener implementation.
     *
     * @param e the ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSignUp) {
            AccountCreationWizard createAccountPanel = new AccountCreationWizard();
            createAccountPanel.invoke(loginDialog);

            if (createAccountPanel.isRegistered()) {
                tfUsername.setText(createAccountPanel.getUsernameWithoutEscape());
                tfPassword.setText(createAccountPanel.getPassword());
                tfDomain.setText(createAccountPanel.getServer());
                btnLogin.setEnabled(true);
            }
        } else if (e.getSource() == btnLogin) {
            TaskEngine.getInstance().submit(this::login);
        } else if (e.getSource() == btnAdvanced) {
            final LoginSettingDialog loginSettingsDialog = new LoginSettingDialog();
            loginSettingsDialog.invoke(loginDialog);
            useSSO(localPref.isSSOEnabled());
        } else if (e.getSource() == cbSavePassword) {
            cbAutoLogin.setEnabled(cbSavePassword.isSelected());

            if (!cbSavePassword.isSelected()) {
                cbAutoLogin.setSelected(false);
            }
        } else if (e.getSource() == cbAutoLogin) {
            if ((cbAutoLogin.isSelected() && (!localPref.isSSOEnabled()))) {
                cbSavePassword.setSelected(true);
            }
        } else if (e.getSource() == cbAnonymous) {
            tfUsername.setEnabled(!cbAnonymous.isSelected());
            tfPassword.setEnabled(!cbAnonymous.isSelected());
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
                tfUsername.setText(username);
                tfDomain.setText(host);

                try {
                    tfPassword.setText(localPref.getPasswordForUser(getBareJid()));
                    if (tfPassword.getPassword().length < 1) {
                        btnLogin.setEnabled(cbAnonymous.isSelected());
                    } else {
                        btnLogin.setEnabled(true);
                    }
                } catch (Exception e1) {
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
        if (e.getKeyCode() == KeyEvent.VK_RIGHT
                && ((JTextField) e.getSource()).getCaretPosition() == ((JTextField) e.getSource()).getText().length()) {
            getPopup().show(otherUsers, 0, 0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        validateDialog();
    }

    /**
     * Checks the users input and enables/disables the login button depending on
     * state.
     *
     * Should be called only from the Event Dispatcher Thread.
     */
    private void validateDialog() {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }
        btnLogin.setEnabled(cbAnonymous.isSelected()
                || ModelUtil.hasLength(getUsername())
                && (ModelUtil.hasLength(getPassword()) || localPref.isSSOEnabled())
                && ModelUtil.hasLength(getServerName()));
    }

    /**
     * Validates key input.
     *
     * @param e the keyEvent.
     */
    private void validate(KeyEvent e) {
        if (btnLogin.isEnabled() && e.getKeyChar() == KeyEvent.VK_ENTER) {
            TaskEngine.getInstance().submit(this::login);
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
     * Should be called only from the Event Dispatcher Thread.
     *
     * @param available true to enable components, otherwise false to disable.
     */
    private void setComponentsAvailable(boolean available)
    {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }

        cbSavePassword.setEnabled(available);
        cbAutoLogin.setEnabled(available);
        cbLoginInvisible.setEnabled(available);
        cbAnonymous.setEnabled(available);
        btnLogin.setEnabled(available);
        btnAdvanced.setEnabled(available);
        btnSignUp.setEnabled(available);

        // Need to set both editable and enabled for best behavior.
        tfUsername.setEditable(available);
        tfUsername.setEnabled(available && !cbAnonymous.isSelected());

        tfPassword.setEditable(available);
        tfPassword.setEnabled(available && !cbAnonymous.isSelected());

        if (Default.getBoolean(Default.HOST_NAME_CHANGE_DISABLED) || !localPref.getHostNameChange()) {
            tfDomain.setEditable(false);
            tfDomain.setEnabled(false);
        } else {
            tfDomain.setEditable(available);
            tfDomain.setEnabled(available);
        }

        if (available) {
            // Reapply focus to password field
            tfPassword.requestFocus();
        }
    }

    /**
     * Displays the progress bar.
     *
     * Should be called only from the Event Dispatcher Thread.
     *
     * @param visible true to display progress bar, false to hide it.
     */
    private void setProgressBarVisible(boolean visible) {
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }

        lblProgress.setVisible(visible);
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension dim = super.getPreferredSize();
        dim.height = 230;
        return dim;
    }

    public void useSSO(boolean use) {
        if (use) {
            //usernameLabel.setVisible(true);
            tfUsername.setVisible(true);

            //passwordLabel.setVisible(false);
            tfPassword.setVisible(false);

            cbSavePassword.setVisible(false);
            cbSavePassword.setSelected(false);

            tfDomain.setVisible(true);

            cbAutoLogin.setVisible(true);
            //serverLabel.setVisible(true);
            cbLoginInvisible.setVisible(true);
            cbAnonymous.setVisible(false);

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
                        princRealm = name.substring(indexOne + 1);
                    }
                    btnLogin.setEnabled(true);
                }
            } catch (LoginException le) {
                Log.debug(le.getMessage());
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
                tfUsername.setText(userName);
            } else {
                tfUsername.setText(princName);
            }
        } else {
            cbAutoLogin.setVisible(true);
            tfUsername.setVisible(true);
            tfPassword.setVisible(true);
            cbSavePassword.setVisible(true);
            // usernameLabel.setVisible(true);
            // passwordLabel.setVisible(true);
            // serverLabel.setVisible(true);
            tfDomain.setVisible(true);
            cbLoginInvisible.setVisible(true);
            cbAnonymous.setVisible(true);

            Configuration.setConfiguration(null);

            validateDialog();
        }
    }

    /**
     * Login to the specified server using username, password, and workgroup.
     * Handles error representation as well as logging.
     *
     * Should _not_ be called from the Event Dispatcher Thread.
     */
    private void login() {
        Log.debug("Start login");

        if (localPref.isDebuggerEnabled()) {
            SmackConfiguration.DEBUG = true;
        }

        // The Event Dispatcher Thread should only be used for fast tasks. Network IO is not one of those.
        if (SmackConfiguration.DEBUG && EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must NOT be called on the Event Dispatcher Thread (but was)");
        }

        localPref = SettingsManager.getLocalPreferences();
        SmackConfiguration.setDefaultReplyTimeout(localPref.getTimeOut() * 1000);

        AtomicBoolean savePasswordAfterSuccessfulLogin = new AtomicBoolean(false);
        AtomicBoolean autoLogin = new AtomicBoolean(false);
        try {
            EventQueue.invokeAndWait(() -> {
                setComponentsAvailable(false);
                setProgressBarVisible(true);

                setLoginUsername(getUsername());
                setLoginPassword(getPassword());
                setLoginServer(getServerName());

                localPref.setLoginAsInvisible(cbLoginInvisible.isSelected());
                localPref.setLoginAnonymously(cbAnonymous.isSelected());
                savePasswordAfterSuccessfulLogin.set(cbSavePassword.isSelected());
                autoLogin.set(cbAutoLogin.isSelected());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new IllegalStateException("Exception while modifying UI components during login.", e);
        }

        try {
            // TODO: SPARK-2140 - add support to Spark for stream management. Challenges expected around reconnection logic!
            XMPPTCPConnection.setUseStreamManagementDefault(false);

            connection = new XMPPTCPConnection(retrieveConnectionConfiguration());
            connection.setParsingExceptionCallback(new ExceptionLoggingCallback());

            connection.connect();

            if (localPref.isLoginAnonymously() && !localPref.isSSOEnabled()) {
                // ConnectionConfiguration.performSaslAnonymousAuthentication() used earlier in connection configuration builder,
                // so now we can just login()
                connection.login();
            } else {
                String resource = localPref.getResource();
                if (Default.getBoolean(Default.USE_HOSTNAME_AS_RESOURCE) || localPref.isUseHostnameAsResource()) {
                    try {
                        resource = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                        Log.warning("Cannot set hostname as resource - unable to retrieve hostname.", e);
                    }
                } else if (Default.getBoolean(Default.USE_VERSION_AS_RESOURCE) || localPref.isUseVersionAsResource()) {
                    resource = JiveInfo.getName() + " " + JiveInfo.getVersion();
                }
                
                localPref.setResource(resource);
                Resourcepart resourcepart = Resourcepart.from(modifyWildcards(resource).trim());
                connection.login(getLoginUsername(), getLoginPassword(), resourcepart);
            }
            Log.debug("Logged in!");

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
                setEnabled(true);
                setComponentsAvailable(true);
                setProgressBarVisible(false);

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
                            TaskEngine.getInstance().submit(this::login);
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
                                TaskEngine.getInstance().submit(this::login);
                            }
                        } else {
                            // For anything else, show a generic error dialog.
                            MessageDialog.showErrorDialog(loginDialog, errorMessage, xee);
                        }
                    }
                }
            });
        }

        // Since the connection and workgroup are valid. Add a ConnectionListener
        connection.addConnectionListener(SparkManager.getSessionManager());

        // Initialize chat state notification mechanism in smack
        ChatStateManager.getInstance(SparkManager.getConnection());

        // Persist information
        localPref.setLastUsername(getLoginUsername());

        // Check to see if the password should be saved or cleared from file.
        if (savePasswordAfterSuccessfulLogin.get()) {
            try {
                localPref.setPasswordForUser(getBareJid(), getLoginPassword());
            } catch (Exception e) {
                Log.error("Error storing encrypted password.", e);
            }
        } else {
            try {
                localPref.clearPasswordForAllUsers();
            } catch (Exception e) {
                Log.debug("Unable to clear saved password..." + e);
            }
        }

        localPref.setSavePassword(savePasswordAfterSuccessfulLogin.get());
        localPref.setAutoLogin(autoLogin.get());
        localPref.setServer(getLoginServer());
        SettingsManager.saveSettings();

        afterLogin();

        EventQueue.invokeLater(()-> {
            lblProgress.setText(Res.getString("message.connecting.please.wait"));

            // Startup Spark
            startSpark();

            // dispose login dialog
            loginDialog.dispose();

            // Show ChangeLog if we need to.
            // new ChangeLogDialog().showDialog();
        });
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException {
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

    /**
     * If the user quits, just shut down the application.
     */
    private void quitLogin() {
        System.exit(1);
    }

    /**
     * Initializes Spark's main window.
     *
     * Should only be called from the Event Dispatcher Thread
     */
    private void startSpark() {
        Log.debug("Starting Spark's main window.");
        // Most Swing components are not thread safe, and _must_ be executed on the Event Dispatcher Thread.
        if (SmackConfiguration.DEBUG && !EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Must be called on the Event Dispatcher Thread (but was not)");
        }
        // Invoke the MainWindow.
        try {
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
                mainWindow.setSize(500, 520);

                // Center Window on Screen
                GraphicUtils.centerWindowOnScreen(mainWindow);
            } else {
                mainWindow.setBounds(mainWindowBounds);
            }

            if (loginDialog != null) {
                if (loginDialog.isVisible()) {
                    mainWindow.setVisible(true);
                }
                loginDialog.dispose();
            }
            // Build the layout in the workspace
            workspace.buildLayout();
            Log.debug("Finished starting Spark's main window.");
        } catch (Exception e) {
            Log.error("An exception occurred while trying to open Spark's main window.", e);
        }
    }

    /**
     * Updates System properties with Proxy configuration.
     *
     * @throws Exception thrown if an exception occurs.
     */
    private void updateProxyConfig() throws Exception {
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
     *
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
            for (Enumeration<?> e = dnsLookup.getAll(); e.hasMoreElements();) {
                Attribute record = (Attribute) e.nextElement();
                for (Enumeration<?> e2 = record.getAll(); e2.hasMoreElements();) {
                    String sRecord = (String) e2.nextElement();
                    String[] sRecParts = sRecord.split(" ");
                    Integer pri = Integer.valueOf(sRecParts[0]);
                    if (priorities.contains(pri)) {
                        List<String> recs = records.get(pri);
                        if (recs == null) {
                            recs = new ArrayList<>();
                        }
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
        //Check that the ClientControl plugin Openfire is installed on the server.
        if(Enterprise.isSparkManagerInstalled()){
            localPref.setAccountsReg(Enterprise.containsFeature(Enterprise.ACCOUNTS_REG_FEATURE));
            localPref.setAdvancedConfig(Enterprise.containsFeature(Enterprise.ADVANCED_CONFIG_FEATURE));
            localPref.setHostNameChange(Enterprise.containsFeature(Enterprise.HOST_NAME_FEATURE));
            localPref.setInvisibleLogin(Enterprise.containsFeature(Enterprise.INVISIBLE_LOGIN_FEATURE));
            localPref.setAnonymousLogin(Enterprise.containsFeature(Enterprise.ANONYMOUS_LOGIN_FEATURE));
            localPref.setPswdAutologin(Enterprise.containsFeature(Enterprise.SAVE_PASSWORD_FEATURE));
            if (Enterprise.containsFeature(Enterprise.HOSTNAME_AS_RESOURCE_FEATURE) != Enterprise.containsFeature(Enterprise.VERSION_AS_RESOURCE_FEATURE)) {
                localPref.setUseHostnameAsResource(Enterprise.containsFeature(Enterprise.HOSTNAME_AS_RESOURCE_FEATURE));
                localPref.setUseVersionAsResource(Enterprise.containsFeature(Enterprise.VERSION_AS_RESOURCE_FEATURE));
            }
            localPref.setFileTransferIbbOnly(Enterprise.containsFeature(Enterprise.IBB_FEATURE));
        }
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
        localPref.setResource(localPref.getResource());
        localPref.setSaslGssapiSmack3Compatible(localPref.isSaslGssapiSmack3Compatible());
        localPref.setDirectTls(localPref.isDirectTls());
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

        if(Default.getBoolean(Default.IDLE_LOCK) || !Enterprise.containsFeature(Enterprise.IDLE_FEATURE)) {
            localPref.setIdleTime(Integer.parseInt(Default.getString(Default.IDLE_TIME)));
            localPref.setIdleMessage(Res.getString("status.away"));
            localPref.setIdleOn(true);
        }

        SettingsManager.saveSettings();
    }

}
