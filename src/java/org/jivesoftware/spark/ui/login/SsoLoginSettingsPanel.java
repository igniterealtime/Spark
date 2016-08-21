package org.jivesoftware.spark.ui.login;

import org.jivesoftware.resource.Res;
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
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Principal;

import static java.awt.GridBagConstraints.*;

/**
 * Internal class to set SSO settings
 */
class SsoLoginSettingsPanel extends JPanel implements ActionListener
{
    private final static Insets DEFAULT_INSETS = new Insets( 5, 5, 5, 5 );
    private final LocalPreferences localPreferences;
    private JCheckBox useSSOBox = new JCheckBox();
    private JTextField realmField = new JTextField();
    private JTextField kdcField = new JTextField();
    private JRadioButton methodFileRadio = new JRadioButton();
    private JRadioButton methodDNSRadio = new JRadioButton();
    private JRadioButton methodManualRadio = new JRadioButton();
    private JCheckBox useSaslGssapiSmack3compatBox = new JCheckBox();
    private JDialog optionsDialog;

    public SsoLoginSettingsPanel( LocalPreferences localPreferences, JDialog optionsDialog )
    {
        this.localPreferences = localPreferences;
        this.optionsDialog = optionsDialog;

        ResourceUtils.resButton( useSSOBox, Res.getString( "title.advanced.connection.usesso" ) );
        useSSOBox.addActionListener( this );
        ResourceUtils.resButton( useSaslGssapiSmack3compatBox, Res.getString( "title.advanced.connection.sso.smack3compat" ) );
        useSaslGssapiSmack3compatBox.addActionListener( this );

        final WrappedLabel wrappedLabel = new WrappedLabel();
        String principalName = null;
        try
        {
            principalName = getPrincipalName();
        }
        catch ( Exception e )
        {
            // Ignore
        }

        if ( ModelUtil.hasLength( principalName ) )
        {
            wrappedLabel.setText( Res.getString( "title.advanced.connection.sso.account", principalName ) );
        }
        else
        {
            wrappedLabel.setForeground( Color.RED );
            wrappedLabel.setText( Res.getString( "title.advanced.connection.sso.noprincipal" ) );
        }
        wrappedLabel.setBackground( Color.white );

        final String method = localPreferences.getSSOMethod();
        if ( ModelUtil.hasLength( method ) )
        {
            switch ( method )
            {
                case "file":
                    methodFileRadio.setSelected( true );
                    break;
                case "dns":
                    methodDNSRadio.setSelected( true );
                    break;
                case "manual":
                    methodManualRadio.setSelected( true );
                    break;
                default:
                    methodFileRadio.setSelected( true );
                    break;
            }
        }
        else
        {
            methodFileRadio.setSelected( true );
        }

        if ( ModelUtil.hasLength( localPreferences.getSSORealm() ) )
        {
            realmField.setText( localPreferences.getSSORealm() );
        }
        if ( ModelUtil.hasLength( localPreferences.getSSOKDC() ) )
        {
            kdcField.setText( localPreferences.getSSOKDC() );
        }

        final JLabel methodFileLabel = new JLabel();
        final JLabel methodDNSLabel = new JLabel();
        final JLabel methodManualLabel = new JLabel();
        final JLabel realmLabel = new JLabel();
        final JLabel kdcLabel = new JLabel();

        ResourceUtils.resLabel( methodFileLabel,   methodFileRadio,   Res.getString( "checkbox.use.krbconf" ) );
        ResourceUtils.resLabel( methodDNSLabel,    methodDNSRadio,    Res.getString( "checkbox.use.krb.dns" ) );
        ResourceUtils.resLabel( methodManualLabel, methodManualRadio, Res.getString( "checkbox.use.specify.below" ) );
        ResourceUtils.resLabel( realmLabel,        realmField,        Res.getString( "label.krb.realm" ) );
        ResourceUtils.resLabel( kdcLabel,          kdcField,          Res.getString( "label.krb.kdc" ) );

        final ButtonGroup ssoMethodRadio = new ButtonGroup();
        ssoMethodRadio.add( methodFileRadio );
        ssoMethodRadio.add( methodDNSRadio );
        ssoMethodRadio.add( methodManualRadio );

        setLayout( new GridBagLayout() );
        add( wrappedLabel,                 new GridBagConstraints( 0, 0, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 10 ) );
        add( useSSOBox,                    new GridBagConstraints( 0, 1, 3, 1, 1.0, 0.0, WEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( methodFileRadio,              new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( methodFileLabel,              new GridBagConstraints( 1, 2, 2, 1, 1.0, 0.0, WEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( methodDNSRadio,               new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( methodDNSLabel,               new GridBagConstraints( 1, 3, 2, 1, 1.0, 0.0, WEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( methodManualRadio,            new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( methodManualLabel,            new GridBagConstraints( 1, 4, 2, 1, 1.0, 0.0, WEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( realmLabel,                   new GridBagConstraints( 1, 5, 1, 1, 0.0, 0.0, WEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( realmField,                   new GridBagConstraints( 2, 5, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( kdcLabel,                     new GridBagConstraints( 1, 6, 1, 1, 0.0, 0.0, WEST, NONE,       DEFAULT_INSETS, 0, 0 ) );
        add( kdcField,                     new GridBagConstraints( 2, 6, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( useSaslGssapiSmack3compatBox, new GridBagConstraints( 0, 7, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );

        useSSOBox.setSelected( localPreferences.isSSOEnabled() );
        useSaslGssapiSmack3compatBox.setSelected( localPreferences.isSaslGssapiSmack3Compatible() );

        setFormEnabled( useSSOBox.isSelected() );
    }

    /**
     * Returns the principal name if one exists.
     *
     * @return the name (ex. derek) of the principal.
     * @throws Exception thrown if a Principal was not found.
     */
    private String getPrincipalName() throws Exception
    {
        if ( localPreferences.getDebug() )
        {
            System.setProperty( "java.security.krb5.debug", "true" );
        }
        System.setProperty( "javax.security.auth.useSubjectCredsOnly", "false" );
        GSSAPIConfiguration config = new GSSAPIConfiguration( false );
        Configuration.setConfiguration( config );

        LoginContext lc;
        try
        {
            lc = new LoginContext( "com.sun.security.jgss.krb5.initiate" );
            lc.login();
        }
        catch ( LoginException le )
        {
            Log.debug( le.getMessage() );
            return null;
        }

        Subject mySubject = lc.getSubject();

        for ( Principal p : mySubject.getPrincipals() )
        {
            String name = p.getName();
            int indexOne = name.indexOf( "@" );
            if ( indexOne != -1 )
            {
                return name;
            }
        }
        return null;
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( e.getSource() == useSSOBox )
        {
            setFormEnabled( useSSOBox.isSelected() );
        }
    }

    private void setFormEnabled( boolean enabled )
    {
        methodFileRadio.setEnabled( enabled );
        methodDNSRadio.setEnabled( enabled );
        methodManualRadio.setEnabled( enabled );
        realmField.setEnabled( enabled );
        kdcField.setEnabled( enabled );
        useSaslGssapiSmack3compatBox.setEnabled( enabled );
    }

    public boolean validate_settings()
    {
        boolean valid = true;
        UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );

        if ( useSSOBox.isSelected() )
        {
            if ( methodManualRadio.isSelected() )
            {
                if ( !ModelUtil.hasLength( realmField.getText() ) )
                {
                    JOptionPane.showMessageDialog( optionsDialog,
                            "You must specify a realm",
                            Res.getString( "title.error" ),
                            JOptionPane.ERROR_MESSAGE );
                    realmField.requestFocus();
                    valid = false;
                }
                if ( !ModelUtil.hasLength( kdcField.getText() ) )
                {
                    JOptionPane.showMessageDialog( optionsDialog,
                            "You must specify a KDC",
                            Res.getString( "title.error" ),
                            JOptionPane.ERROR_MESSAGE );
                    kdcField.requestFocus();
                    valid = false;
                }
            }
        }

        return valid;
    }

    public void saveSettings()
    {
        localPreferences.setSSOEnabled( useSSOBox.isSelected() );
        if ( methodFileRadio.isSelected() )
        {
            localPreferences.setSSOMethod( "file" );
        }
        else if ( methodDNSRadio.isSelected() )
        {
            localPreferences.setSSOMethod( "dns" );
        }
        else if ( methodManualRadio.isSelected() )
        {
            localPreferences.setSSOMethod( "manual" );
            localPreferences.setSSORealm( realmField.getText() );
            localPreferences.setSSOKDC( kdcField.getText() );
        }
        else
        {
            localPreferences.setSSOMethod( "file" );
        }
        localPreferences.setSaslGssapiSmack3Compatible( useSaslGssapiSmack3compatBox.isSelected() );
        SettingsManager.saveSettings();
    }
}
