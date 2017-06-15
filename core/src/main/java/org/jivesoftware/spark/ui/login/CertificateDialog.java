package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.certificates.CertificateModel;
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

	private JScrollPane scrollPane;
	private JPanel panel = new JPanel();
	private JPanel buttonPanel = new JPanel();

	private JTextField versionField = new JTextField();
	private JTextField serialNumberField = new JTextField();
	private JTextArea signatureValueField = new JTextArea();
	private JTextField signatureAlgorithmField = new JTextField();
	private JTextField issuerField = new JTextField();
	private JTextField subjectField = new JTextField();
	private JTextField notBeforeField = new JTextField();
	private JTextField notAfterField = new JTextField();
	private JTextArea publicKeyField = new JTextArea();
	private JTextField publicKeyAlgorithmField = new JTextField();
	private JTextField issuerUniqueIDField = new JTextField();
	private JTextField subjectUniqueIDField = new JTextField();

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
	
	private JRadioButton trust = new JRadioButton();
	private JRadioButton distrust = new JRadioButton();
	private JButton checkValidity = new JButton();
	private JButton okButton = new JButton();

	public CertificateDialog(LocalPreferences localPreferences, CertificateModel cert) {
		if (localPreferences == null || cert == null) {
			throw new IllegalArgumentException();
		}
		this.localPreferences = localPreferences;
		this.cert = cert;
		setTitle(Res.getString("title.certificate"));
		setSize(500, 600);
		setLayout(new GridBagLayout());
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dimension.width / 2 - this.getWidth(), dimension.height / 2 - this.getHeight() / 2);
		setModal(true);
		setLayout(new GridBagLayout());
		isAlwaysOnTop();
		
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

		signatureValueField.setLineWrap(true);
		publicKeyField.setLineWrap(true);
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(distrust);
		radioGroup.add(trust);
		
		okButton.addActionListener(this);
		
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
		ResourceUtils.resButton(trust, Res.getString("radio.certificate.trust"));
		ResourceUtils.resButton(distrust, Res.getString("radio.certificate.distrust"));
		ResourceUtils.resButton(checkValidity, Res.getString("button.check.validity"));
		ResourceUtils.resButton(okButton, Res.getString("ok"));
		
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

		buttonPanel.add(trust, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(distrust, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(checkValidity, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, WEST, NONE, DEFAULT_INSETS, 0, 0));
		buttonPanel.add(okButton, new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 200), 60, 0));

		scrollPane = new JScrollPane(panel);
		scrollPane.setMinimumSize(new Dimension(250, 500));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPane, new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0, WEST, BOTH, DEFAULT_INSETS, 0, 0));
		add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));

		scrollPane.setVisible(true);
		setVisible(true);
		
		scrollPane.getVerticalScrollBar().setValue(0);
		scrollPane.repaint();
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton){
			this.dispose();
		}
	}

}
