package org.jivesoftware.spark.ui.login;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

import static java.awt.GridBagConstraints.*;

/**
 * Internal class to allow setting of proxies within Spark.
 */
class ProxyLoginSettingsPanel extends JPanel
{
    private final static Insets DEFAULT_INSETS = new Insets( 5, 5, 5, 5 );
    private final LocalPreferences localPreferences;
    private JCheckBox useProxyBox = new JCheckBox();
    private JComboBox<String> protocolBox = new JComboBox<>();
    private JTextField hostField = new JTextField();
    private JTextField portField = new JTextField();
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JDialog optionsDialog;

    public ProxyLoginSettingsPanel( LocalPreferences localPreferences, JDialog optionsDialog )
    {
        this.localPreferences = localPreferences;
        this.optionsDialog = optionsDialog;
        final JLabel protocolLabel = new JLabel();
        final JLabel hostLabel = new JLabel();
        final JLabel portLabel = new JLabel();
        final JLabel usernameLabel = new JLabel();
        final JLabel passwordLabel = new JLabel();

        protocolBox.addItem( "SOCKS" );
        protocolBox.addItem( "HTTP" );

        ResourceUtils.resButton( useProxyBox, Res.getString( "checkbox.use.proxy.server" ) );
        ResourceUtils.resLabel( protocolLabel, protocolBox, Res.getString( "label.protocol" ) );
        ResourceUtils.resLabel( hostLabel, hostField, Res.getString( "label.host" ) );
        ResourceUtils.resLabel( portLabel, portField, Res.getString( "label.port" ) );
        ResourceUtils.resLabel( usernameLabel, usernameField, Res.getString( "label.username" ) );
        ResourceUtils.resLabel( passwordLabel, passwordField, Res.getString( "label.password" ) );

        setLayout( new GridBagLayout() );
        add( useProxyBox,   new GridBagConstraints( 0, 0, 2, 1, 1.0, 0.0, NORTHWEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( protocolLabel, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( protocolBox,   new GridBagConstraints( 1, 1, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( hostLabel,     new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, NORTHWEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( hostField,     new GridBagConstraints( 1, 2, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( portLabel,     new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, NORTHWEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( portField,     new GridBagConstraints( 1, 3, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( usernameLabel, new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, NORTHWEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( usernameField, new GridBagConstraints( 1, 4, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( passwordLabel, new GridBagConstraints( 0, 5, 1, 1, 0.0, 0.0, NORTHWEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( passwordField, new GridBagConstraints( 1, 5, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );

        useProxyBox.addActionListener( e -> enableFields( useProxyBox.isSelected() ) );

        // Check localSettings
        if ( localPreferences.isProxyEnabled() )
        {
            useProxyBox.setSelected( true );
        }

        enableFields( useProxyBox.isSelected() );

        if ( ModelUtil.hasLength( localPreferences.getHost() ) )
        {
            hostField.setText( localPreferences.getHost() );
        }

        if ( ModelUtil.hasLength( localPreferences.getPort() ) )
        {
            portField.setText( localPreferences.getPort() );
        }

        if ( ModelUtil.hasLength( localPreferences.getProxyPassword() ) )
        {
            passwordField.setText( localPreferences.getProxyPassword() );
        }

        if ( ModelUtil.hasLength( localPreferences.getProxyUsername() ) )
        {
            usernameField.setText( localPreferences.getProxyUsername() );
        }

        if ( ModelUtil.hasLength( localPreferences.getProtocol() ) )
        {
            protocolBox.setSelectedItem( localPreferences.getProtocol() );
        }

        if ( Default.getString( "PROXY_PROTOCOL" ).length() > 0 )
        {
            protocolBox.setSelectedItem( Default.getString( "PROXY_PROTOCOL" ) );
            protocolBox.setEnabled( false );
            useProxyBox.setSelected( true );
            useProxyBox.setVisible( false );
        }
        if ( Default.getString( "PROXY_HOST" ).length() > 0 )
        {
            hostField.setText( Default.getString( "PROXY_HOST" ) );
            hostField.setEnabled( false );
            useProxyBox.setSelected( true );
            useProxyBox.setVisible( false );
        }
        if ( Default.getString( "PROXY_PORT" ).length() > 0 )
        {
            portField.setText( Default.getString( "PROXY_PORT" ) );
            portField.setEnabled( false );
        }

    }

    /**
     * Enables the fields of the proxy panel.
     *
     * @param enable true if all fields should be enabled, otherwise false.
     */
    private void enableFields( boolean enable )
    {
        Component[] comps = getComponents();
        for ( Component comp1 : comps )
        {
            if ( comp1 instanceof JTextField || comp1 instanceof JComboBox )
            {
                JComponent comp = (JComponent) comp1;
                comp.setEnabled( enable );
            }
        }
    }

    /**
     * Returns the protocol to use for this proxy.
     *
     * @return the protocol.
     */
    public String getProtocol()
    {
        return (String) protocolBox.getSelectedItem();
    }

    /**
     * Returns the host to use for this proxy.
     *
     * @return the host.
     */
    public String getHost()
    {
        return hostField.getText();
    }

    /**
     * Returns the port to use with this proxy.
     *
     * @return the port to use.
     */
    public String getPort()
    {
        return portField.getText();
    }

    /**
     * Returns the username to use with this proxy.
     *
     * @return the username.
     */
    public String getUsername()
    {
        return usernameField.getText();
    }

    /**
     * Returns the password to use with this proxy.
     *
     * @return the password.
     */
    public String getPassword()
    {
        return new String( passwordField.getPassword() );
    }

    public boolean validate_settings()
    {
        boolean valid = true;

        UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );

        if ( useProxyBox.isSelected() )
        {
            try
            {
                Integer.valueOf( portField.getText() );
            }
            catch ( NumberFormatException numberFormatException )
            {
                JOptionPane.showMessageDialog( optionsDialog,
                        Res.getString( "message.supply.valid.port" ),
                        Res.getString( "title.error" ),
                        JOptionPane.ERROR_MESSAGE );
                portField.requestFocus();
                valid = false;
            }

            if ( !ModelUtil.hasLength( hostField.getText() ) )
            {
                JOptionPane.showMessageDialog( optionsDialog,
                        Res.getString( "message.supply.valid.host" ),
                        Res.getString( "title.error" ),
                        JOptionPane.ERROR_MESSAGE );
                hostField.requestFocus();
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Persist the proxy settings to local preferences.
     */
    public void saveSettings()
    {
        localPreferences.setProxyEnabled( useProxyBox.isSelected() );

        if ( ModelUtil.hasLength( getProtocol() ) )
        {
            localPreferences.setProtocol( getProtocol() );
        }

        if ( ModelUtil.hasLength( getHost() ) )
        {
            localPreferences.setHost( getHost() );
        }

        if ( ModelUtil.hasLength( getPort() ) )
        {
            localPreferences.setPort( getPort() );
        }

        if ( getUsername().equals( "" ) || getUsername() == null )
        {
            localPreferences.setProxyUsername( "" );
        }

        if ( ModelUtil.hasLength( getUsername() ) )
        {
            localPreferences.setProxyUsername( getUsername() );
        }

        if ( getPassword().equals( "" ) || getPassword() == null )
        {
            localPreferences.setProxyPassword( "" );
        }

        if ( ModelUtil.hasLength( getPassword() ) )
        {
            localPreferences.setProxyPassword( getPassword() );
        }

        if ( !localPreferences.isProxyEnabled() )
        {
            Properties props = System.getProperties();
            props.remove( "socksProxyHost" );
            props.remove( "socksProxyPort" );
            props.remove( "http.proxyHost" );
            props.remove( "http.proxyPort" );
            props.remove( "http.proxySet" );
        }
        else
        {
            String host = localPreferences.getHost();
            String port = localPreferences.getPort();
            String protocol = localPreferences.getProtocol();

            boolean isValid = ModelUtil.hasLength( host ) && ModelUtil.hasLength( port );

            if ( isValid )
            {
                if ( protocol.equals( "SOCKS" ) )
                {
                    System.setProperty( "socksProxyHost", host );
                    System.setProperty( "socksProxyPort", port );
                }
                else
                {
                    System.setProperty( "http.proxySet", "true" );

                    // Set https settings
                    System.setProperty( "https.proxyHost", host );
                    System.setProperty( "https.proxyPort", port );

                    // Set http settings
                    System.setProperty( "http.proxyHost", host );
                    System.setProperty( "http.proxyPort", port );
                }
            }
            else
            {
                localPreferences.setProxyEnabled( false );
            }
        }
        SettingsManager.saveSettings();
    }
}
