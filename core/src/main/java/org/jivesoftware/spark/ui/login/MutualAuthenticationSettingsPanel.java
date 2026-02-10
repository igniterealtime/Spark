package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.naming.InvalidNameException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.IdentityController;
import org.jivesoftware.sparkimpl.certificates.PemHelper;
import org.jivesoftware.sparkimpl.certificates.PemHelper.PemBuilder;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class MutualAuthenticationSettingsPanel extends JPanel implements ActionListener, MouseListener {
    private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    private final IdentityController idControll;

    private static JTable idTable;
    private static JScrollPane scrollPane;

    private final JFileChooser fileChooser = new JFileChooser();
    private final JButton addCertButton = new JButton();
    private final JButton showCert =      new JButton();

    private final JPanel uploadCertificatePanel = new JPanel();
    private final JPanel creationPanel =          new JPanel();

    private final JRadioButton selfSignedCertificate =     new JRadioButton();
    private final JRadioButton certificateSigningRequest = new JRadioButton();
    private final JCheckBox saveCertToFile = new JCheckBox();
    private final JButton createButton = new JButton();

    private final JTextField commonNameField =        new JTextField();
    private final JTextField organizationUnitField =  new JTextField();
    private final JTextField organizationField =      new JTextField();
    private final JTextField countryField =           new JTextField();
    private final JTextField cityField =              new JTextField();

    private final JLabel commonNameLabel =        new JLabel();
    private final JLabel organizationUnitLabel =  new JLabel();
    private final JLabel organizationLabel =      new JLabel();
    private final JLabel countryLabel =           new JLabel();
    private final JLabel cityLabel =              new JLabel();
    private final ButtonGroup radioGroup = new ButtonGroup();
    //current Spark version support only .pem format of RSA private key with certificate
    private final FileNameExtensionFilter certFilter = new FileNameExtensionFilter(Res.getString("menuitem.certificate.files.filter"),"pem");

    public MutualAuthenticationSettingsPanel(LocalPreferences localPreferences, JDialog optionsDialog) {
        setLayout(new GridBagLayout());

        idControll = new IdentityController(localPreferences);
        idTable = new JTable(idControll.getTableModel());
        idTable.addMouseListener(this);
        idTable.setPreferredSize(new Dimension(50, 50));
        idTable.setPreferredScrollableViewportSize(idTable.getPreferredSize());
        idTable.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(idTable);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        ResourceUtils.resButton(addCertButton, Res.getString("label.choose.file"));

        ResourceUtils.resButton(selfSignedCertificate,      Res.getString("cert.self.signed"));
        ResourceUtils.resButton(certificateSigningRequest,  Res.getString("cert.sign.request"));
        ResourceUtils.resButton(saveCertToFile,             Res.getString("cert.self.signed.save.to.file"));
        
        ResourceUtils.resLabel( commonNameLabel,        commonNameField,        Res.getString("cert.common.name"));
        ResourceUtils.resLabel( organizationUnitLabel,  organizationUnitField,  Res.getString("cert.organization.unit"));
        ResourceUtils.resLabel( organizationLabel,      organizationField,      Res.getString("cert.organization"));
        ResourceUtils.resLabel( countryLabel,           countryField,           Res.getString("cert.country"));
        ResourceUtils.resLabel( cityLabel,              cityField,              Res.getString("cert.city"));

        ResourceUtils.resButton(createButton, Res.getString("create"));
        ResourceUtils.resButton(showCert, Res.getString("button.show.certificate"));


        uploadCertificatePanel.setLayout(new GridBagLayout());
        uploadCertificatePanel.setBorder(BorderFactory.createTitledBorder(Res.getString("label.certificate.add.certificate.to.identitystore")));
        uploadCertificatePanel.add(addCertButton,    new GridBagConstraints(0, 0, 1, 1, 0.05, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

        radioGroup.add(selfSignedCertificate);
        radioGroup.add(certificateSigningRequest);
        
        selfSignedCertificate.addActionListener(this);
        certificateSigningRequest.addActionListener(this);
        
        certificateSigningRequest.setSelected(true);
        saveCertToFile.setEnabled(false);

        creationPanel.setLayout(new GridBagLayout());

        creationPanel.add(certificateSigningRequest, new GridBagConstraints(0, 0, 1, 1, 0.3, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(selfSignedCertificate,     new GridBagConstraints(1, 0, 1, 1, 0.3, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(saveCertToFile,            new GridBagConstraints(2, 0, 1, 1, 0.4, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 200, 0));
        
        creationPanel.add(commonNameLabel,           new GridBagConstraints(0, 1, 1, 1, 0.05, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(organizationUnitLabel,     new GridBagConstraints(0, 2, 1, 1, 0.05, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(organizationLabel,         new GridBagConstraints(0, 3, 1, 1, 0.05, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(countryLabel,              new GridBagConstraints(0, 4, 1, 1, 0.05, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(cityLabel,                 new GridBagConstraints(0, 5, 1, 1, 0.05, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(createButton,              new GridBagConstraints(0, 6, 1, 1, 0.05, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

        creationPanel.add(commonNameField,           new GridBagConstraints(1, 1, 2, 1, 0.95, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(organizationUnitField,     new GridBagConstraints(1, 2, 2, 1, 0.95, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(organizationField,         new GridBagConstraints(1, 3, 2, 1, 0.95, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(countryField,              new GridBagConstraints(1, 4, 2, 1, 0.95, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
        creationPanel.add(cityField,                 new GridBagConstraints(1, 5, 2, 1, 0.95, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));


        add(scrollPane,             new GridBagConstraints(0, 0, 1, 1, 1.0, 0.3, WEST, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0));
        add(uploadCertificatePanel, new GridBagConstraints(0, 1, 1, 1, 0.2, 0.2, WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 400), 0, 0));
        add(showCert,               new GridBagConstraints(0, 2, 1, 1, 0.2, 0.2, WEST, HORIZONTAL, new Insets(5, 5, 5, 400), 0, 0));
        add(creationPanel,          new GridBagConstraints(0, 3, 1, 6, 1.0, 0.5, WEST, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0));

        showCert.setEnabled(false);
        showCert.addActionListener(this);
        addCertButton.addActionListener(this);
        createButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addCertButton){
           addCertificateByChoosingFile();
        }
        if(e.getSource() == idTable){
            showCert.setEnabled(true);    
        }
        if(e.getSource() == showCert){
            idControll.showCertificate();
        }
        if (e.getSource() == certificateSigningRequest) {
            saveCertToFile.setEnabled(false);
        }
        if (e.getSource() == selfSignedCertificate) {
            saveCertToFile.setEnabled(true);
        }
        if (e.getSource() == createButton) {
            if (certificateSigningRequest.isSelected()) {
                createCertificateSignRequest();
            } else if (selfSignedCertificate.isSelected()) {
                createSelfSignedCertificate();
            }
        }
    }

    private void createCertificateSignRequest() {
        idControll.setUpData(commonNameField.getText(), organizationUnitField.getText(), organizationField.getText(), countryField.getText(),
                cityField.getText());
        try {
            KeyPair keyPair = idControll.createKeyPair();

            PKCS10CertificationRequest request = idControll.createCSR(keyPair);
            PemHelper.saveToPemFile(keyPair, IdentityController.KEY_FILE);
            PemHelper.saveToPemFile(request, IdentityController.CSR_FILE);
            JOptionPane.showMessageDialog(null,
                    Res.getString("dialog.certificate.request.has.been.created") + IdentityController.SECURITY_DIRECTORY.toString());
        } catch (OperatorCreationException | NoSuchAlgorithmException | IOException | NoSuchProviderException e1) {
            Log.error("Couldn't create Certificate Signing Request", e1);
        }
    }

    private void createSelfSignedCertificate() {
	idControll.setUpData(commonNameField.getText(), organizationUnitField.getText(), organizationField.getText(), countryField.getText(),
		cityField.getText());
	try {
	    KeyPair keyPair = idControll.createKeyPair();

	    X509Certificate cert = idControll.createSelfSignedCertificate(keyPair);
            if (saveCertToFile.isSelected()) {
                PemBuilder pemBuilder = new PemBuilder();
                pemBuilder.add(keyPair.getPrivate());
                pemBuilder.add(cert);
                pemBuilder.saveToPemFile(IdentityController.CERT_FILE);
                JOptionPane.showMessageDialog(null,
                        Res.getString("dialog.self.signed.certificate.has.been.created") + IdentityController.SECURITY_DIRECTORY.toString());
            } else {
                try {
                    idControll.addEntryToKeyStore(cert, keyPair.getPrivate());
                } catch (HeadlessException | InvalidNameException | KeyStoreException e) {
                        Log.error("Couldn't save entry to IdentityStore", e);
                }
            }
	} catch (NoSuchAlgorithmException | NoSuchProviderException | IOException | OperatorCreationException | CertificateException e1) {
	    Log.error("Couldn't create Self Signed Certificate", e1);
	}
    }

    public static JTable getIdTable() {
        return idTable;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub   
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (e.getSource() == idTable) {
                idControll.showCertificate();
            }
        }
        if (e.getSource() == idTable) {
            showCert.setEnabled(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    private void addCertificateByChoosingFile() {
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(certFilter);
        fileChooser.setFileFilter(certFilter);

        int retVal = fileChooser.showOpenDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                idControll.addEntryFileToKeyStore(file);
            } catch (CertificateException e) {
                JOptionPane.showMessageDialog(null, Res.getString("dialog.cannot.upload.certificate.might.be.ill.formatted"));
                Log.error("Cannot upload certificate file", e);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException | KeyStoreException | InvalidNameException
                    | IOException e) {
                JOptionPane.showMessageDialog(null, Res.getString("dialog.cannot.upload.certificate"));
                Log.error("Cannot upload certificate file", e);
            }
        }
    }

    public void saveSettings() {
        idControll.overWriteKeyStores();
    }
}
