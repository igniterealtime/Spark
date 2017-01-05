package org.jivesoftware.sparkimpl.plugin.gateways;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.spark.component.renderer.JPanelRenderer;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;

public class GatewayTabItem extends CollapsiblePane implements GatewayItem {

    /**
     */
    private static final long serialVersionUID = 5589644402913737488L;
    /**
     */

    private boolean signedIn;

    private Transport _transport;
    private DefaultListModel model = new DefaultListModel();
    private JLabel _status = new JLabel();
    private JPanel _listPanel = new JPanel(new GridBagLayout());
    private JLabel _statusIcon = new JLabel();
    private RolloverButton _signInOut = new RolloverButton();
    private RolloverButton _registerButton = new RolloverButton();
    private JCheckBox _autoJoin = new JCheckBox();
    private boolean _transportRegistered = false;

    private RolloverButton _autoJoinButton = new RolloverButton();
    
    public GatewayTabItem(final Transport transport) {
	this._transport = transport;
	_transportRegistered = TransportUtils.isRegistered(
		SparkManager.getConnection(), _transport);

	_autoJoin.setEnabled(false);
	_autoJoin.setBackground((Color)UIManager.get("ContactItem.background"));
	this.setIcon(transport.getIcon());
	_status.setForeground((Color)UIManager.get("ContactItemDescription.foreground"));
	_status.setFont(new Font(getFont().getName(), Font.ITALIC, getFont()
		.getSize()));
	getTitlePane().add(_status);
	this.setTitle(transport.getName());

	_listPanel.setBackground((Color)UIManager.get("ContactItem.background"));
        JList _transportMenu = new JList( model );
        _transportMenu.setCellRenderer(new JPanelRenderer());

	this.setContentPane(_listPanel);

	setCollapsed(true);

	// Check if transport is already registered
	if (_transportRegistered) {
	    // If yes, check if it is online
	    if (PresenceManager.isOnline(transport.getServiceName())) {

		getTitlePane().setIcon(transport.getIcon());
		_status.setText(Res.getString("online"));
		setOnline();

	    } else {
		getTitlePane().setIcon(transport.getInactiveIcon());
		_status.setText(Res.getString("offline"));
		setOffline();
	    }

	} else {
	    // If not. set that transport is not registered yet
	    getTitlePane().setIcon(transport.getInactiveIcon());
	    _status.setText(Res.getString("offline"));
	    setNotRegistered();
	}

	createTransportMenu();
	// Check if autojoin is enabled an join automatically
	final StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
	final Runnable registerThread = () -> {
    // Send directed presence if registered with this transport.
    final boolean isRegistered = TransportUtils.isRegistered(
        SparkManager.getConnection(), transport);
    if (isRegistered) {
        // Check if auto login is set.
        boolean autoJoin = TransportUtils.autoJoinService(transport
            .getServiceName());
        if (autoJoin) {
        Presence oldPresence = statusBar.getPresence();
        Presence presence = new Presence(oldPresence.getType(),
            oldPresence.getStatus(),
            oldPresence.getPriority(),
            oldPresence.getMode());
        presence.setTo(transport.getServiceName());
            try
            {
                SparkManager.getConnection().sendStanza(presence);
            }
            catch ( SmackException.NotConnectedException e )
            {
                Log.error( "Unable to send presence.", e );
            }
        }
    }
    };

	TaskEngine.getInstance().submit(registerThread);

    }

