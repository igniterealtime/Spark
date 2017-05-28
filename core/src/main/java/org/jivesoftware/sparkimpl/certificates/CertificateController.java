package org.jivesoftware.sparkimpl.certificates;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.resource.Res;

/**
 * This class serve to extract certificates, storage them during runtime and
 * format them to put them in
 * 
 * @author Paweł Ścibiorski
 *
 */

public class CertificateController {
	List<CertificateModel> certificates;
	DefaultTableModel tableModel;
	Object[] certEntry;
	private static final String[] COLUMN_NAMES = { Res.getString("table.column.certificate.certificate"),
			Res.getString("table.column.certificate.subject"), Res.getString("table.column.certificate.valid"),
			Res.getString("table.column.certificate.exempted") };
	private static final int NUMBER_OF_COLUMNS = COLUMN_NAMES.length;

	public CertificateController() {

		certificates = new ArrayList<>();
		tableModel = new DefaultTableModel() {
			// return adequate classes for columns so last column is Boolean
			// displayed as checkbox
			public Class<?> getColumnClass(int column) {
				switch (column) {

				case 0:
					return String.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				case 3:
					return Boolean.class;
				default:
					return String.class;

				}
			}
		};

		tableModel.setColumnIdentifiers(COLUMN_NAMES);
		certEntry = new Object[NUMBER_OF_COLUMNS];
		// some certificates to fill table until creating extraction of
		// certificates
		certificates.add(new CertificateModel("3", "421214142", "364321641", "SHA256WITHRSA", "CN = someone", "someone",
				"22-02-2017", "22-02-2018", "RSA", "413525", true, new Boolean(false)));
		certificates.add(new CertificateModel("3", "421214142", "364321641", "SHA256WITHRSA", "CN = someone", "someone",
				"22-02-2017", "22-02-2018", "RSA", "413525", true, new Boolean(false)));
		certificates.add(new CertificateModel("3", "421214142", "364321641", "SHA256WITHRSA", "CN = someone", "someone",
				"22-02-2017", "22-02-2018", "RSA", "413525", true, new Boolean(true)));
		certificates.add(new CertificateModel("3", "421214142", "364321641", "SHA256WITHRSA", "CN = someone", "someone",
				"22-02-2017", "22-02-2018", "RSA", "413525", true, new Boolean(false)));

		// put certificate from arrayList into rows with chosen columns
		for (CertificateModel cert : certificates) {
			certEntry[0] = cert.getIssuer();
			certEntry[1] = cert.getSubject();
			certEntry[2] = cert.isValid();
			certEntry[3] = cert.isExempted();
			tableModel.addRow(certEntry);
		}
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}
}
