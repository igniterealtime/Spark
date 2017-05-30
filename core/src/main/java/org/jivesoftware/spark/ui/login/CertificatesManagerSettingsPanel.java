package org.jivesoftware.spark.ui.login;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.certificates.CertificateController;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

/**
 * This class serve as visual in implementation of manageable list of
 * certificates.
 * 
 * @author Paweł Ścibiorski
 *
 */
public class CertificatesManagerSettingsPanel extends JPanel implements ActionListener {

	private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
	private final LocalPreferences localPreferences;
	private JDialog optionsDialog;
	private CertificateController certControll = new CertificateController();
	private JTable certTable = new JTable();
	private JCheckBox acceptAll = new JCheckBox();
	private JCheckBox acceptExpired = new JCheckBox();
	private JCheckBox acceptRevoked = new JCheckBox();
	private JCheckBox acceptSelfSigned = new JCheckBox();
	private JCheckBox checkCRL = new JCheckBox();
	private JCheckBox checkOCSP = new JCheckBox();
	private JScrollPane scrollPane;

	public CertificatesManagerSettingsPanel(LocalPreferences localPreferences, JDialog optionsDialog) {

		this.localPreferences = localPreferences;
		this.optionsDialog = optionsDialog;
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

		acceptAll.addActionListener(this);

		add(scrollPane, new GridBagConstraints(0, 0, 6, 1, 0.0, 1.0, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptAll, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptSelfSigned, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptExpired, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(acceptRevoked, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(checkCRL, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
		add(checkOCSP, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.5, WEST, HORIZONTAL, DEFAULT_INSETS, 0, 0));
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
		}

	}

}
