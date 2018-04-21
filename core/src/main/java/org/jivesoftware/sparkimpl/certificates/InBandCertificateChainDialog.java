package org.jivesoftware.sparkimpl.certificates;

import static java.awt.GridBagConstraints.WEST;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.util.log.Log;

public class InBandCertificateChainDialog extends JDialog implements ActionListener {
    private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    private KeyStore trustStore, caCertsStore;
    private boolean readyToAddChain = false;
    private CertificateController certMan;
    private X509Certificate[] chain;
    private JScrollPane scrollPane;
    private JPanel panel = new JPanel();
    private JButton addChainButton = new JButton();
    private JButton cancelButton = new JButton();
    private ImageIcon imgIconInStore = SparkRes.getImageIcon(SparkRes.ACCEPT_INVITE_IMAGE);

    private JLabel informationLabel;

    public InBandCertificateChainDialog(X509Certificate[] chain, CertificateController certMan) throws Exception {
        if(chain == null) {
            throw new CertificateException("Certificate chain cannot be null");
        }
        if(certMan == null) {
            throw new Exception("Certificate controller cannot be null");
        }
        
        this.certMan = certMan;
        this.chain = chain;
        // openKeystores
        trustStore = certMan.openKeyStore(certMan.TRUSTED);
        caCertsStore = certMan.openCacertsKeyStore();
        panel.setLayout(new GridBagLayout());
        scrollPane = new JScrollPane(panel);

        setTitle(Res.getString("title.certificate"));
        setSize(400, 300);

        setLayout(new GridBagLayout());
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getWidth(), dimension.height / 2 - this.getHeight() / 2);
        setModal(true);
        setAlwaysOnTop(true);
        setResizable(false);
        
        for(int i =0; i<chain.length; i ++) {
            JTextField certName = new JTextField();
            CertificateModel certModel = new CertificateModel(chain[i]);
            
            certName.setText(certModel.getSubjectCommonName());
            certName.setEditable(false);
            
            panel.add(certName, new GridBagConstraints(0, i, 2, 1, 0.8, 0, WEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
            
            GridBagConstraints buttonOrIconConstraints = new GridBagConstraints(2, i, 1, 1, 0.2, 0, WEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);
            
            try {
                if ((trustStore.getCertificateAlias(chain[i]) == null)
                        && (caCertsStore.getCertificateAlias(chain[i]) == null)) {
                    JButton addCertButton = new JButton(Res.getString("button.add2"));
                    addCertButton.addActionListener(e -> {
                        try {
                            certMan.addEntryToKeyStore(certModel.getCertificate(),
                                    CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION);
                            certMan.overWriteKeyStores();
                        } catch (HeadlessException | InvalidNameException | KeyStoreException e1) {
                            Log.error("Cannot add certificate from connection");
                        }
                        repaint();
                    });
                    panel.add(addCertButton, buttonOrIconConstraints);
                } else {

                    panel.add(new JLabel(imgIconInStore), buttonOrIconConstraints);
                }
            } catch (KeyStoreException e) {
                Log.error("Cannot access one of the KeyStores", e);
            }
        }
      

        informationLabel = new JLabel(Res.getString("dialog.certificate.chain.add.from.connection"));
        
        addChainButton.setText(Res.getString("dialog.certificate.add.chain"));
        cancelButton.setText(Res.getString("cancel"));
        
        addChainButton.addActionListener(this);
        cancelButton.addActionListener(this);

        add(informationLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.2, WEST, GridBagConstraints.HORIZONTAL,
                DEFAULT_INSETS, 0, 0));
        add(scrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.6, WEST, GridBagConstraints.HORIZONTAL,
                DEFAULT_INSETS, 0, 0));
        add(addChainButton, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.1, WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 25, 5, 25), 50, 0));
        add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.1, WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 25, 5, 25), 50, 0));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cancelButton) {
            this.dispose();
        }
        if(e.getSource() == addChainButton) {
            readyToAddChain = true;
            this.dispose();
        }
      
    }

    public boolean isReadyToAddChain() {
        return readyToAddChain;
    }
    
}
