package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	public final static File TRUSTED = new File(System.getProperty("user.dir") +"\\src\\main\\security\\truststore");
	public final static File BLACKLIST = new File(System.getProperty("user.dir") +"\\src\\main\\security\\blacklist");
	public final static File EXCEPTIONS = new File(System.getProperty("user.dir") +"\\src\\main\\security\\exceptions");
	public final static char[] passwd = "changeit".toCharArray();
	
	
	private List<CertificateModel> certificates = new ArrayList<>(); // contain all certificates from all keystores
	private List<CertificateModel> exemptedCertificates = new ArrayList<>(); // contain only certificates from exempted list
	private List<CertificateModel> blackListedCertificates = new ArrayList<>(); //contain only revoked certificates
	
	private static DefaultTableModel tableModel;
	private Object[] certEntry;
	private LocalPreferences localPreferences;
	private static final String[] COLUMN_NAMES = { Res.getString("table.column.certificate.subject"),
			Res.getString("table.column.certificate.validity"), Res.getString("table.column.certificate.exempted") };
	private static final int NUMBER_OF_COLUMNS = COLUMN_NAMES.length;
	private boolean addToKeystore;

	public CertificateController(LocalPreferences localPreferences) {
		if (localPreferences == null) {
			throw new IllegalArgumentException("localPreferences cannot be null");
		}
		this.localPreferences = localPreferences;
	}
	
	public void createCertTableModel(){
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
				certEntry[2] = isOnExceptionList(cert);
				tableModel.addRow(certEntry);
			}
		}
	}
	/**
	 * If argument is true then move certificate to the exceptions Keystore, if false then move to the trusted Keystore.
	 * Useful for checkboxes where it's selected value indicates where certificate should be moved.
	 * @param checked should it be moved?
	 */
	public void addOrRemoveFromExceptionList(boolean checked) {
		int row = CertificatesManagerSettingsPanel.getCertTable().getSelectedRow();
		if (checked) {
			try {
				moveCertificate(TRUSTED, EXCEPTIONS);
				exemptedCertificates.add(certificates.get(row));
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				Log.error("Error at moving certificate from trusted list to the exception list", ex);
			}
		} else {
			try {
				moveCertificate(EXCEPTIONS, TRUSTED);
				exemptedCertificates.remove(certificates.get(row));
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				Log.error("Error at moving certificate from exceptions list to trusted list", ex);
			}
		}
	}
	
	/**
	 * Return information if certificate is on exception list.
	 * 
	 * @param alias of the certificate
	 */
	public boolean isOnExceptionList(CertificateModel cert) {
		return exemptedCertificates.contains(cert);
	}

	/**
	 * Add certificates from keyStore to
	 * 
	 * @param storePath path of the store which will fill certificate table
	 */
	
	private void fillCertTableWithKeyStoreContent(File storePath) {

		try (FileInputStream input = new FileInputStream(storePath)) {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(input, passwd);

			Enumeration store = keyStore.aliases();
			while (store.hasMoreElements()) {
				String alias = (String) store.nextElement();
				X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
				CertificateModel certModel = new CertificateModel(certificate, alias);
				certificates.add(certModel);
				if(storePath.equals(EXCEPTIONS)){
					exemptedCertificates.add(certModel);
				}
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Log.warning("Cannot access Truststore, it might be not set up", e);
		}

	}

	/**
	 * Return file which contains certificate with given alias;
	 * 
	 * @param alias of the certificate
	 * @return File path of KeyStore with certificate
	 */
	private File getAliasKeyStore(String alias) {
		for (CertificateModel model : exemptedCertificates) {
			if (model.getAlias().equals(alias)) {
				return EXCEPTIONS;
			}
		}
		for (CertificateModel model : blackListedCertificates) {
			if (model.getAlias().equals(alias)) {
				return BLACKLIST;
			}
		}
		for (CertificateModel model : certificates) {
			if (model.getAlias().equals(alias)) {
				return TRUSTED;
			}
		}
		return null;
	}

	/**
	 * This method delete certificate with provided alias from the Truststore
	 * 
	 * @param alias Alias of the certificate to delete
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 */
	public void deleteCertificate(String alias) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogValue = JOptionPane.showConfirmDialog(null, Res.getString("dialog.certificate.sure.to.delete"), null, dialogButton);
		if (dialogValue == JOptionPane.YES_OPTION) {
			File storeFile = getAliasKeyStore(alias); 
			try(FileInputStream inputStream = new FileInputStream(storeFile)){
			KeyStore store = KeyStore.getInstance("JKS");
			store.load(inputStream, passwd);
			store.deleteEntry(alias);
			try (FileOutputStream outputStream = new FileOutputStream(storeFile)) {
				store.store(outputStream, localPreferences.getTrustStorePassword().toCharArray());
				JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.deleted"));
			}
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
	public void moveCertificate(File source, File target) throws FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		
			int row = CertificatesManagerSettingsPanel.getCertTable().getSelectedRow();
			String alias = certificates.get(row).getAlias();
			moveCertificate(source, target, alias);

	}

	public void moveCertificateToBlackList(String alias) throws FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
		moveCertificate(getAliasKeyStore(alias), BLACKLIST, alias);
	}
	
	/**
	 * This method transfer certificate from source KeyStore to target KeyStore.
	 * 
	 * @param source File with source KeyStore
	 * @param target File with target KeyStore
	 * @param alias Alias of the certificate meant to move
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 */
	public void moveCertificate(File source, File target, String alias) throws FileNotFoundException, IOException,
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
	 * @param file File with certificate that is added
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
				
				try (InputStream trustStoreStream = new FileInputStream(TRUSTED)) {

					KeyStore trustStore = KeyStore.getInstance("JKS");
					trustStore.load(trustStoreStream, passwd);
					
					String alias = useCommonNameAsAlias(addedCert);
					trustStore.setCertificateEntry(alias, addedCert);
					try (OutputStream outputStream = new FileOutputStream(TRUSTED)) {
						trustStore.store(outputStream, passwd);
						certificates.add(new CertificateModel(addedCert));
						JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.added"));
					}
				}
			}
		}
	}
	
	/**
	 * Extract from certificate common name ("CN") and returns it to use as certificate name.
	 * This method also assure that it will not add second same alias to Truststore by adding number to alias. 
	 * In case when common name cannot be extracted method will return "cert{number}".
	 * 
	 * @param cert Certificate which Common Name is meant to use
	 * @return String Common Name of the certificate
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
	 * @param alias Alias of the certificate which is looked for in the model list
	 * @return True if KeyStore contain the same alias.
	 * @throws HeadlessException
	 * @throws KeyStoreException
	 */
	private boolean checkForSameAlias(String alias) throws HeadlessException, KeyStoreException {
		for(CertificateModel model: certificates){
			if(model.getAlias().equals(alias)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if this certificate already exist in Truststore.
	 * 
	 * @param alias Alias of the certificate for which it method look in the model list
	 * @return true if KeyStore already have this certificate.
	 * @throws KeyStoreException 
	 */	
	private boolean checkForSameCertificate(X509Certificate addedCert) throws KeyStoreException{
		// check if this certificate isn't already added to Truststore
		for(CertificateModel model :certificates){
			X509Certificate certificateCheck = model.getCertificate();
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
	 * @param CertificateModel Model of the certificate which details are meant to be shown.
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
