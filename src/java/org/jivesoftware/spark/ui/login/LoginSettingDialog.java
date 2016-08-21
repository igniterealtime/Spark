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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.ui.login;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Allows users to configure startup options.
 *
 * @author Derek DeMoro
 * @author Jay Kline
 */
public class LoginSettingDialog implements PropertyChangeListener
{
    private JDialog optionsDialog;
    private JOptionPane optionPane;

    private GeneralLoginSettingsPanel generalPanel;
    private ProxyLoginSettingsPanel proxyPanel;
    private PkiLoginSettingsPanel pkiPanel;
    private SsoLoginSettingsPanel ssoPanel;

    /**
     * Empty Constructor.
     */
    public LoginSettingDialog()
    {
        LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
        generalPanel = new GeneralLoginSettingsPanel( localPreferences, optionsDialog );
        proxyPanel = new ProxyLoginSettingsPanel( localPreferences, optionsDialog );
        ssoPanel = new SsoLoginSettingsPanel( localPreferences, optionsDialog );
        pkiPanel = new PkiLoginSettingsPanel( localPreferences, optionsDialog );
    }

    /**
     * Invokes the OptionsDialog.
     *
     * @param owner the parent owner of this dialog. This is used for correct
     *              parenting.
     * @return true if the options have been changed.
     */
    public boolean invoke( JFrame owner )
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel( Res.getString( "title.advanced.connection.preferences" ), "", SparkRes.getImageIcon( SparkRes.BLANK_24x24 ), true );
        tabbedPane.addTab( Res.getString( "tab.general" ), generalPanel );
        if ( !Default.getBoolean( Default.PROXY_DISABLED ) )
        {
            tabbedPane.addTab( Res.getString( "tab.proxy" ), proxyPanel );
        }
        if ( !Default.getBoolean( Default.SSO_DISABLED ) )
        {
            tabbedPane.addTab( Res.getString( "tab.sso" ), ssoPanel );
        }
        if ( !Default.getBoolean( Default.PKI_DISABLED ) )
        {
            tabbedPane.addTab( Res.getString( "tab.pki" ), pkiPanel );
        }

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add( titlePanel, BorderLayout.NORTH );

        // The user should only be able to close this dialog.
        Object[] options = { Res.getString( "ok" ), Res.getString( "cancel" ), Res.getString( "use.default" ) };
        optionPane = new JOptionPane( tabbedPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[ 0 ] );

        mainPanel.add( optionPane, BorderLayout.CENTER );

        optionsDialog = new JDialog( owner, Res.getString( "title.preferences" ), true );
        optionsDialog.setContentPane( mainPanel );
        optionsDialog.pack();

        optionsDialog.setLocationRelativeTo( owner );
        optionPane.addPropertyChangeListener( this );

        optionsDialog.setResizable( false );
        optionsDialog.setVisible( true );
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
    public void propertyChange( PropertyChangeEvent e )
    {
        String value = (String) optionPane.getValue();
        if ( Res.getString( "cancel" ).equals( value ) )
        {
            optionsDialog.setVisible( false );
        }
        else if ( Res.getString( "ok" ).equals( value ) )
        {

            boolean valid = generalPanel.validate_settings();
            valid = valid && proxyPanel.validate_settings();
            valid = valid && ssoPanel.validate_settings();
            valid = valid && pkiPanel.validate_settings();

            if ( valid )
            {
                generalPanel.saveSettings();
                proxyPanel.saveSettings();
                ssoPanel.saveSettings();
                pkiPanel.saveSettings();
                SettingsManager.saveSettings();
                optionsDialog.setVisible( false );
            }
            else
            {
                optionPane.removePropertyChangeListener( this );
                optionPane.setValue( JOptionPane.UNINITIALIZED_VALUE );
                optionPane.addPropertyChangeListener( this );
            }
        }
        else
        {
            // Some unknown operation happened
            optionPane.setValue( JOptionPane.UNINITIALIZED_VALUE );
        }
    }
}
