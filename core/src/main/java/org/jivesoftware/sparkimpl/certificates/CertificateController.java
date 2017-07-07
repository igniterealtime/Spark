package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
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
	public final static String TRUSTED = System.getProperty("user.dir") + "\\src\\main\\security\\truststore";
	public final static String BLACKLIST = System.getProperty("user.dir") + "\\src\\main\\security\\blacklist";
	public final static String EXCEPTIONS = System.getProperty("user.dir") + "\\src\\main\\security\\exceptions";
	File directory = new File("");
	public static String trustStorePath;
	private final static char[] passwd = "changeit".toCharArray();
	
	
	private List<CertificateModel> certificates;
	private DefaultTableModel tableModel;
	private Object[] certEntry;
	private LocalPreferences localPreferences;
	private static final String[] COLUMN_NAMES = { Res.getString("table.column.certificate.subject"),
			Res.getString("table.column.certificate.validity"), Res.getString("table.column.certificate.exempted") };
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
					return Boolean.class;
				default:
					throw new RuntimeException("Cannot assign classes for columns");

				}
			}
		};

		tableModel.setColumnIdentifiers(COLUMN_NAMES);
		certEntry = new Object[NUMBER_OF_COLUMNS];

		fillCertTableWithKeyStoreContent(TRUSTED);
		fillCertTableWithKeyStoreContent(EXCEPTIONS);
		fillCertTableWithKeyStoreContent(BLACKLIST);
		

		if (certificates != null) {
			// put certificate from arrayList into rows with chosen columns
			for (CertificateModel cert : certificates) {
				if (cert.getSubjectCommonName() != null) {
					certEntry[0] = cert.getSubjectCommonName();
				} else {
					certEntry[0] = cert.getSubject();
				}
				certEntry[1] = cert.getValidityStatus();
				certEntry[2] = cert.isExempted();
				tableModel.addRow(certEntry);
			}
		}
	}
	
	public void isOnExceptionList(X509Certificate cert){
		
	}
	
	
	/**
	 * Add certificates from keyStore to 
	 * @param storePath
	 */
	
	private void fillCertTableWithKeyStoreContent(String storePath) {
		try {
			FileInputStream input = new FileInputStream(storePath);
			trustStore = KeyStore.getInstance("JKS");
			trustStore.load(input, passwd);

			Enumeration store = trustStore.aliases();
			while (store.hasMoreElements()) {
				String alias = (String) store.nextElement();
				X509Certificate certificate = (X509Certificate) trustStore.getCertificate(alias);
				CertificateModel certModel = new CertificateModel(certificate, alias);
				if (storePath.equals(EXCEPTIONS)) {
					certModel.setExempted(true);
				}
				certificates.add(certModel);
			}

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Log.warning("Cannot access Truststore, it might be not set up", e);
		}

	}
	
	/**
	 * This method delete certificate with provided alias from the Truststore
	 * @param alias
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 */
	public void deleteCertificate(String alias) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogValue = JOptionPane.showConfirmDialog(null, Res.getString("dialog.certificate.sure.to.delete"), null, dialogButton);
		if (dialogValue == JOptionPane.YES_OPTION) {
			trustStore.deleteEntry(alias);
			try (FileOutputStream outputStream = new FileOutputStream(localPreferences.getTrustStorePath())) {
				trustStore.store(outputStream, localPreferences.getTrustStorePassword().toCharArray());
				JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.deleted"));
			}

		}
	} 
	/**
	 * This method transfer certificate from source KeyStore to target KeyStore.
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @throws FileNotFoundException 
	 */
	public void moveCertificate(String source, String target) throws FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		
			int row = CertificatesManagerSettingsPanel.getCertTable().getSelectedRow();
			String alias = certificates.get(row).getAlias();
			moveCertificate(source, target, alias);

	}

	/**
	 * This method transfer certificate from source KeyStore to target KeyStore.
	 * 
	 * @param source
	 * @param target
	 * @param alias
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 */
	public void moveCertificate(String source, String target, String alias) throws FileNotFoundException, IOException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException {
		if (!source.equals(TRUSTED) && !source.equals(BLACKLIST) && !source.equals(EXCEPTIONS)
				&& !target.equals(TRUSTED) && !target.equals(EXCEPTIONS) && !target.equals(BLACKLIST)) {
			throw new IllegalArgumentException();
		}
		try (FileInputStream sourceInput = new FileInputStream(source)) {
			KeyStore sourceStore = KeyStore.getInstance("JKS");
			sourceStore.load(sourceInput, passwd);
			X509Certificate cert = (X509Certificate) sourceStore.getCertificate(alias);

			try (FileInputStream targetInput = new FileInputStream(target)) {
				KeyStore targetStore = KeyStore.getInstance("JKS");
				try{
				targetStore.load(targetInput, passwd);	
				}catch (EOFException ex){
					Log.warning("Keystore was empty", ex);
					//in case when KeyStore is empty it have to be initialized with null value 
					targetStore.load(null, passwd);
				}
				targetStore.setCertificateEntry(alias, cert);
				sourceStore.deleteEntry(alias);
				sourceInput.close();
				targetInput.close();
				try (FileOutputStream sourceOutput = new FileOutputStream(source)) {

					try (FileOutputStream targetOutput = new FileOutputStream(target)) {
						sourceStore.store(sourceOutput, passwd);
						targetStore.store(targetOutput, passwd);
					}
				}

			}
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
	 * @throws InvalidNameException 
	 * @throws HeadlessException 
	 */	
	public void addCertificateToKeystore(File file) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, HeadlessException, InvalidNameException{
		if(file == null){
			throw new IllegalArgumentException();
		}
		try (InputStream inputStream = new FileInputStream(file)) {
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate addedCert = (X509Certificate) cf.generateCertificate(inputStream);
			if (checkForSameCertificate(addedCert) == false) {
				showCertificate(new CertificateModel(addedCert), CertificateDialogReason.ADD_CERTIFICATE);
			}
			// value of addToKeyStore is changed by setter in CertificateDialog
			if (addToKeystore == true) {
				addToKeystore = false;

				String alias = useCommonNameAsAlias(addedCert);
				trustStore.setCertificateEntry(alias, addedCert);
				try (FileOutputStream outputStream = new FileOutputStream(TRUSTED)) {
					trustStore.store(outputStream, passwd);
					JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.added"));
				}
			}
		}
	}
	/**
	 * Extract from certificate common name ("CN") and returns it to use as certificate name.
	 * This method also assure that it will not add second same alias to Truststore by adding number to alias. 
	 * In case when common name cannot be extracted method will return "cert{number}".
	 * 
	 * @param cert
	 * @return
	 * @throws InvalidNameException
	 * @throws HeadlessException
	 * @throws KeyStoreException
	 */
	private String useCommonNameAsAlias(X509Certificate cert) throws InvalidNameException, HeadlessException, KeyStoreException {
		String alias = null;
		String dn = cert.getSubjectX500Principal().getName();
		LdapName ldapDN = new LdapName(dn);
		for (Rdn rdn : ldapDN.getRdns()) {
			if (rdn.getType().equals("CN")) {
				alias = rdn.getValue().toString();
				int i = 1;
				while (checkForSameAlias(alias)) {
					alias = alias + Integer.toString(i);
					i++;
				}
				break;
			}
		}
		// Certificate subject doesn't have easy distinguishable common name then generate alias as cert{integer}
		if (alias == null) {
			alias = "cert";
			int i = 1;
			while (checkForSameAlias(alias)) {
				alias = alias + Integer.toString(i);
				i++;
			}
		}
		return alias;
	}

	/**
	 * Check if there is certificate entry in Truststore with the same alias.
	 * 
	 * @param alias
	 * @return
	 * @throws HeadlessException
	 * @throws KeyStoreException
	 */
	private boolean checkForSameAlias(String alias) throws HeadlessException, KeyStoreException {
		if (trustStore.getCertificate(alias) != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check if this certificate already exist in Truststore.
	 * 
	 * @param alias
	 * @return
	 * @throws KeyStoreException 
	 */	
	private boolean checkForSameCertificate(X509Certificate addedCert) throws KeyStoreException{
		// check if this certificate isn't already added to Truststore
		Enumeration storeCheck = trustStore.aliases();
		while (storeCheck.hasMoreElements()) {

			String aliasCheck = (String) storeCheck.nextElement();
			X509Certificate certificateCheck = (X509Certificate) trustStore.getCertificate(aliasCheck);
			String signature = Base64.getEncoder().encodeToString(certificateCheck.getSignature());
			String addedSignature = Base64.getEncoder().encodeToString(addedCert.getSignature());
			if (addedSignature.equals(signature)) {
				JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.cannot.have.copy"));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Open dialog with certificate.
	 */
	public void showCertificate() {
		CertificateDialog certDialog = new CertificateDialog(localPreferences,
				certificates.get(CertificatesManagerSettingsPanel.getCertTable().getSelectedRow()), this, CertificateDialogReason.SHOW_CERTIFICATE);
	}

	/**
	 * Open dialog with certificate.
	 * 
	 * @param CertificateModel
	 */
	public void showCertificate(CertificateModel certModel, CertificateDialogReason reason) {
		CertificateDialog certDialog = new CertificateDialog(localPreferences, certModel, this, reason);
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
