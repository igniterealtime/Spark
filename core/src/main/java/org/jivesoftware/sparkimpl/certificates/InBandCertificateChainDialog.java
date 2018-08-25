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
    private boolean readyToAddEndCertificate;
    private CertificateController certMan;
    private X509Certificate[] chain;
    private JTextField endCertTextField = new JTextField();
    private CertificateModel endCertModel;
    private JScrollPane scrollPane;
    private JPanel panel = new JPanel();
    private JButton addSingleCertButton =   new JButton();
    private JButton cancelButton =          new JButton();
    private JButton CertInfoButton =        new JButton();
    private JButton okButton =              new JButton();
    private ImageIcon imgIconInStore = SparkRes.getImageIcon(SparkRes.ACCEPT_INVITE_IMAGE);
    private JLabel informationLabel;

    public InBandCertificateChainDialog(X509Certificate[] chain, CertificateController certMan) throws Exception {
        if (chain == null) {
            throw new CertificateException("Certificate chain cannot be null");
        }
        if (certMan == null) {
            throw new Exception("Certificate controller cannot be null");
        }

        this.certMan = certMan;
        this.chain = chain;
        // openKeystores
        trustStore = certMan.openKeyStore(CertificateController.TRUSTED);
        caCertsStore = certMan.openCacertsKeyStore();
        panel.setLayout(new GridBagLayout());
        scrollPane = new JScrollPane(panel);

        setTitle(Res.getString("title.certificate"));
        setSize(400, 300);

        setLayout(new GridBagLayout());
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getWidth(), dimension.height / 2 - this.getHeight() / 2);
        setModal(true);
        setResizable(false);
        
        endCertModel = new CertificateModel(chain[0]);

        addChainButtons();

        informationLabel = new JLabel(Res.getString("dialog.certificate.presented.by.server"));
        
        if (endCertModel.getSubjectCommonName() != null) {
            endCertTextField.setText(endCertModel.getSubjectCommonName());
        }else {
            endCertTextField.setText(endCertModel.getSubject());
        }        
        endCertTextField.setEditable(false);
        
        addSingleCertButton.setText(Res.getString("button.add2"));
        cancelButton.setText(Res.getString("cancel"));
        CertInfoButton.setText(Res.getString("button.cert.info"));
        okButton.setText(Res.getString("label.ok"));

        addSingleCertButton.addActionListener(this);
        cancelButton.addActionListener(this);
        CertInfoButton.addActionListener(this);
        okButton.addActionListener(this);

        add(informationLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.2, WEST, GridBagConstraints.HORIZONTAL,
                DEFAULT_INSETS, 0, 0));
        add(endCertTextField, new GridBagConstraints(0, 2, 3, 1, 1.0, 0.2, WEST, GridBagConstraints.HORIZONTAL,
                DEFAULT_INSETS, 0, 0));
        add(addSingleCertButton, new GridBagConstraints(0, 6, 1, 1, 0.33, 0.1, WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 25, 0));
        add(CertInfoButton, new GridBagConstraints(1, 6, 1, 1, 0.33, 0.1, WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 15, 5, 15), 25, 0));
        add(cancelButton, new GridBagConstraints(2, 6, 1, 1, 0.33, 0.1, WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 25, 0));

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    private void addChainButtons() {
        for (int i = 0; i < chain.length; i++) {
            JTextField certName = new JTextField();
            CertificateModel certModel = new CertificateModel(chain[i]);
            if (certModel.getSubjectCommonName() != null) {
            certName.setText(certModel.getSubjectCommonName());
            } else {
                certName.setText(certModel.getSubject());
            }
            certName.setEditable(false);

            panel.add(certName, new GridBagConstraints(0, i, 2, 1, 0.8, 0, WEST, GridBagConstraints.HORIZONTAL,
                    DEFAULT_INSETS, 0, 0));

            GridBagConstraints buttonOrIconConstraints = new GridBagConstraints(2, i, 1, 1, 0.2, 0, WEST,
                    GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);

            try {
                if ((trustStore.getCertificateAlias(chain[i]) == null)
                        && (caCertsStore.getCertificateAlias(chain[i]) == null)) {

                    JButton addCertButton = new JButton(Res.getString("button.add2"));
                    addCertButton.addActionListener(e -> addCertButtonImpl(certModel, addCertButton, buttonOrIconConstraints));
                    panel.add(addCertButton, buttonOrIconConstraints);

                } else {
                    panel.add(new JLabel(imgIconInStore), buttonOrIconConstraints);
                }
            } catch (KeyStoreException e) {
                Log.error("Cannot access one of the KeyStores", e);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            this.dispose();
        }
        if (e.getSource() == CertInfoButton) {
            showCertInfo();
        }
        if (e.getSource() == addSingleCertButton) {
            addEndEntityCertButtonImpl(endCertModel);
            dispose();
        }
        if (e.getSource() == okButton) {
            certMan.overWriteKeyStores();
            dispose();
        }
    }

    private void showCertInfo() {
        informationLabel.setText(Res.getString("dialog.certificate.chain.add.from.connection"));
        remove(endCertTextField);
        remove(CertInfoButton);
        remove(addSingleCertButton);
        add(okButton, new GridBagConstraints(0, 6, 1, 1, 0.33, 0.1, WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 25, 0));
        add(scrollPane, new GridBagConstraints(0, 1, 3, 4, 1.0, 0.6, WEST, GridBagConstraints.HORIZONTAL,
                DEFAULT_INSETS, 0, 50));
        revalidate();
        repaint();
    }

    private void addEndEntityCertButtonImpl(CertificateModel certModel) {
        try {
            certMan.addEntryToKeyStore(certModel.getCertificate(), true);
            certMan.overWriteKeyStores();
        } catch (HeadlessException | InvalidNameException | KeyStoreException e) {
            Log.error("Cannot add certificate from connection", e);
        }
    }
    
    private void addCertButtonImpl(CertificateModel certModel, JButton button, GridBagConstraints constraints) {
        try {
            certMan.addEntryToKeyStore(certModel.getCertificate(), CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION);      
            panel.remove(button);
            panel.add(new JLabel(imgIconInStore), constraints);
        } catch (HeadlessException | InvalidNameException | KeyStoreException e1) {
            Log.error("Cannot add certificate from connection", e1);
        }
        
        revalidate();
        repaint();
    }
    
    public boolean isReadyToAddEndCertificate() {
        return readyToAddEndCertificate;
    }

}
