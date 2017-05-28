package org.jivesoftware.spark.ui.login;

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
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.sparkimpl.certificates.CertificateController;
import org.jivesoftware.sparkimpl.certificates.CertificateModel;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

public class CertificatesManagerSettingsPanel extends JPanel implements ActionListener {

	private final static Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
	private final LocalPreferences localPreferences;
	private JDialog optionsDialog;
	private CertificateController certControll = new CertificateController();
	private JTable certTable = new JTable();
	private JScrollPane scrollPane;

	public CertificatesManagerSettingsPanel(LocalPreferences localPreferences, JDialog optionsDialog) {

		this.localPreferences = localPreferences;
		this.optionsDialog = optionsDialog;

		certTable.setModel(certControll.getTableModel());
		scrollPane = new JScrollPane(certTable);
		certTable.setFillsViewportHeight(true);
		this.add(scrollPane);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
