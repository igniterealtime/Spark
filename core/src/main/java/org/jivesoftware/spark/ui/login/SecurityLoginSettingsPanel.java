/*
 * Copyright (c) 2017 Ignite Realtime Foundation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.*;

/**
 * Allows users to configure security-related settings.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class SecurityLoginSettingsPanel extends JPanel
{
    private final static Insets DEFAULT_INSETS = new Insets( 5, 5, 5, 5 );

    private final JDialog optionsDialog;
    private final LocalPreferences localPreferences;

    // Radio buttons that configure the 'mode' of encryption.
    private final JRadioButton modeRequiredRadio;
    private final JRadioButton modeIfPossibleRadio;
    private final JRadioButton modeDisabledRadio;

    // Checkbox that toggles between 'old' style Direct TLS (socket encryption, typically on port 5223), or STARTTLS. A check indicates 'old' behavior.
    private final JCheckBox useDirectTlsBox;

    private final JCheckBox disableHostnameVerificationBox;
    private final JCheckBox allowClientSideAuthentication;

    private final JButton deleteSavedPasswords;

    public SecurityLoginSettingsPanel( LocalPreferences localPreferences, JDialog optionsDialog )
    {
        this.localPreferences = localPreferences;
        this.optionsDialog = optionsDialog;

        setLayout( new GridBagLayout() );

        // The titled-border panel labeled 'encryption mode'
        final JPanel encryptionModePanel = new JPanel();
        encryptionModePanel.setLayout( new GridBagLayout() );
        encryptionModePanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "group.encryption_mode" ) ) );

        // The radio buttons that config the 'encryption mode'
        modeRequiredRadio = new JRadioButton();
        modeRequiredRadio.setToolTipText( Res.getString( "tooltip.encryptionmode.required" ) );
        modeIfPossibleRadio = new JRadioButton();
        modeIfPossibleRadio.setToolTipText( Res.getString( "tooltip.encryptionmode.ifpossible" ) );
        modeDisabledRadio = new JRadioButton();
        modeDisabledRadio.setToolTipText( Res.getString( "tooltip.encryptionmode.disabled" ) );

        useDirectTlsBox = new JCheckBox();
        disableHostnameVerificationBox = new JCheckBox();
        allowClientSideAuthentication  = new JCheckBox();

        deleteSavedPasswords = new JButton();
        deleteSavedPasswords.setEnabled( localPreferences.hasStoredPasswords() );
        
        // .. Set labels/text for all the components.
        ResourceUtils.resButton( modeRequiredRadio,              Res.getString( "radio.encryptionmode.required" ) );
        ResourceUtils.resButton( modeIfPossibleRadio,            Res.getString( "radio.encryptionmode.ifpossible" ) );
        ResourceUtils.resButton( modeDisabledRadio,              Res.getString( "radio.encryptionmode.disabled" ) );
        ResourceUtils.resButton( useDirectTlsBox,                Res.getString( "label.old.ssl" ) );
        ResourceUtils.resButton( disableHostnameVerificationBox, Res.getString( "checkbox.disable.hostname.verification" ) );
        ResourceUtils.resButton( allowClientSideAuthentication,  Res.getString( "checkbox.allow.client.side.authentication" ) );
        ResourceUtils.resButton( deleteSavedPasswords,           Res.getString( "button.delete.saved.passwords" ) );

        // ... add the radio buttons to a group to make them interdependent.
        final ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add( modeRequiredRadio );
        modeGroup.add( modeIfPossibleRadio );
        modeGroup.add( modeDisabledRadio );

        // ... add event handler that disables the UI of encryption-related config, when encryption itself is disabled.
        modeDisabledRadio.addChangeListener( e -> {
            final boolean encryptionPossible = !modeDisabledRadio.isSelected();
            useDirectTlsBox.setEnabled( encryptionPossible );
            disableHostnameVerificationBox.setEnabled( encryptionPossible );
            allowClientSideAuthentication.setEnabled( encryptionPossible );
        } );

        // ... apply the correct state, either based on saves settings, or defaults.
        modeRequiredRadio.setSelected( localPreferences.getSecurityMode() == ConnectionConfiguration.SecurityMode.required );
        modeIfPossibleRadio.setSelected( localPreferences.getSecurityMode() == ConnectionConfiguration.SecurityMode.ifpossible );
        modeDisabledRadio.setSelected( localPreferences.getSecurityMode() == ConnectionConfiguration.SecurityMode.disabled );
        useDirectTlsBox.setSelected( localPreferences.isDirectTls() );
        disableHostnameVerificationBox.setSelected( localPreferences.isDisableHostnameVerification() );
        allowClientSideAuthentication.setSelected(true);

        // ... register click-handler that deletes stored passwords.
        deleteSavedPasswords.addActionListener(actionEvent -> {
            SettingsManager.getLocalPreferences().clearPasswordForAllUsers();
            deleteSavedPasswords.setEnabled(false);
        });

        // ... place the components on the titled-border panel.
        encryptionModePanel.add( modeRequiredRadio,   new GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        encryptionModePanel.add( modeIfPossibleRadio, new GridBagConstraints( 0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        encryptionModePanel.add( modeDisabledRadio,   new GridBagConstraints( 0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        encryptionModePanel.add( useDirectTlsBox,     new GridBagConstraints( 0, 3, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );

        // ... place the titled-border panel on the global panel.
        add( encryptionModePanel,            new GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );

        // ... place the other components under the titled-border panel.
        add( disableHostnameVerificationBox, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( allowClientSideAuthentication,  new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( deleteSavedPasswords,           new GridBagConstraints( 0, 3, 1, 1, 0.0, 1.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
    }

    public boolean validate_settings()
    {
        return true;
    }

    public void useDefault() {
        modeRequiredRadio.setSelected(Default.getString(Default.SECURITY_MODE).equals("required"));
        modeIfPossibleRadio.setSelected(Default.getString(Default.SECURITY_MODE).equals("ifpossible"));
        modeDisabledRadio.setSelected(Default.getString(Default.SECURITY_MODE).equals("disabled"));
        disableHostnameVerificationBox.setSelected(Default.getBoolean(Default.DISABLE_HOSTNAME_VERIFICATION));
        allowClientSideAuthentication.setSelected(Default.getBoolean(Default.ALLOW_CLIENT_SIDE_AUTH));
        useDirectTlsBox.setSelected(Default.getBoolean(Default.OLD_SSL_ENABLED));
    }
    
    public void saveSettings()
    {
        if ( modeRequiredRadio.isSelected() )
        {
            localPreferences.setSecurityMode( ConnectionConfiguration.SecurityMode.required );
        }
        if ( modeIfPossibleRadio.isSelected() )
        {
            localPreferences.setSecurityMode( ConnectionConfiguration.SecurityMode.ifpossible );
        }
        if ( modeDisabledRadio.isSelected() )
        {
            localPreferences.setSecurityMode( ConnectionConfiguration.SecurityMode.disabled );
        }
        localPreferences.setDirectTls( useDirectTlsBox.isSelected() );
        localPreferences.setDisableHostnameVerification( disableHostnameVerificationBox.isSelected() );
        localPreferences.setAllowClientSideAuthentication( allowClientSideAuthentication.isSelected() );
    }
}
