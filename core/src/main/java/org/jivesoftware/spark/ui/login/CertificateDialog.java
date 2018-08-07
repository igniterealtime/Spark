package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.CertManager;
import org.jivesoftware.sparkimpl.certificates.CertificateController;
import org.jivesoftware.sparkimpl.certificates.CertificateDialogReason;
import org.jivesoftware.sparkimpl.certificates.CertificateModel;
import org.jivesoftware.sparkimpl.certificates.OIDTranslator;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

/**
 * This class show to user all fields of certificate as well as options to check it's validity and set trust toward it.
 * When certificate is invalid then setting radio button on trust will put certificate to exceptions list. When
 * certificate is valid and radio button is set on distrust then it will be put on distrusted list. In other situations
 * trust will work according to validity
 * 
 * @author Pawel Scibiorski
 */
public class CertificateDialog extends JDialog implements ActionListener {

	private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
	private final LocalPreferences localPreferences;
	private CertificateModel certModel;
	private CertManager certControll;
	private CertificateDialogReason reason;
	private boolean addCert = false;

	private JScrollPane scrollPane;
	private JPanel panel = new JPanel();
	public boolean isAddCert() {
        return addCert;
    }

    private JPanel buttonPanel = new JPanel();
	private JPanel certStatusPanel = new JPanel();

	private JTextArea versionField = new JTextArea();
	private JTextArea serialNumberField = new JTextArea();
	private JTextArea signatureValueField = new JTextArea();
	private JTextArea signatureAlgorithmField = new JTextArea();
	private JTextArea issuerField = new JTextArea();
	private JTextArea subjectField = new JTextArea();
	private JTextArea notBeforeField = new JTextArea();
	private JTextArea notAfterField = new JTextArea();
	private JTextArea publicKeyField = new JTextArea();
	private JTextArea publicKeyAlgorithmField = new JTextArea();
	private JTextArea issuerUniqueIDField = new JTextArea();
	private JTextArea subjectUniqueIDField = new JTextArea();
	private JTextArea unsupportedExtensionsArea = new JTextArea();
	private JTextArea certStatusArea = new JTextArea();

	private JLabel infoLabel = new JLabel();
	private JLabel versionLabel = new JLabel();
	private JLabel serialNumberLabel = new JLabel();
	private JLabel signatureValueLabel = new JLabel();
	private JLabel signatureAlgorithmLabel = new JLabel();
	private JLabel issuerLabel = new JLabel();
	private JLabel subjectLabel = new JLabel();
	private JLabel notBeforeLabel = new JLabel();
	private JLabel notAfterLabel = new JLabel();
	private JLabel publicKeyLabel = new JLabel();
	private JLabel publicKeyAlgorithmLabel = new JLabel();
	private JLabel issuerUniqueIDLabel = new JLabel();
	private JLabel subjectUniqueIDLabel = new JLabel();
	private JLabel unsupportedExtensionsLabel = new JLabel();
	private JLabel extensionsLabel = new JLabel();
	
	private JCheckBox exceptionBox = new JCheckBox();
	private JButton checkValidity = new JButton();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private List<String> certUnsupportedCriticalExtensions;
	private List<String> certUnsupportedNonCriticalExtensions;
	private HashMap<String,String> certExtensions;
	private JButton deleteButton = new JButton();

	
	public CertificateDialog(LocalPreferences localPreferences, CertificateModel certModel,
			CertManager certificateController, CertificateDialogReason reason) {
		if (localPreferences == null || certModel == null) {
			throw new IllegalArgumentException();
		}
		
		certControll = certificateController;
		this.localPreferences = localPreferences;
		this.certModel = certModel;
		this.certExtensions = certModel.getExtensions();
		this.certUnsupportedCriticalExtensions = certModel.getUnsupportedCriticalExtensions();
		this.certUnsupportedNonCriticalExtensions = certModel.getUnsupportedNonCriticalExtensions();
		this.reason = reason;
		setTitle(Res.getString("title.certificate"));
		setSize(500, 600);
		setLayout(new GridBagLayout());
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dimension.width / 2 - this.getWidth(), dimension.height / 2 - this.getHeight() / 2);
		setModal(true);
		setLayout(new GridBagLayout());
		setResizable(false);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		versionField.setText(Integer.toString(certModel.getVersion()));
		serialNumberField.setText(certModel.getSerialNumber());
		signatureValueField.setText(certModel.getSignatureValue());
		signatureAlgorithmField.setText(certModel.getSignatureAlgorithm());
		issuerField.setText(certModel.getIssuer());
		subjectField.setText(certModel.getSubject());
		notBeforeField.setText(certModel.getNotBefore());
		notAfterField.setText(certModel.getNotAfter());
		publicKeyField.setText(certModel.getPublicKey());
		publicKeyAlgorithmField.setText(certModel.getPublicKeyAlgorithm());
		issuerUniqueIDField.setText(certModel.getIssuerUniqueID());
		subjectUniqueIDField.setText(certModel.getSubjectUniqueID());
		extensionsLabel.setText(Res.getString("cert.extensions"));
		certStatusArea.setText(certModel.getCertStatusAll());

