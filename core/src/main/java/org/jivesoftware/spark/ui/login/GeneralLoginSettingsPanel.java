package org.jivesoftware.spark.ui.login;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.awt.GridBagConstraints.*;

/**
 * Internal class to set General settings
 */
class GeneralLoginSettingsPanel extends JPanel implements ActionListener
{
    private final static Insets DEFAULT_INSETS = new Insets( 5, 5, 5, 5 );
    private final LocalPreferences localPreferences;
    private final JDialog optionsDialog;
    private final JCheckBox autoDiscoverBox = new JCheckBox();
    private final JTextField portField = new JTextField();
    private final JTextField xmppHostField = new JTextField();
    private final JTextField timeOutField = new JTextField();
    private final JTextField resourceField = new JTextField();
    private final JCheckBox useHostnameAsResourceBox = new JCheckBox();
    private final JCheckBox useVersionAsResourceBox = new JCheckBox();
    private final JCheckBox compressionBox = new JCheckBox();
    private final JCheckBox debuggerBox = new JCheckBox();

    public GeneralLoginSettingsPanel( LocalPreferences localPreferences, JDialog optionsDialog )
    {
        this.localPreferences = localPreferences;
        this.optionsDialog = optionsDialog;
        JLabel portLabel = new JLabel();
        ResourceUtils.resLabel( portLabel, portField, Res.getString( "label.port" ) );
        JLabel timeOutLabel = new JLabel();
        ResourceUtils.resLabel( timeOutLabel, timeOutField, Res.getString( "label.response.timeout" ) );
        JCheckBox autoLoginBox = new JCheckBox();
        ResourceUtils.resButton( autoLoginBox, Res.getString( "label.auto.login" ) );
        JLabel xmppHostLabel = new JLabel();
        ResourceUtils.resLabel( xmppHostLabel, xmppHostField, Res.getString( "label.host" ) );
        ResourceUtils.resButton( autoDiscoverBox, Res.getString( "checkbox.auto.discover.port" ) );
        JLabel resourceLabel = new JLabel();
        ResourceUtils.resLabel( resourceLabel, resourceField, Res.getString( "label.resource" ) );
        ResourceUtils.resButton( useHostnameAsResourceBox, Res.getString( "checkbox.use.hostname.as.resource" ) );
        ResourceUtils.resButton( useVersionAsResourceBox, Res.getString( "checkbox.use.version.as.resource" ) );
        ResourceUtils.resButton( compressionBox, Res.getString( "checkbox.use.compression" ) );
        ResourceUtils.resButton( debuggerBox, Res.getString( "checkbox.use.debugger.on.startup" ) );
        portField.setText( Integer.toString( localPreferences.getXmppPort() ) );
        timeOutField.setText( Integer.toString( localPreferences.getTimeOut() ) );
        autoLoginBox.setSelected( localPreferences.isAutoLogin() );
        xmppHostField.setText( localPreferences.getXmppHost() );
        resourceField.setText( localPreferences.getResource() );

        useHostnameAsResourceBox.addActionListener( this );
        useHostnameAsResourceBox.setSelected( localPreferences.isUseHostnameAsResource() );
        updateResourceHostname();

        useVersionAsResourceBox.addActionListener( this );
        useVersionAsResourceBox.setSelected( localPreferences.isUseVersionAsResource() );
        updateResourceVersion();

        resourceField.setEnabled(!(useHostnameAsResourceBox.isSelected() || useVersionAsResourceBox.isSelected()));

        autoDiscoverBox.addActionListener( this );

        autoDiscoverBox.setSelected( !localPreferences.isHostAndPortConfigured() );
        updateAutoDiscovery();

        compressionBox.setSelected( localPreferences.isCompressionEnabled() );

        debuggerBox.setSelected( localPreferences.isDebuggerEnabled() );

        final JPanel connectionPanel = new JPanel();
        connectionPanel.setLayout( new GridBagLayout() );
        connectionPanel.setBorder( BorderFactory.createTitledBorder( Res.getString( "group.connection" ) ) );

        setLayout( new GridBagLayout() );

        add( autoDiscoverBox, new GridBagConstraints( 0, 0, 2, 1, 1.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0 ) );

        connectionPanel.add( xmppHostLabel, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        connectionPanel.add( xmppHostField, new GridBagConstraints( 1, 0, 1, 1, 1.0, 0.0, WEST, NONE, DEFAULT_INSETS, 200, 0 ) );
        connectionPanel.add( portLabel,     new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        connectionPanel.add( portField,     new GridBagConstraints( 1, 1, 1, 1, 1.0, 0.0, WEST, NONE, DEFAULT_INSETS, 50, 0 ) );

        add( connectionPanel, new GridBagConstraints( 0, 1, 3, 1, 1.0, 0.0, NORTHWEST, BOTH, DEFAULT_INSETS, 0, 0 ) );

        if ( Default.getBoolean( Default.USE_HOSTNAME_AS_RESOURCE ) == Default.getBoolean( Default.USE_VERSION_AS_RESOURCE ) )
        {
            add( resourceLabel,            new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0 ) );
            add( resourceField,            new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 100, 0 ) );
            add( useHostnameAsResourceBox, new GridBagConstraints( 0, 3, 2, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0 ) );
            add( useVersionAsResourceBox,  new GridBagConstraints( 0, 4, 2, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        }
        add( timeOutLabel,             new GridBagConstraints( 0, 5, 1, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0 ) );
        add( timeOutField,             new GridBagConstraints( 1, 5, 1, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 50, 0 ) );
        add( compressionBox,           new GridBagConstraints( 0, 6, 2, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
        add( debuggerBox,              new GridBagConstraints( 0, 7, 2, 1, 0.0, 1.0, NORTHWEST, HORIZONTAL, DEFAULT_INSETS, 0, 0 ) );
    }

    /**
     * Updates local preferences with auto discovery settings.
     */
    private void updateAutoDiscovery()
    {
        boolean isSelected = autoDiscoverBox.isSelected();
        xmppHostField.setEnabled( !isSelected );
        portField.setEnabled( !isSelected );
        localPreferences.setHostAndPortConfigured( !isSelected );
    }

    /**
     * Updates resource settings.
     */
    private void updateResourceHostname()
    {
        boolean isSelected = useHostnameAsResourceBox.isSelected();
        try
        {
            if ( isSelected )
            {
                String resource = InetAddress.getLocalHost().getHostName();
                resourceField.setText( resource );
                useVersionAsResourceBox.setSelected( false );
            }
            resourceField.setEnabled( !isSelected );
        }
        catch ( UnknownHostException e )
        {
            UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );
            JOptionPane.showMessageDialog( optionsDialog,
                            Res.getString( "message.unable.to.use.hostname.as.resource" ),
                            Res.getString( "title.error" ),
                            JOptionPane.ERROR_MESSAGE );
        }
        //localPreferences.setHostAndPortConfigured(!isSelected);
    }

    private void updateResourceVersion()
    {
        boolean isSelected = useVersionAsResourceBox.isSelected();
        if ( isSelected )
        {
            String resource = JiveInfo.getName() + " " + JiveInfo.getVersion();
            resourceField.setText( resource );
            useHostnameAsResourceBox.setSelected( false );
        }
        resourceField.setEnabled( !isSelected );
        //localPreferences.setHostAndPortConfigured(!isSelected);
    }

    @Override
	public void actionPerformed( ActionEvent e )
    {
        if ( e.getSource() == autoDiscoverBox )
        {
            updateAutoDiscovery();
        }
        else if ( e.getSource() == useHostnameAsResourceBox )
        {
            updateResourceHostname();
        }
        else if ( e.getSource() == useVersionAsResourceBox )
        {
            updateResourceVersion();
        }
    }

    public boolean validate_settings()
    {
        String timeOut = timeOutField.getText();
        String port = portField.getText();
        String resource = resourceField.getText();

        boolean valid = true;
        UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );

        try
        {
            Integer.valueOf( timeOut );
        }
        catch ( NumberFormatException numberFormatException )
        {
            JOptionPane.showMessageDialog( optionsDialog,
                            Res.getString( "message.supply.valid.timeout" ),
                            Res.getString( "title.error" ),
                            JOptionPane.ERROR_MESSAGE );
            timeOutField.requestFocus();
            valid = false;
        }

        try
        {
            Integer.valueOf( port );
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

        if ( !ModelUtil.hasLength( resource ) )
        {
            JOptionPane.showMessageDialog( optionsDialog,
                            Res.getString( "message.supply.resource" ),
                            Res.getString( "title.error" ),
                            JOptionPane.ERROR_MESSAGE );
            resourceField.requestFocus();
            valid = false;
        }

        return valid;
    }

    public void useDefault(){
        autoDiscoverBox.setSelected(!Default.getBoolean(Default.HOST_AND_PORT_CONFIGURED));
        useHostnameAsResourceBox.setSelected(Default.getBoolean(Default.USE_HOSTNAME_AS_RESOURCE));
        useVersionAsResourceBox.setSelected(Default.getBoolean(Default.USE_VERSION_AS_RESOURCE));
        compressionBox.setSelected(Default.getBoolean(Default.COMPRESSION_ENABLED));
        debuggerBox.setSelected(Default.getBoolean(Default.DEBUGGER_ENABLED));
        portField.setText(Default.getString(Default.XMPP_PORT));
        resourceField.setText(Default.getString(Default.SHORT_NAME));
        timeOutField.setText(Default.getString(Default.TIME_OUT));
    }
    
    public void saveSettings()
    {
        localPreferences.setTimeOut( Integer.parseInt( timeOutField.getText() ) );
        localPreferences.setXmppPort( Integer.parseInt( portField.getText() ) );
        localPreferences.setXmppHost( xmppHostField.getText() );
        localPreferences.setCompressionEnabled( compressionBox.isSelected() );
        localPreferences.setDebuggerEnabled( debuggerBox.isSelected() );
        localPreferences.setResource( resourceField.getText() );
        localPreferences.setUseHostnameAsResource( useHostnameAsResourceBox.isSelected() );
        localPreferences.setUseVersionAsResource( useVersionAsResourceBox.isSelected() );
    }
}
