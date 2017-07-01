package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
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
	private CertificateModel cert;
	private CertificateController certControll;
	private CertificateDialogReason reason;

	private JScrollPane scrollPane;
	private JPanel panel = new JPanel();
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
	
	private JRadioButton trust = new JRadioButton();
	private JRadioButton distrust = new JRadioButton();
	private JButton checkValidity = new JButton();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private List<String> certUnsupportedCriticalExtensions;
	private List<String> certUnsupportedNonCriticalExtensions;
	private HashMap<String,String> certExtensions;
	private JButton deleteButton = new JButton();
	
	public CertificateDialog(LocalPreferences localPreferences, CertificateModel cert,
			CertificateController certificateController, CertificateDialogReason reason) {
		if (localPreferences == null || cert == null) {
			throw new IllegalArgumentException();
		}
		certControll = certificateController;
		this.localPreferences = localPreferences;
		this.cert = cert;
		this.certExtensions = cert.getExtensions();
		this.certUnsupportedCriticalExtensions = cert.getUnsupportedCriticalExtensions();
		this.certUnsupportedNonCriticalExtensions = cert.getUnsupportedNonCriticalExtensions();
		this.reason = reason;
		setTitle(Res.getString("title.certificate"));
		setSize(500, 600);
		setLayout(new GridBagLayout());
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dimension.width / 2 - this.getWidth(), dimension.height / 2 - this.getHeight() / 2);
		setModal(true);
		setLayout(new GridBagLayout());
		isAlwaysOnTop();
		setResizable(false);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		versionField.setText(Integer.toString(cert.getVersion()));
		serialNumberField.setText(cert.getSerialNumber());
		signatureValueField.setText(cert.getSignatureValue());
		signatureAlgorithmField.setText(cert.getSignatureAlgorithm());
		issuerField.setText(cert.getIssuer());
		subjectField.setText(cert.getSubject());
		notBeforeField.setText(cert.getNotBefore());
		notAfterField.setText(cert.getNotAfter());
		publicKeyField.setText(cert.getPublicKey());
		publicKeyAlgorithmField.setText(cert.getPublicKeyAlgorithm());
		issuerUniqueIDField.setText(cert.getIssuerUniqueID());
		subjectUniqueIDField.setText(cert.getSubjectUniqueID());
		extensionsLabel.setText(Res.getString("cert.extensions"));
		certStatusArea.setText(cert.getCertStatusAll());

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
		
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(distrust);
		radioGroup.add(trust);
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		deleteButton.addActionListener(this);
		
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
		ResourceUtils.resButton(trust, Res.getString("radio.certificate.trust"));
		ResourceUtils.resButton(distrust, Res.getString("radio.certificate.distrust"));
		ResourceUtils.resButton(checkValidity, Res.getString("button.check.validity"));
		ResourceUtils.resButton(okButton, Res.getString("ok"));
		ResourceUtils.resButton(cancelButton, Res.getString("cancel"));
		ResourceUtils.resButton(deleteButton, Res.getString("delete"));
		infoLabel.setText(Res.getString("dialog.certificate.show"));
		
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
				BorderFactory.createTitledBorder(Res.getString("label.certificate.add.certificate.to.truststore")));
		

		// extensions
		panel.add(extensionsLabel, new GridBagConstraints(2, 12, 6, 1, 1.0, 0.0, EAST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		int i =13; //place extensions in next rows
		for(HashMap.Entry<String, String> entry : certExtensions.entrySet()) {
		    String oid = entry.getKey();
		    String value = entry.getValue();
		    JTextArea extensionArea = new JTextArea();
		    extensionArea.setLineWrap(true);
		    extensionArea.setText(value);
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
		

		buttonPanel.add(trust, new GridBagConstraints(0, 0, 1, 1, 0.2, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(distrust, new GridBagConstraints(1, 0, 1, 1, 0.1, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(certStatusPanel, new GridBagConstraints(2, 0, 3, 2, 0.2, 0.0, CENTER, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(checkValidity, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(okButton, new GridBagConstraints(2, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		if (reason == CertificateDialogReason.ADD_CERTIFICATE) {
			buttonPanel.add(cancelButton,
					new GridBagConstraints(3, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 5, 100), 0, 0));
		}
		if (reason == CertificateDialogReason.SHOW_CERTIFICATE) {
			buttonPanel.add(cancelButton,
					new GridBagConstraints(3, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, DEFAULT_INSETS, 0, 0));
			buttonPanel.add(deleteButton,
					new GridBagConstraints(4, 2, 1, 1, 0.2, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
		}
		trust.setSelected(cert.isValid() || cert.isExempted());
		distrust.setSelected(!cert.isValid() && !cert.isExempted());
		
		scrollPane = new JScrollPane(panel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		if (reason == CertificateDialogReason.ADD_CERTIFICATE) {
			add(infoLabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, WEST, BOTH, DEFAULT_INSETS, 0, 0));
		}
		
		add(scrollPane, new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0, WEST, BOTH, DEFAULT_INSETS, 0, 0));
		add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
				 
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	//scrolls scrollPane to top
            	panel.scrollRectToVisible(versionField.getBounds());           
        		
            	//info that certificate is distrusted
        		if(reason == CertificateDialogReason.ADD_CERTIFICATE && !cert.isValid()){
        			JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.is.distrusted"));
        		}
            }
        });
		
		setVisible(true);
		
	}
	
	public CertificateDialog(LocalPreferences localPreferences, CertificateModel certificateModel) {
		this(localPreferences, certificateModel, null, CertificateDialogReason.SHOW_CERTIFICATE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton){
			//controller should be passed to this class only if there is need to modification content of Keystore.
			if (certControll != null) {
				certControll.setAddToKeystore(true);
			}
			this.dispose();
		}else if(e.getSource() == cancelButton){
			if (certControll != null) {
				certControll.setAddToKeystore(false);
			}
			this.dispose();
		}else if(e.getSource() == deleteButton){
			try {
				certControll.deleteCertificate(cert.getAlias());
				this.dispose();
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				Log.error("Couldn't delete the certificate", ex);
			}
		}
	}

}