		certStatusArea.setEditable(false);
		
		versionField.setLineWrap(true);
		serialNumberField.setLineWrap(true);
		signatureValueField.setLineWrap(true);
		signatureAlgorithmField.setLineWrap(true);
		issuerField.setLineWrap(true);
		subjectField.setLineWrap(true);
		notBeforeField.setLineWrap(true);
		notAfterField.setLineWrap(true);
		publicKeyField.setLineWrap(true);
		publicKeyAlgorithmField.setLineWrap(true);
		issuerUniqueIDField.setLineWrap(true);
		subjectUniqueIDField.setLineWrap(true);
		unsupportedExtensionsArea.setLineWrap(true);
		certStatusArea.setLineWrap(true);

		versionField.setEditable(false);
        serialNumberField.setEditable(false);
        signatureValueField.setEditable(false);
        signatureAlgorithmField.setEditable(false);
        issuerField.setEditable(false);
        subjectField.setEditable(false);
        notBeforeField.setEditable(false);
        notAfterField.setEditable(false);
        publicKeyField.setEditable(false);
        publicKeyAlgorithmField.setEditable(false);
        issuerUniqueIDField.setEditable(false);
        subjectUniqueIDField.setEditable(false);
        unsupportedExtensionsArea.setEditable(false);
        certStatusArea.setEditable(false);

		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		deleteButton.addActionListener(this);
		checkValidity.addActionListener(this);
		exceptionBox.addActionListener(this);
		
		ResourceUtils.resLabel(versionLabel, versionField, Res.getString("label.certificate.version"));
		ResourceUtils.resLabel(serialNumberLabel, serialNumberField, Res.getString("label.certificate.serial.number"));
		ResourceUtils.resLabel(signatureValueLabel, signatureValueField,
				Res.getString("label.certificate.signature.value"));
		ResourceUtils.resLabel(signatureAlgorithmLabel, signatureAlgorithmField,
				Res.getString("label.certificate.signature.algorithm"));
		ResourceUtils.resLabel(issuerLabel, issuerField, Res.getString("label.certificate.issuer"));
		ResourceUtils.resLabel(subjectLabel, subjectField, Res.getString("label.certificate.subject"));
		ResourceUtils.resLabel(notBeforeLabel, notBeforeField, Res.getString("label.certificate.not.before"));
		ResourceUtils.resLabel(notAfterLabel, notAfterField, Res.getString("label.certificate.not.after"));
		ResourceUtils.resLabel(publicKeyLabel, publicKeyField, Res.getString("label.certificate.public.key"));
		ResourceUtils.resLabel(publicKeyAlgorithmLabel, publicKeyAlgorithmField,
				Res.getString("label.certificate.public.key.algorithm"));
		ResourceUtils.resLabel(issuerUniqueIDLabel, issuerUniqueIDField,
				Res.getString("label.certificate.issuer.unique.id"));
		ResourceUtils.resLabel(subjectUniqueIDLabel, subjectUniqueIDField,
				Res.getString("label.certificate.subject.unique.id"));
		ResourceUtils.resLabel(unsupportedExtensionsLabel, unsupportedExtensionsArea,
				Res.getString("cert.extensions.unsupported"));
		ResourceUtils.resButton(exceptionBox, Res.getString("checkbox.on.exception.list"));
		ResourceUtils.resButton(checkValidity, Res.getString("button.check.validity"));
		ResourceUtils.resButton(okButton, Res.getString("ok"));
		ResourceUtils.resButton(cancelButton, Res.getString("cancel"));
		ResourceUtils.resButton(deleteButton, Res.getString("delete"));
        if (reason == CertificateDialogReason.ADD_CERTIFICATE) {
            infoLabel.setText(Res.getString("dialog.certificate.show"));
        }else if (reason == CertificateDialogReason.ADD_ID_CERTIFICATE){
            infoLabel.setText(Res.getString("dialog.id.certificate.show"));
        } else if (reason == CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION){
            infoLabel.setText(Res.getString("dialog.certificate.add.from.connection"));
        }
		