    private void createTransportMenu() {

	_signInOut.addActionListener( e -> {
    if (signedIn) {

        final Presence offlinePresence = new Presence(
            Presence.Type.unavailable);
        offlinePresence.setTo(_transport.getServiceName());
        try
        {
            SparkManager.getConnection().sendStanza(offlinePresence);
            _statusIcon.setIcon(SparkRes
                    .getImageIcon(SparkRes.YELLOW_BALL));
        }
        catch ( SmackException.NotConnectedException e1 )
        {
            Log.error( "Unable to send presence.", e1 );
        }

    } else {
        final Presence onlinePresence = new Presence(
            Presence.Type.available);
        onlinePresence.setTo(_transport.getServiceName());
        try
        {
            SparkManager.getConnection().sendStanza(onlinePresence);
            _statusIcon.setIcon(SparkRes
                    .getImageIcon(SparkRes.YELLOW_BALL));
        }
        catch ( SmackException.NotConnectedException e1 )
        {
            Log.error( "Unable to send presence.", e1 );
        }
    }

    } );

	//_autoJoin.setText(Res.getString("menuitem.sign.in.at.login"));

	// If transport is registered, we can check if the autojoin is enabled
	if (_transportRegistered) {
	    _autoJoin.setSelected(TransportUtils.autoJoinService(_transport
		    .getServiceName()));
	    _registerButton.setText(Res
		    .getString("menuitem.delete.login.information"));
	    _signInOut.setEnabled(true);

	} else {
	    setNotRegistered();
	}

	_autoJoinButton.addActionListener( e -> {
    _autoJoin.setSelected(!_autoJoin.isSelected());
     TransportUtils.setAutoJoin(_transport.getServiceName(), _autoJoin.isSelected());

    } );
	
	_registerButton.addActionListener( e -> {
    // If transport is registered we should show the
    // "delete information" gui
        UIManager.put("OptionPane.yesButtonText", Res.getString("yes"));
        UIManager.put("OptionPane.noButtonText", Res.getString("no"));
        UIManager.put("OptionPane.cancelButtonText", Res.getString("cancel"));
    if (TransportUtils.isRegistered(SparkManager.getConnection(),
        _transport)) {

        int confirm = JOptionPane.showConfirmDialog(SparkManager
            .getMainWindow(), Res.getString(
            "message.disable.transport", _transport.getName()),
            Res.getString("title.disable.transport"),
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
        try {
            TransportUtils.unregister(
                SparkManager.getConnection(),
                _transport.getServiceName());
            setNotRegistered();
        } catch (SmackException e1) {
            Log.error(e1);
        }
        }
    } else {
        // If transport is not registered we should show the
        // register gui
        TransportRegistrationDialog registrationDialog = new TransportRegistrationDialog(
            _transport.getServiceName());
        registrationDialog.invoke();
        // Set user as offline while he fills in the login
        // information
        setOffline();

        ActionListener al = e1 -> {
            // If user canceled the register window, he is sill
            // not registrated
            setNotRegistered();
        };

        registrationDialog.addCancelActionListener(al);

    }

    } );
	
	_autoJoinButton.setText(Res.getString("menuitem.sign.in.at.login"));
	_autoJoinButton.setHorizontalAlignment(SwingConstants.LEFT);
//	JPanel signPanel = new JPanel(new BorderLayout());
//	signPanel.setBackground(Color.lightGray);
//	signPanel.add(_statusIcon, BorderLayout.WEST);
//	signPanel.add(_signInOut, BorderLayout.CENTER);
	_registerButton.setHorizontalAlignment(SwingConstants.LEFT);
	_signInOut.setHorizontalAlignment(SwingConstants.LEFT);
	
	_listPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 0.1, 0.0,GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	_listPanel.add(_statusIcon, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));
	_listPanel.add(_signInOut, new GridBagConstraints(2, 0, 1, 1, 0.9, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
	_listPanel.add(_autoJoin, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	_listPanel.add(_autoJoinButton, new GridBagConstraints(2, 1, 1, 1, 0.9, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
	_listPanel.add(_registerButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
	

	
	
	//_listPanel.add(_autoJoin);
	//_listPanel.add(_registerButton);
	

    }

    // Set GUI when user is not registered
    private void setNotRegistered() {
	_transportRegistered = false;
	_autoJoin.setSelected(false);
	_registerButton.setText(Res
		.getString("menuitem.enter.login.information"));
	_signInOut.setEnabled(false);
	_signInOut.setText(Res.getString("menuitem.sign.in"));
	_autoJoinButton.setEnabled(false);
	_statusIcon.setIcon(SparkRes.getImageIcon(SparkRes.BLUE_BALL));
    }

    // Set GUI when user is registered but offline
    private void setOffline() {
	_transportRegistered = true;
	_autoJoin.setSelected(TransportUtils.autoJoinService(_transport
		.getServiceName()));
	_registerButton.setText(Res
		.getString("menuitem.delete.login.information"));
	_signInOut.setEnabled(true);
	_signInOut.setText(Res.getString("menuitem.sign.in"));
	_autoJoinButton.setEnabled(true);
	_statusIcon.setIcon(SparkRes.getImageIcon(SparkRes.RED_BALL));
    }

    // Set GUI when user is online with rigistered transport
    private void setOnline() {
	_statusIcon.setIcon(SparkRes.getImageIcon(SparkRes.GREEN_BALL));
	EventQueue.invokeLater( () -> _autoJoin.setSelected(TransportUtils.autoJoinService(_transport
        .getServiceName())) );
	
	_signInOut.setText(Res.getString("menuitem.sign.out"));
	_signInOut.setEnabled(true);
	_autoJoinButton.setEnabled(true);
	_registerButton.setText(Res
		.getString("menuitem.delete.login.information"));
    }

    // Change gui if transport signed on or off
    public void signedIn(final boolean signedIn) {

	if (!signedIn) {
	    getTitlePane().setIcon(_transport.getInactiveIcon());
	    _status.setText(Res.getString("offline"));
	    if (!_transportRegistered) {

		_statusIcon.setIcon(SparkRes.getImageIcon(SparkRes.BLUE_BALL));
		_signInOut.setText(Res.getString("menuitem.sign.in"));
	    } else {
		_statusIcon.setIcon(SparkRes.getImageIcon(SparkRes.RED_BALL));
		setOffline();
	    }

	} else {
	    getTitlePane().setIcon(_transport.getIcon());
	    _status.setText(Res.getString("online"));
	    setOnline();

	}
	this.signedIn = signedIn;
    }

    public boolean isLoggedIn() {
	return signedIn;
    }

}
