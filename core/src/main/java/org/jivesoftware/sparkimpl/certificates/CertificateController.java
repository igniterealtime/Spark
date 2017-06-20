package org.jivesoftware.sparkimpl.certificates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.ui.login.CertificateDialog;
import org.jivesoftware.spark.ui.login.CertificatesManagerSettingsPanel;
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
	private List<CertificateModel> certificates;
	private DefaultTableModel tableModel;
	private Object[] certEntry;
	private LocalPreferences localPreferences;
	private static final String[] COLUMN_NAMES = { Res.getString("table.column.certificate.alias"), Res.getString("table.column.certificate.issuer"),
			Res.getString("table.column.certificate.subject"), Res.getString("table.column.certificate.valid"),
			Res.getString("table.column.certificate.exempted") };
	private static final int NUMBER_OF_COLUMNS = COLUMN_NAMES.length;
	private KeyStore trustStore;
	private boolean addToKeystore;

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
					return String.class;
				case 4:
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
				certificates.add(new CertificateModel(certificate, alias));
			}

			// put certificate from arrayList into rows with chosen columns
			for (CertificateModel cert : certificates) {
				certEntry[0] = cert.getAlias();
				certEntry[1] = cert.getIssuer();
				certEntry[2] = cert.getSubject();
				certEntry[3] = cert.isValid();
				certEntry[4] = cert.isExempted();
				tableModel.addRow(certEntry);
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Log.warning("Cannot acces Truststore, it might be not set up", e);
		}
	}
  
	/**
	 * This method add certifiate from file ((*.cer), (*.crt), (*.der)) to Truststore.
	 * 
	 * @param file
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	
	public void addCertificateToKeystore(File file) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException{
		if(file == null){
			throw new IllegalArgumentException();
		}
		try (InputStream inputStream = new FileInputStream(file)) {
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate addedCert = (X509Certificate) cf.generateCertificate(inputStream);
			showCertificate(new CertificateModel(addedCert));
			// value of addToKeyStore is changed by setter in CertificateDialog
			if (addToKeystore == true) {
				addToKeystore = false;
				String alias = JOptionPane.showInputDialog(null, Res.getString("dialog.provide.alias.and.confirm" ));
				if (alias != null) {
					trustStore.setCertificateEntry(alias, addedCert);
					try (FileOutputStream outputStream = new FileOutputStream(localPreferences.getTrustStorePath())) {
						trustStore.store(outputStream, localPreferences.getTrustStorePassword().toCharArray());
					}
				}
			}
		}
	}

	/**
	 * Open dialog with certificate.
	 */
	public void showCertificate() {
		CertificateDialog certDialog = new CertificateDialog(localPreferences,
				certificates.get(CertificatesManagerSettingsPanel.getCertTable().getSelectedRow()));
	}

	/**
	 * Open dialog with certificate.
	 * 
	 * @param CertificateModel
	 */
	public void showCertificate(CertificateModel certModel) {
		CertificateDialog certDialog = new CertificateDialog(localPreferences, certModel, this);
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

	public boolean isAddToKeystore() {
		return addToKeystore;
	}

	public void setAddToKeystore(boolean addToKeystore) {
		this.addToKeystore = addToKeystore;
	}
}