		panel.setLayout(new GridBagLayout());
		buttonPanel.setLayout(new GridBagLayout());

		panel.add(versionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(serialNumberLabel,
				new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(signatureValueLabel,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(signatureAlgorithmLabel,
				new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(issuerLabel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(subjectLabel, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(notBeforeLabel, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(notAfterLabel, new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(publicKeyLabel, new GridBagConstraints(0, 8, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(publicKeyAlgorithmLabel,
				new GridBagConstraints(0, 9, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(issuerUniqueIDLabel,
				new GridBagConstraints(0, 10, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(subjectUniqueIDLabel,
				new GridBagConstraints(0, 11, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		
		
		panel.add(versionField,
				new GridBagConstraints(2, 0, 12, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(serialNumberField,
				new GridBagConstraints(2, 1, 6, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(signatureValueField,
				new GridBagConstraints(2, 2, 6, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(signatureAlgorithmField,
				new GridBagConstraints(2, 3, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(issuerField, new GridBagConstraints(2, 4, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(subjectField, new GridBagConstraints(2, 5, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(notBeforeField, new GridBagConstraints(2, 6, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(notAfterField, new GridBagConstraints(2, 7, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(publicKeyField, new GridBagConstraints(2, 8, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(publicKeyAlgorithmField,
				new GridBagConstraints(2, 9, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(issuerUniqueIDField,
				new GridBagConstraints(2, 10, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(subjectUniqueIDField,
				new GridBagConstraints(2, 11, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		certStatusPanel.setLayout(new GridBagLayout());
		certStatusPanel.add(certStatusArea, new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		certStatusPanel.setBorder(
				BorderFactory.createTitledBorder(Res.getString("label.certificate.status")));
		

		// extensions
		panel.add(extensionsLabel, new GridBagConstraints(2, 12, 6, 1, 1.0, 0.0, EAST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		int i =13; //place extensions in next rows
		for(HashMap.Entry<String, String> entry : certExtensions.entrySet()) {
		    String oid = entry.getKey();
		    String value = entry.getValue();
		    JTextArea extensionArea = new JTextArea();
		    extensionArea.setLineWrap(true);
		    extensionArea.setText(value);
		    extensionArea.setEditable(false);
		    JLabel extensionLabel = new JLabel();
		    ResourceUtils.resLabel(extensionLabel, extensionArea, OIDTranslator.getDescription(oid));

			panel.add(extensionLabel,
					new GridBagConstraints(0, i, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
			panel.add(extensionArea,
					new GridBagConstraints(2, i, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
			i++;
		}
		for (String extension : certUnsupportedCriticalExtensions) {
			unsupportedExtensionsArea.append(Res.getString("cert.critical") + "\n" + extension + ": "
					+ OIDTranslator.getDescription(extension) + '\n');
		}
		for (String extension : certUnsupportedNonCriticalExtensions) {
			unsupportedExtensionsArea.append(Res.getString("cert.not.critical") + "\n" + extension + ": "
					+ OIDTranslator.getDescription(extension) + '\n');
		}

		panel.add(unsupportedExtensionsLabel, new GridBagConstraints(0, i, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		panel.add(unsupportedExtensionsArea, new GridBagConstraints(2, i, 6, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		
		if(reason == CertificateDialogReason.SHOW_CERTIFICATE || reason == CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION){
		
		    buttonPanel.add(exceptionBox, new GridBagConstraints(0, 0, 1, 1, 0.2, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
            if (reason == CertificateDialogReason.SHOW_CERTIFICATE) {
                exceptionBox.setSelected(certificateController.isOnExceptionList(certModel));
            } else if (reason == CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION) {
                exceptionBox.setSelected(true);
            }
	        
		}
		
		buttonPanel.add(certStatusPanel,  new GridBagConstraints(2, 0, 3, 2, 0.2, 0.0, CENTER, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(checkValidity,    new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(okButton,         new GridBagConstraints(2, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        if (reason == CertificateDialogReason.ADD_CERTIFICATE || reason == CertificateDialogReason.ADD_ID_CERTIFICATE
                || reason == CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION) {
            buttonPanel.add(cancelButton,
					new GridBagConstraints(3, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 5, 100), 0, 0));
		}
		if (reason == CertificateDialogReason.SHOW_CERTIFICATE || reason == CertificateDialogReason.SHOW_ID_CERTIFICATE) {
			buttonPanel.add(cancelButton,
					new GridBagConstraints(3, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, DEFAULT_INSETS, 0, 0));
			buttonPanel.add(deleteButton,
					new GridBagConstraints(4, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
		}
		
		scrollPane = new JScrollPane(panel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
        if (reason == CertificateDialogReason.ADD_CERTIFICATE || reason == CertificateDialogReason.ADD_ID_CERTIFICATE
                || reason == CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION) {
            add(infoLabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, WEST, BOTH, DEFAULT_INSETS, 0, 0));
		}
		
		add(scrollPane, new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0, WEST, BOTH, DEFAULT_INSETS, 0, 0));
		add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
				 
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	//scrolls scrollPane to top
            	panel.scrollRectToVisible(versionField.getBounds());           
        		
                // info that certificate is distrusted
                if ((      reason == CertificateDialogReason.ADD_CERTIFICATE
                        || reason == CertificateDialogReason.ADD_ID_CERTIFICATE
                        || reason == CertificateDialogReason.ADD_CERTIFICATE_FROM_CONNECTION) && !certModel.isValid()) {
                    JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.is.distrusted"));
                }
            }
        });
		
		setVisible(true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton){
			//controller should be passed to this class only if there is need to modification content of Keystore.
			if (certControll != null) {
				addCert= true;
				if (certControll instanceof CertificateController && !certControll.isOnExceptionList(certModel) && exceptionBox.isSelected()) {
				    
				    CertificateController crtCtrl = (CertificateController)certControll;
				    try {
                        crtCtrl.addCertificateAsExempted(certModel);
                    } catch (HeadlessException | InvalidNameException | KeyStoreException e1) {
                        Log.error(e1);
                    }
				}
			}
			this.dispose();
		}else if(e.getSource() == cancelButton){
			if (certControll != null) {
				certControll.setAddToKeystore(false);
			}
			this.dispose();
		}else if(e.getSource() == deleteButton){
			try {
				certControll.deleteEntry(certModel.getAlias());
				this.dispose();
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				Log.error("Couldn't delete the certificate", ex);
            }

        } else if (e.getSource() == exceptionBox) {
            certControll.addOrRemoveFromExceptionList(exceptionBox.isSelected());
        } else if (e.getSource() == checkValidity) {
            checkValidity();
        }
    }

	private void checkValidity() {
        if (certControll.checkRevocation(certModel.getCertificate())) {
            certStatusArea.setText(certModel.getCertStatusAll());
            try {
                certControll.addCertificateToBlackList(certModel.getCertificate());
            } catch (HeadlessException | KeyStoreException | NoSuchAlgorithmException | CertificateException
                    | InvalidNameException | IOException ex) {
                Log.warning("Couldn't add certificate to the blacklist", ex);
            }
            certStatusArea.updateUI();
        }
	}	
}
