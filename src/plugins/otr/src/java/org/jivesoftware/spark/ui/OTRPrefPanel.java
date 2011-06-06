package org.jivesoftware.spark.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.java.otr4j.session.SessionID;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.otrplug.OTRProperties;
import org.jivesoftware.spark.otrplug.OTRResources;

public class OTRPrefPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -7125162190413040003L;
    private OTRManager _manager;
    private JCheckBox _enableOTR;
    private JCheckBox _closeSessionOff;
    private JCheckBox _closeSessionOnWindowClose;
    private JLabel _currentKeyLabel;
    private JButton _renewPrivateKey;
    private OTRKeyTable _keytable;
    private JTextField _privateKey;
    private JPanel _gridPanel;
    private MyOtrKeyManager _keyManager;
    private OTRProperties _properties;
    
    public OTRPrefPanel() {

        _manager = OTRManager.getInstance();
        _keyManager = _manager.getKeyManager();
        _properties = OTRProperties.getInstance();
        setLayout(new VerticalFlowLayout());
        init();
        buildGUI();
        OtrEnableSwitch();
        this.add(_gridPanel);
    }

    private void OtrEnableSwitch() {
    
       _enableOTR.setSelected(_properties.getIsOTREnabled());
        
    }

    public boolean isOTREnabled()
    {
        return _enableOTR.isSelected();
    }
    
    private void init() {
        _gridPanel = new JPanel(new GridBagLayout());

        _enableOTR = new JCheckBox();
        _enableOTR.setText(OTRResources.getString("otr.enable"));
        
        _closeSessionOff = new JCheckBox();
        _closeSessionOff.setText(OTRResources.getString("otr.close.session.on.contact.off"));
        
        _closeSessionOnWindowClose = new JCheckBox();
        _closeSessionOnWindowClose.setText(OTRResources.getString("otr.close.session.on.window.close"));
        
        _currentKeyLabel = new JLabel();
        _currentKeyLabel.setText(OTRResources.getString("current.priv.key"));

        _renewPrivateKey = new JButton();
        _renewPrivateKey.setText(OTRResources.getString("renew.current.key"));
        _renewPrivateKey.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SessionID mySession = new SessionID(SparkManager.getConnection().getUser(), "no one", "Scytale");
                _manager.getKeyManager().generateLocalKeyPair(mySession);
                _privateKey.setText(getCurrentLocalKey());
            }
        });

        _privateKey = new JTextField();
        _privateKey.setEditable(false);
        _privateKey.setSize(300, 20);
        _privateKey.setText(getCurrentLocalKey());

        _keytable = new OTRKeyTable();

        loadRemoteKeys();

        _keytable.addTableChangeListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int col = e.getColumn();
                int row = e.getFirstRow();

                if (col == 2) {
                    boolean selection = (Boolean) _keytable.getValueAt(row, col);
                    String JID = (String)_keytable.getValueAt(row, 0);
                    SessionID curSelectedSession = new SessionID(SparkManager.getConnection().getUser(), JID, "Scytale");
                    if (selection) {
                        _keyManager.verify(curSelectedSession);
                    } else

                    {
                        _keyManager.unverify(curSelectedSession);
                    }
                }
            }
        });

    }

    private String getCurrentLocalKey() {
        SessionID mySession = new SessionID(SparkManager.getConnection().getUser(), "no one", "Scytale");
        String myKey = _keyManager.getLocalFingerprint(mySession);
        return myKey;
    }

    private void loadRemoteKeys() {

        for (RosterEntry entry : SparkManager.getConnection().getRoster().getEntries()) {
            SessionID curSession = new SessionID(SparkManager.getConnection().getUser(), entry.getUser(), "Scytale");
            String remoteKey = _keyManager.getRemoteFingerprint(curSession);
            if (remoteKey != null) {
                boolean isVerified = _keyManager.isVerified(curSession);
                _keytable.addEntry(entry.getUser(), remoteKey, isVerified);
            }
        }
    }

    private void buildGUI() {
        _gridPanel.setBorder(BorderFactory.createTitledBorder(OTRResources.getString("otr.settings")));

        _gridPanel.add(_enableOTR, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        _gridPanel.add(_closeSessionOff, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        _gridPanel.add(_closeSessionOnWindowClose, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        _gridPanel.add(_currentKeyLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        _gridPanel.add(_privateKey, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        _gridPanel.add(_renewPrivateKey, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        _gridPanel.add(_keytable, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
    }

}
