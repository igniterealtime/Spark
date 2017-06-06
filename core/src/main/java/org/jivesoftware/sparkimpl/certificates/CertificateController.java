package org.jivesoftware.sparkimpl.certificates;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

/**
 * This class serve to extract certificates, storage them during runtime and format them and support management of them.
 * Together with CertificateManagerSettingsPanel and CertificateModel Classes this apply MVC pattern.
 * 
 * @author Paweł Ścibiorski
 *
 */

public class CertificateController {
	List<CertificateModel> certificates;
	DefaultTableModel tableModel;
	Object[] certEntry;
	LocalPreferences localPreferences;
	private static final String[] COLUMN_NAMES = { Res.getString("table.column.certificate.certificate"),
			Res.getString("table.column.certificate.subject"), Res.getString("table.column.certificate.valid"),
			Res.getString("table.column.certificate.exempted") };
	private static final int NUMBER_OF_COLUMNS = COLUMN_NAMES.length;
	KeyStore trustStore;

	public CertificateController(LocalPreferences localPreferences) {
		if (localPreferences == null) {
			throw new IllegalArgumentException("localPreferences cannot be null");
		}

		this.localPreferences = localPreferences;
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

		try {
			FileInputStream input = new FileInputStream(localPreferences.getTrustStorePath());
			trustStore = KeyStore.getInstance(localPreferences.getPKIStore().toString());
			trustStore.load(input, localPreferences.getTrustStorePassword().toCharArray());

			Enumeration store = trustStore.aliases();
			while (store.hasMoreElements()) {
				String alias = (String) store.nextElement();
				X509Certificate certificate = (X509Certificate) trustStore.getCertificate(alias);
				certificates.add(new CertificateModel(certificate));
			}

			// put certificate from arrayList into rows with chosen columns
			for (CertificateModel cert : certificates) {
				certEntry[0] = cert.getIssuer();
				certEntry[1] = cert.getSubject();
				certEntry[2] = cert.isValid();
				certEntry[3] = cert.isExempted();
				tableModel.addRow(certEntry);
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Log.warning("Cannot acces Truststore, it might be not set up", e);
		}
	}

	public List<CertificateModel> getCertificates() {
		return certificates;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}
}
