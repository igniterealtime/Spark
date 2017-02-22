package org.jivesoftware.spark.ui.login;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static java.awt.GridBagConstraints.*;

/**
 * Internal class to set PKI settings
 */

class PkiLoginSettingsPanel extends JPanel implements ActionListener
{
    private final static Insets DEFAULT_INSETS = new Insets( 5, 5, 5, 5 );
    private final LocalPreferences localPreferences;
    private JCheckBox usePKIBox = new JCheckBox();
    private JComboBox<String> pkiStore = new JComboBox<>();
    private JFileChooser fileChooser = new JFileChooser();
    private JButton fileButton = new JButton();
    private JTextField fileField = new JTextField();
    private JPanel filePanel = new JPanel();
    private JPasswordField trustStorePassword = new JPasswordField();
    private JTextField trustStoreField = new JTextField();
    private JButton trustStoreButton = new JButton();
    private JDialog optionsDialog;

    public PkiLoginSettingsPanel( LocalPreferences localPreferences, JDialog optionsDialog )
    {
        this.localPreferences = localPreferences;
        this.optionsDialog = optionsDialog;
        ResourceUtils.resButton( usePKIBox, Res.getString( "checkbox.use.pki.authentication" ) );
        JLabel pkiStoreLabel = new JLabel();
        ResourceUtils.resLabel( pkiStoreLabel, pkiStore, Res.getString( "label.which.pki.method" ) );
        ResourceUtils.resButton( fileButton, Res.getString( "label.choose.file" ) );
        ResourceUtils.resButton( trustStoreButton, Res.getString( "label.choose.file" ) );
        JLabel trustStorePasswordLabel = new JLabel();
        ResourceUtils.resLabel( trustStorePasswordLabel, trustStorePassword, Res.getString( "label.trust.store.password" ) );

        pkiStore.addItem( "Java Keystore" );
        pkiStore.addItem( "PKCS#11" );
        // pkiStore.addItem("X.509 PEM File");
        pkiStore.addItem( "Apple KeyChain" );

        usePKIBox.setSelected( localPreferences.isPKIEnabled() );

        if ( ModelUtil.hasLength( localPreferences.getPKIStore() ) )
        {
            if ( localPreferences.getPKIStore().equals( "PKCS11" ) )
            {
                pkiStore.setSelectedItem( "PKCS#11" );
                if ( ModelUtil.hasLength( localPreferences.getPKCS11Library() ) )
                {
                    fileField.setText( localPreferences.getPKCS11Library() );
                }
                else
                {
                    fileField.setText( "" );
                }
            }
            else if ( localPreferences.getPKIStore().equals( "X509" ) )
            {
                pkiStore.setSelectedItem( "X.509 PEM File" );
                // if(ModelUtil.hasLength(localPreferences.getPEMFile())) {
                // fileField.setText(localPreferences.getPEMFile());
                // }
                // else {
                fileField.setText( "" );
                // }
            }
            else if ( localPreferences.getPKIStore().equals( "Apple KeyChain" ) )
            {
                fileField.setText( "" );
            }
            else
            {
                pkiStore.setSelectedItem( "Java Keystore" );
                if ( ModelUtil.hasLength( localPreferences.getJKSPath() ) )
                {
                    fileField.setText( localPreferences.getJKSPath() );
                }
                else
                {
                    fileField.setText( "" );
                }
            }
        }
        else
        {
            pkiStore.setSelectedItem( "Java Keystore" );
            if ( ModelUtil.hasLength( localPreferences.getJKSPath() ) )
            {
                fileField.setText( localPreferences.getJKSPath() );
            }
            else
            {
                fileField.setText( "" );
            }
        }

        if ( ModelUtil.hasLength( localPreferences.getTrustStorePath() ) )
        {
            trustStoreField.setText( localPreferences.getTrustStorePath() );
        }

        if ( ModelUtil.hasLength( localPreferences.getTrustStorePassword() ) )
        {
            trustStorePassword.setText( localPreferences.getTrustStorePassword() );
        }

        pkiStore.setEnabled( usePKIBox.isSelected() );
        filePanel.setEnabled( usePKIBox.isSelected() );
        fileField.setEnabled( usePKIBox.isSelected() );
        fileButton.setEnabled( usePKIBox.isSelected() );

        setLayout( new GridBagLayout() );

        JLabel usePKILabel = new JLabel();

        add( usePKIBox,     new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, NORTHWEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        add( usePKILabel,   new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, NORTHWEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        add( pkiStoreLabel, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        add( pkiStore,      new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, DEFAULT_INSETS, 0, 0 ) );

        filePanel.setLayout( new GridBagLayout() );
        filePanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "label.keystore.location" ) ) );
        filePanel.add( fileField,  new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 100, 0 ) );
        filePanel.add( fileButton, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, NORTHWEST, NONE, DEFAULT_INSETS, 0, 0 ) );

        add( filePanel,     new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 150, 0 ) );

        JPanel trustStorePanel = new JPanel();
        trustStorePanel.setLayout( new GridBagLayout() );
        trustStorePanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "label.truststore.location" ) ) );
        trustStorePanel.add( trustStoreField,         new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 100, 0 ) );
        trustStorePanel.add( trustStoreButton,        new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, NORTHWEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        trustStorePanel.add( trustStorePasswordLabel, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        trustStorePanel.add( trustStorePassword,      new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 100, 0 ) );

        add( trustStorePanel, new GridBagConstraints( 0, 3, 2, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 150, 0 ) );

        usePKIBox.addActionListener( this );
        pkiStore.addActionListener( this );
        fileButton.addActionListener( this );
        trustStoreButton.addActionListener( this );
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( e.getSource() == usePKIBox )
        {
            pkiStore.setEnabled( usePKIBox.isSelected() );
            filePanel.setEnabled( usePKIBox.isSelected() );
            fileField.setEnabled( usePKIBox.isSelected() );
            fileButton.setEnabled( usePKIBox.isSelected() );
        }
        else if ( e.getSource() == pkiStore )
        {
            if ( ( pkiStore.getSelectedItem() ).equals( "PKCS#11" ) )
            {
                filePanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "label.pkcs.library.file" ) ) );
                if ( ModelUtil.hasLength( localPreferences.getPKCS11Library() ) )
                {
                    fileField.setText( localPreferences.getPKCS11Library() );
                }
                else
                {
                    fileField.setText( "" );
                }
            }
            else if ( ( pkiStore.getSelectedItem() )
                    .equals( "X.509 PEM File" ) )
            {
                filePanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "label.x509.certificate" ) ) );
                // if(ModelUtil.hasLength(localPreferences.getPEMFile())) {
                // fileField.setText(localPreferences.getPEMFile());
                // } else {
                // fileField.setText("");
                // }
            }
            else if ( ( pkiStore.getSelectedItem() )
                    .equals( "Apple KeyChain" ) )
            {
                filePanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "label.apple.keychain" ) ) );
            }
            else
            {
                filePanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "label.keystore.location" ) ) );
                if ( ModelUtil.hasLength( localPreferences.getJKSPath() ) )
                {
                    fileField.setText( localPreferences.getJKSPath() );
                }
                else
                {
                    fileField.setText( "" );
                }
            }
        }
        else if ( e.getSource() == fileButton )
        {
            int retval = fileChooser.showOpenDialog( this );
            if ( retval == JFileChooser.APPROVE_OPTION )
            {
                File file = fileChooser.getSelectedFile();
                fileField.setText( file.getAbsolutePath() );
            }
        }
        else if ( e.getSource() == trustStoreButton )
        {
            int retVal = fileChooser.showOpenDialog( this );
            if ( retVal == JFileChooser.APPROVE_OPTION )
            {
                File file = fileChooser.getSelectedFile();
                trustStoreField.setText( file.getAbsolutePath() );
            }
        }
    }

    public boolean validate_settings()
    {

        boolean valid = true;
        UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );

        if ( usePKIBox.isSelected() )
        {
            if ( !ModelUtil.hasLength( fileField.getText() ) )
            {
                JOptionPane.showMessageDialog( optionsDialog,
                        "You must specify a file location",
                        Res.getString( "title.error" ),
                        JOptionPane.ERROR_MESSAGE );
                fileField.requestFocus();
                valid = false;
            }
        }
        return valid;
    }

    public void saveSettings()
    {

        localPreferences.setPKIEnabled( usePKIBox.isSelected() );
        localPreferences.setPKIStore( (String) pkiStore.getSelectedItem() );
        if ( ( pkiStore.getSelectedItem() ).equals( "PKCS#11" ) )
        {
            localPreferences.setPKIStore( "PKCS11" );
            localPreferences.setPKCS11Library( fileField.getText() );
        }
        else if ( ( pkiStore.getSelectedItem() ).equals( "X.509 Certificate" ) )
        {
            localPreferences.setPKIStore( "X509" );
            // localPreferences.setPEMFile(fileField.getText());
        }
        else if ( ( pkiStore.getSelectedItem() ).equals( "Apple KeyChain" ) )
        {
            localPreferences.setPKIStore( "Apple" );
        }
        else
        {
            localPreferences.setPKIStore( "JKS" );
            localPreferences.setJKSPath( fileField.getText() );
        }
        localPreferences.setTrustStorePath( trustStoreField.getText() );
        localPreferences.setTrustStorePassword( new String(
                trustStorePassword.getPassword() ) );
        SettingsManager.saveSettings();
    }
}
