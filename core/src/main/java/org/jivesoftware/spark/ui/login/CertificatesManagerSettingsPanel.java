package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.certificates.CertificateController;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

/**
 * This class serve as visual in implementation of manageable list of certificates. Together with CertificateController
 * and CertificateModel Classes this apply MVC pattern.
 * 
 * @author Paweł Ścibiorski
 *
 */
public class CertificatesManagerSettingsPanel extends JPanel implements ActionListener, MouseListener {

	private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
	private final LocalPreferences localPreferences;
	private CertificateController certControll;
	private JTable certTable = new JTable();
	private JCheckBox acceptAll = new JCheckBox();
	private JCheckBox acceptExpired = new JCheckBox();
	private JCheckBox acceptRevoked = new JCheckBox();
	private JCheckBox acceptSelfSigned = new JCheckBox();
	private JCheckBox checkCRL = new JCheckBox();
	private JCheckBox checkOCSP = new JCheckBox();
	private JButton showCert = new JButton();
	private JButton uploadCert = new JButton();
	private JFileChooser fileChooser = new JFileChooser();
	private JButton fileButton = new JButton();
	private JTextField fileField = new JTextField();
	private JScrollPane scrollPane;
	private JLabel addCertLabel = new JLabel(Res.getString("label.certificate.add.certificate.to.truststore"));
	private JPanel filePanel = new JPanel();
	public CertificatesManagerSettingsPanel(LocalPreferences localPreferences, JDialog optionsDialog) {

		this.localPreferences = localPreferences;
		certControll = new CertificateController(localPreferences);
		setLayout(new GridBagLayout());

		certTable.setModel(certControll.getTableModel());
		scrollPane = new JScrollPane(certTable);
		certTable.setFillsViewportHeight(true);

		ResourceUtils.resButton(acceptAll, Res.getString("checkbox.accept.all"));
		ResourceUtils.resButton(acceptExpired, Res.getString("checkbox.accept.expired"));
		ResourceUtils.resButton(acceptRevoked, Res.getString("checkbox.accept.invalid"));
		ResourceUtils.resButton(acceptSelfSigned, Res.getString("checkbox.accept.self.signed"));
		ResourceUtils.resButton(checkCRL, Res.getString("checkbox.check.crl"));
		ResourceUtils.resButton(checkOCSP, Res.getString("checkbox.check.ocsp"));
		ResourceUtils.resButton(showCert, Res.getString("button.show.certificate"));
		ResourceUtils.resButton(fileButton, Res.getString("label.choose.file"));

		acceptAll.addActionListener(this);
		certTable.addMouseListener(this);
		showCert.addActionListener(this);
		fileButton.addActionListener(this);

		filePanel.setLayout(new GridBagLayout());
		filePanel.add(fileField, new GridBagConstraints(0, 1, 1, 1, 0.1, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		filePanel.add(fileButton, new GridBagConstraints(1, 1, 2, 1, 0.0, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		filePanel.setBorder(
				BorderFactory.createTitledBorder(Res.getString("label.certificate.add.certificate.to.truststore")));

		add(scrollPane, new GridBagConstraints(0, 0, 6, 1, 1.0, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptAll, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptSelfSigned, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptExpired, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptRevoked, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(checkCRL, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(checkOCSP, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(showCert, new GridBagConstraints(2, 1, 2, 1, 1.0, 1.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 200), 0, 0));
		add(filePanel, new GridBagConstraints(2, 2, 2, 4, 0.0, 0.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == acceptAll && acceptAll.isSelected()) {
			acceptSelfSigned.setSelected(true);
			acceptExpired.setSelected(true);
			acceptRevoked.setSelected(true);

			acceptSelfSigned.setEnabled(false);
			acceptExpired.setEnabled(false);
			acceptRevoked.setEnabled(false);
		} else if (e.getSource() == acceptAll && !acceptAll.isSelected()) {
			acceptSelfSigned.setEnabled(true);
			acceptExpired.setEnabled(true);
			acceptRevoked.setEnabled(true);
		} else if (e.getSource() == showCert) {
			showCertificate();
		} else if (e.getSource() == fileButton) {
			int retVal = fileChooser.showOpenDialog(this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				fileField.setText(file.getAbsolutePath());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2) {
			JTable source = (JTable) e.getSource();
			if (e.getSource() == certTable && source.getSelectedColumn() != 3) {
				showCertificate();
			}
		}

	}

	/**
	 * Open dialog with certificate.
	 */
	private void showCertificate() {
		CertificateDialog certDialog = new CertificateDialog(localPreferences,
				certControll.getCertificates().get(certTable.getSelectedRow()));
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

}
