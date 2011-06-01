package org.jivesoftware.spark.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.otr4j.session.SessionID;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.otrplug.OTRManager;

public class OTRPrefPanel extends JPanel{
    
    private OTRManager _manager;
    private SessionID _mySession = new SessionID(SparkManager.getConnection().getUser(), SparkManager.getConnection().getUser(), "Scytale");
    public OTRPrefPanel()
    {
        
        _manager = OTRManager.getInstance();
        setLayout(new GridBagLayout());

        buildGUI();
    }
    
    
    private void buildGUI()
    {
        //_manager.getKeyManager().generateLocalKeyPair(_mySession);
        KeyPair key = _manager.getKeyManager().loadLocalKeyPair(_mySession);
        
        String privkey = _manager.getKeyManager().getLocalFingerprint(_mySession);
        
       System.out.println( privkey);
        this.add(new JLabel("key: "), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
       this.add(new JTextField(privkey), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        
    
    }

    
}
