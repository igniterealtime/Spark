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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.ImageIcon;
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
import javax.swing.text.JTextComponent;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatStateManager;
import org.jivesoftware.spark.SessionManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.DummySSLSocketFactory;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettings;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Dialog to log in a user into the Spark Server. The LoginDialog is used only
 * for login in registered users into the Spark Server.
 */
public class LoginDialog {
    private JFrame loginDialog;
    private static final String BUTTON_PANEL = "buttonpanel"; // NOTRANS
    private static final String PROGRESS_BAR = "progressbar"; // NOTRANS
    private LocalPreferences localPref;
    private ArrayList<String> _usernames = new ArrayList<String>();
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
        }
        catch (Exception e) {
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
        }
        catch (Exception e) {
            Log.error(e);
        }


        // Construct Dialog
    	 EventQueue.invokeLater(new Runnable() {
   		 public void run() {
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
   	                        new Insets(0, 0, 0, 0), 0, 0));

   	        loginDialog.setContentPane(mainPanel);
   	        loginDialog.setLocationRelativeTo(parentFrame);

   	        loginDialog.setResizable(false);
   	        loginDialog.pack();

   	        // Center dialog on screen
   	        GraphicUtils.centerWindowOnScreen(loginDialog);

   	        // Show dialog
   	        loginDialog.addWindowListener(new WindowAdapter() {
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
   		 }
   	 });


    }

    //This method can be overwritten by subclasses to provide additional validations
    //(such as certificate download functionality when connecting)
    protected boolean beforeLoginValidations() {
    	return true;
    }
    
    protected void afterLogin() {
        // Does noting by default - but can be overwritten by subclasses to provide additional
        // settings
    }    

    protected ConnectionConfiguration retrieveConnectionConfiguration() {
        int port = localPref.getXmppPort();

        int checkForPort = loginServer.indexOf(":");
        if (checkForPort != -1) {
            String portString = loginServer.substring(checkForPort + 1);
            if (ModelUtil.hasLength(portString)) {
                // Set new port.
                port = Integer.valueOf(portString);
            }
        }

        boolean useSSL = localPref.isSSL();
        boolean hostPortConfigured = localPref.isHostAndPortConfigured();

        ConnectionConfiguration config = null;

        if (useSSL) {
            if (!hostPortConfigured) {
                config = new ConnectionConfiguration(loginServer, 5223);
                config.setSocketFactory(new DummySSLSocketFactory());
            }
            else {
                config = new ConnectionConfiguration(localPref.getXmppHost(), port, loginServer);
                config.setSocketFactory(new DummySSLSocketFactory());
            }
        }
        else {
            if (!hostPortConfigured) {
                config = new ConnectionConfiguration(loginServer);
            }
            else {
                config = new ConnectionConfiguration(localPref.getXmppHost(), port, loginServer);
            }


        }
        config.setReconnectionAllowed(true);
        config.setRosterLoadedAtLogin(true);
        config.setSendPresence(false);

        if (localPref.isPKIEnabled()) {
            SASLAuthentication.supportSASLMechanism("EXTERNAL");
            config.setKeystoreType(localPref.getPKIStore());
            if(localPref.getPKIStore().equals("PKCS11")) {
                config.setPKCS11Library(localPref.getPKCS11Library());
            }
            else if(localPref.getPKIStore().equals("JKS")) {
                config.setKeystoreType("JKS");
                config.setKeystorePath(localPref.getJKSPath());

            }
            else if(localPref.getPKIStore().equals("X509")) {
                //do something
            }
            else if(localPref.getPKIStore().equals("Apple")) {
                config.setKeystoreType("Apple");
            }
        }

        boolean compressionEnabled = localPref.isCompressionEnabled();
        config.setCompressionEnabled(compressionEnabled);            
        if(ModelUtil.hasLength(localPref.getTrustStorePath())) {
        	config.setTruststorePath(localPref.getTrustStorePath());
        	config.setTruststorePassword(localPref.getTrustStorePassword());
        }
        return config;
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

        private final RolloverButton createAccountButton = new RolloverButton();
        private final RolloverButton passwordResetButton = new RolloverButton();

        private final JLabel progressBar = new JLabel();

        // Panel used to hold buttons
        private final CardLayout cardLayout = new CardLayout(0, 5);
        final JPanel cardPanel = new JPanel(cardLayout);

        final JPanel buttonPanel = new JPanel(new GridBagLayout());
        private final GridBagLayout GRIDBAGLAYOUT = new GridBagLayout();
        private XMPPConnection connection = null;

        private JLabel headerLabel = new JLabel();
        private JLabel accountLabel = new JLabel();
        private JLabel accountNameLabel = new JLabel();
        private JLabel serverNameLabel = new JLabel();
        private JLabel ssoServerLabel = new JLabel();

        private RolloverButton otherUsers = new RolloverButton(SparkRes.getImageIcon(SparkRes.PANE_UP_ARROW_IMAGE));


        LoginPanel() {
            //setBorder(BorderFactory.createTitledBorder("Sign In Now"));
            ResourceUtils.resButton(savePasswordBox, Res.getString("checkbox.save.password"));
            ResourceUtils.resButton(autoLoginBox, Res.getString("checkbox.auto.login"));
            ResourceUtils.resLabel(serverLabel, serverField, Res.getString("label.server"));
            ResourceUtils.resButton(createAccountButton, Res.getString("label.accounts"));
            ResourceUtils.resButton(passwordResetButton, Res.getString("label.passwordreset"));

            savePasswordBox.setOpaque(false);
            autoLoginBox.setOpaque(false);
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
            add(savePasswordBox,
                    new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
            add(autoLoginBox,
                    new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));

            // Add button but disable the login button initially
            savePasswordBox.addActionListener(this);
            autoLoginBox.addActionListener(this);

            if (!Default.getBoolean(Default.ACCOUNT_DISABLED)) {
                buttonPanel.add(createAccountButton,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
            }

            if (Default.getBoolean(Default.PASSWORD_RESET_ENABLED)) {
                buttonPanel.add(passwordResetButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
                passwordResetButton.addActionListener(new ActionListener() {
                    final String url = Default.getString(Default.PASSWORD_RESET_URL);
                    private static final long serialVersionUID = 2680369963282231348L;

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

            if(!Default.getBoolean(Default.ADVANCED_DISABLED)){
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


            add(cardPanel, new GridBagConstraints(0, 8, 4, 1,
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
            progressBar.setText(Res.getString("message.autenticating"));
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
                usernameField.setText(StringUtils.unescapeNode(userProp));
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
            useSSO(localPref.isSSOEnabled());
            if (autoLoginBox.isSelected()) {
                savePasswordBox.setEnabled(false);
                autoLoginBox.setEnabled(false);
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
                savePasswordBox.setEnabled(false);
                autoLoginBox.setEnabled(false);
                validateLogin();
            }

            createAccountButton.addActionListener(this);

            final String lockedDownURL = Default.getString(Default.HOST_NAME);
            if (ModelUtil.hasLength(lockedDownURL)) {
                serverField.setText(lockedDownURL);
            }
            if (Default.getBoolean("HOST_NAME_CHANGE_DISABLED"))
                serverField.setEnabled(false);



        }

        /**
         * Returns the username the user defined.
         *
         * @return the username.
         */
        private String getUsername() {
            return StringUtils.escapeNode(usernameField.getText().trim());
        }

        /**
         * Returns the resulting bareJID from username and server
         * @return
         */
        private String getBareJid()
        {
            return usernameField.getText()+"@"+serverField.getText();
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
         * ActionListener implementation.
         *
         * @param e the ActionEvent
         */
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == quitButton) {
                quitLogin();
            }
            else if (e.getSource() == createAccountButton) {
                AccountCreationWizard createAccountPanel = new AccountCreationWizard();
                createAccountPanel.invoke(loginDialog);

                if (createAccountPanel.isRegistered()) {
                    usernameField.setText(createAccountPanel.getUsernameWithoutEscape());
                    passwordField.setText(createAccountPanel.getPassword());
                    serverField.setText(createAccountPanel.getServer());
                    loginButton.setEnabled(true);
                }
            }
            else if (e.getSource() == loginButton) {
                validateLogin();
            }
            else if (e.getSource() == advancedButton) {
                final LoginSettingDialog loginSettingsDialog = new LoginSettingDialog();
                loginSettingsDialog.invoke(loginDialog);
                useSSO(localPref.isSSOEnabled());
            }
            else if (e.getSource() == savePasswordBox) {
                autoLoginBox.setEnabled(savePasswordBox.isSelected());

                if (!savePasswordBox.isSelected()) {
                    autoLoginBox.setSelected(false);
                }
            }
            else if (e.getSource() == autoLoginBox) {
                if (autoLoginBox.isSelected()) {
                    savePasswordBox.setSelected(true);
                }
            }
        }

        private JPopupMenu getPopup()
        {
            JPopupMenu popup = new JPopupMenu();
	    for(final String key : _usernames)
	    {

		JMenuItem menu = new JMenuItem(key);

		final String username = key.split("@")[0];
		final String host = key.split("@")[1];
		menu.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			usernameField.setText(username);
			serverField.setText(host);

			try {
			    passwordField.setText(localPref.getPasswordForUser(getBareJid()));
			    if(passwordField.getPassword().length<1) {
				loginButton.setEnabled(false);
			    }
			    else {
				loginButton.setEnabled(true);
			    }
			} catch (Exception e1) {
			}

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
        public void keyTyped(KeyEvent e) {
            validate(e);
        }

        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_RIGHT &&
        	    ((JTextField)e.getSource()).getCaretPosition()==((JTextField)e.getSource()).getText().length())
            {
        	getPopup().show(otherUsers,0,0);
            }
        }

        public void keyReleased(KeyEvent e) {
            validateDialog();
        }

        /**
         * Checks the users input and enables/disables the login button depending on state.
         */
        private void validateDialog() {
            loginButton.setEnabled(
                    ModelUtil.hasLength(getUsername()) &&
                    ( ModelUtil.hasLength(getPassword()) || localPref.isSSOEnabled() ) &&
                    ModelUtil.hasLength(getServerName())   );
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

        public void focusGained(FocusEvent e) {
            Object o = e.getSource();
            if (o instanceof JTextComponent) {
                ((JTextComponent)o).selectAll();
            }
        }

        public void focusLost(FocusEvent e) {
        }

        /**
         * Enables/Disables the editable components in the login screen.
         *
         * @param editable true to enable components, otherwise false to disable.
         */
        private void enableComponents(boolean editable) {

            // Need to set both editable and enabled for best behavior.
            usernameField.setEditable(editable);
            usernameField.setEnabled(editable);

            passwordField.setEditable(editable);
            passwordField.setEnabled(editable);

            final String lockedDownURL = Default.getString(Default.HOST_NAME);
            if (!ModelUtil.hasLength(lockedDownURL)) {
                serverField.setEditable(editable);
                serverField.setEnabled(editable);
            }


            if (editable) {
                // Reapply focus to username field
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
            }
            else {
                cardLayout.show(cardPanel, BUTTON_PANEL);
            }
        }

        /**
         * Validates the users login information.
         */
        private void validateLogin() {
            final SwingWorker loginValidationThread = new SwingWorker() {
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
                    }
                    else {
                        EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                savePasswordBox.setEnabled(true);
                                autoLoginBox.setEnabled(true);
                                enableComponents(true);
                                setProgressBarVisible(false);
                            }
                        });

                    }
                    return loginSuccessfull;
                }
            };

            // Start the login process in seperate thread.
            // Disable textfields
            enableComponents(false);

            // Show progressbar
            setProgressBarVisible(true);

            loginValidationThread.start();
        }

        public JPasswordField getPasswordField() {
            return passwordField;
        }

        public Dimension getPreferredSize() {
            final Dimension dim = super.getPreferredSize();
            dim.height = 200;
            return dim;
        }

        public void useSSO(boolean use) {
            if (use) {
                usernameLabel.setVisible(true);
                usernameField.setVisible(true);

                passwordLabel.setVisible(false);
                passwordField.setVisible(false);

                savePasswordBox.setVisible(false);

                accountLabel.setVisible(true);
                accountNameLabel.setVisible(true);

                serverField.setVisible(true);

                autoLoginBox.setVisible(true);
                serverLabel.setVisible(true);

                headerLabel.setVisible(true);

                if(localPref.getDebug()) {
                    System.setProperty("java.security.krb5.debug","true");
                    System.setProperty("sun.security.krb5.debug","true");
                } else {
                    System.setProperty("java.security.krb5.debug","false");
                    System.setProperty("sun.security.krb5.debug","false");
                }

                String ssoMethod = localPref.getSSOMethod();
                if(!ModelUtil.hasLength(ssoMethod)) {
                    ssoMethod = "file";
                }

                System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
                GSSAPIConfiguration config = new GSSAPIConfiguration( ssoMethod.equals("file") );
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
                            princRealm = name.substring(indexOne+1);
                        }
                        loginButton.setEnabled(true);
                    }
                }
                catch (LoginException le) {
                    Log.debug(le.getMessage());
                    accountNameLabel.setText(Res.getString("title.login.no.account"));
                    //useSSO(false);
                }

                String ssoKdc;
                if(ssoMethod.equals("dns")) {
                    if (princRealm != null) { //princRealm is null if we got a LoginException above.
                        ssoKdc = getDnsKdc(princRealm);
                        System.setProperty("java.security.krb5.realm",princRealm);
                        System.setProperty("java.security.krb5.kdc",ssoKdc);
                    }
                } else if(ssoMethod.equals("manual")) {
                    princRealm = localPref.getSSORealm();
                    ssoKdc = localPref.getSSOKDC();
                    System.setProperty("java.security.krb5.realm",princRealm);
                    System.setProperty("java.security.krb5.kdc",ssoKdc);
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
            }
            else {
                autoLoginBox.setVisible(true);
                usernameField.setVisible(true);
                passwordField.setVisible(true);
                savePasswordBox.setVisible(true);
                usernameLabel.setVisible(true);
                passwordLabel.setVisible(true);
                serverLabel.setVisible(true);
                serverField.setVisible(true);

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
            final SessionManager sessionManager = SparkManager.getSessionManager();

            boolean hasErrors = false;
            String errorMessage = null;

            // Handle specifyed Workgroup
            String serverName = getServerName();


            if (!hasErrors) {
                localPref = SettingsManager.getLocalPreferences();
                if (localPref.isDebuggerEnabled()) {
                    XMPPConnection.DEBUG_ENABLED = true;
                }

                SmackConfiguration.setPacketReplyTimeout(localPref.getTimeOut() * 1000);

                // Get connection
                try {
                	ConnectionConfiguration config = retrieveConnectionConfiguration();
                    connection = new XMPPConnection(config,this);
                    //If we want to use the debug version of smack, we have to check if
                    //we are on the dispatch thread because smack will create an UI
		    if (localPref.isDebuggerEnabled()) {
			if (EventQueue.isDispatchThread()) {
			    connection.connect();
			} else {
			    EventQueue.invokeAndWait(new Runnable() {

				@Override
				public void run() {
				    try {
					connection.connect();
				    } catch (XMPPException e) {
					Log.error("connection error",e);
				    }

				}
			    });
			}
		    } else {
			connection.connect();
		    }

                    String resource = localPref.getResource();
                    connection.login(getLoginUsername(), getLoginPassword(),
                	    org.jivesoftware.spark.util.StringUtils.modifyWildcards(resource).trim());

                    sessionManager.setServerAddress(connection.getServiceName());
                    sessionManager.initializeSession(connection, getLoginUsername(), getLoginPassword());
                    sessionManager.setJID(connection.getUser());
                }
                catch (Exception xee) {
                    if (!loginDialog.isVisible()) {
                        loginDialog.setVisible(true);
                    }
                    if (xee instanceof XMPPException) {

                       XMPPException xe = (XMPPException)xee;
                       final XMPPError error = xe.getXMPPError();
                       int errorCode = 0;
                       if (error != null) {
                           errorCode = error.getCode();
                       }
                       if (errorCode == 401) {
                           errorMessage = Res.getString("message.invalid.username.password");
                       }
                       else if (errorCode == 502 || errorCode == 504) {
                           errorMessage = Res.getString("message.server.unavailable");
                       }
                       else if (errorCode == 409) {
                           errorMessage = Res.getString("label.conflict.error");
                       }
                       else {
                           errorMessage = Res.getString("message.unrecoverable.error");
                       }
                   }
                   else {
                       errorMessage = SparkRes.getString(SparkRes.UNRECOVERABLE_ERROR);
                   }

                    // Log Error
                    Log.warning("Exception in Login:", xee);
                    hasErrors = true;
                }
            }

            if (hasErrors) {

            	final String finalerrorMessage = errorMessage;
               EventQueue.invokeLater(new Runnable() {

     					@Override
     					public void run()
     					{
     							progressBar.setVisible(false);
     							//progressBar.setIndeterminate(false);

     							// Show error dialog
     							if (loginDialog.isVisible()) {
     								if (!localPref.isSSOEnabled()) {
     	                        JOptionPane.showMessageDialog(loginDialog, finalerrorMessage, Res.getString("title.login.error"),
     	                                JOptionPane.ERROR_MESSAGE);
     								}
     								else {
     	                        JOptionPane.showMessageDialog(loginDialog, Res.getString("title.advanced.connection.sso.unable"), Res.getString("title.login.error"),
     	                                JOptionPane.ERROR_MESSAGE);
     	                        //useSSO(false);
     	                        //localPref.setSSOEnabled(false);
     								}
     							}
     						}
     				});

                setEnabled(true);
                return false;
            }

            // Since the connection and workgroup are valid. Add a ConnectionListener
            connection.addConnectionListener(SparkManager.getSessionManager());
            //Initialize chat state notification mechanism in smack
            ChatStateManager.getInstance(SparkManager.getConnection());

            // Persist information
            localPref.setLastUsername(getLoginUsername());

            // Check to see if the password should be saved.
            if (savePasswordBox.isSelected()) {
                try {
                    localPref.setPasswordForUser(getBareJid(), getPassword());
                }
                catch (Exception e) {
                    Log.error("Error encrypting password.", e);
                }
            }

            localPref.setSavePassword(savePasswordBox.isSelected());
            localPref.setAutoLogin(autoLoginBox.isSelected());

//            if (localPref.isSSOEnabled()) {
//                localPref.setAutoLogin(true);
//            }

            localPref.setServer(serverField.getText());


            SettingsManager.saveSettings();

            return !hasErrors;
        }

	public void handle(Callback[] callbacks) throws IOException  {
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
       try
		{
			EventQueue.invokeLater(new Runnable() {
			 	public void run() {
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
			      int x = settings.getMainWindowX();
			      int y = settings.getMainWindowY();
			      int width = settings.getMainWindowWidth();
			      int height = settings.getMainWindowHeight();

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
			          }
			          else {
			              mainWindow.getSplitPane().setDividerLocation(240);
			          }

			          mainWindow.getContentPane().add(splitPane, BorderLayout.CENTER);
			      }
			      else {
			          mainWindow.getContentPane().add(workspace.getCardPanel(), BorderLayout.CENTER);
			      }

			      if (x == 0 && y == 0) {
			          // Use Default size
			          mainWindow.setSize(310, 520);

			          // Center Window on Screen
			          GraphicUtils.centerWindowOnScreen(mainWindow);
			      }
			      else {
			          mainWindow.setBounds(x, y, width, height);
			      }

			      if (loginDialog.isVisible()) {
			          mainWindow.setVisible(true);
			      }

			      loginDialog.setVisible(false);

			      // Build the layout in the workspace
			      workspace.buildLayout();
			 	}
			 });
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
            }
            else {
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
    public class LoginBackgroundPanel extends JPanel {
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
        public void paintComponent(Graphics g) {
            Image backgroundImage = icons.getImage();
            double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
            double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
            AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
            ((Graphics2D)g).drawImage(backgroundImage, xform, this);
        }
    }

    /**
     * The image panel to display the Spark Logo.
     */
    public class ImagePanel extends JPanel {

		private static final long serialVersionUID = -1778389077647562606L;
		private final ImageIcon icons = Default.getImageIcon(Default.MAIN_IMAGE);

        /**
         * Uses the Spark logo to paint as the background.
         *
         * @param g the graphics to use.
         */
        public void paintComponent(Graphics g) {
            final Image backgroundImage = icons.getImage();
            final double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
            final double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
            AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
            ((Graphics2D)g).drawImage(backgroundImage, xform, this);
        }

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
                pluginXML = saxReader.read(settingsXML);
            }
            catch (DocumentException e) {
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
                localPref.setPasswordForUser(username+"@"+server, password);

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
            Hashtable<String,String> env= new Hashtable<String,String>();
            env.put("java.naming.factory.initial","com.sun.jndi.dns.DnsContextFactory");
            DirContext context = new InitialDirContext(env);
            Attributes dnsLookup = context.getAttributes("_kerberos._udp."+realm, new String[]{"SRV"});

            ArrayList<Integer> priorities = new ArrayList<Integer>();
            HashMap<Integer,List<String>> records = new HashMap<Integer,List<String>>();
            for (Enumeration<?> e = dnsLookup.getAll() ; e.hasMoreElements() ; ) {
                Attribute record = (Attribute)e.nextElement();
                for (Enumeration<?> e2 = record.getAll() ; e2.hasMoreElements() ; ) {
                    String sRecord = (String)e2.nextElement();
                    String [] sRecParts = sRecord.split(" ");
                    Integer pri = Integer.valueOf(sRecParts[0]);
                    if(priorities.contains(pri)) {
                        List<String> recs = records.get(pri);
                        if(recs == null) recs = new ArrayList<String>();
                        recs.add(sRecord);
                    } else {
                        priorities.add(pri);
                        List<String> recs = new ArrayList<String>();
                        recs.add(sRecord);
                        records.put(pri,recs);
                    }
                }
            }
            Collections.sort(priorities);
            List<String> l = records.get(priorities.get(0));
            String toprec = l.get(0);
            String [] sRecParts = toprec.split(" ");
            return sRecParts[3];
        } catch (NamingException e){
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


}
