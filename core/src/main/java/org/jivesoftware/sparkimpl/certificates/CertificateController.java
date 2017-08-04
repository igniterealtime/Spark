package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
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
import java.util.Base64;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jivesoftware.Spark;
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
	public final static File TRUSTED = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + "truststore");
	public final static File BLACKLIST = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + "blacklist");
	public final static File EXCEPTIONS = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + "exceptions");
	public final static char[] passwd = "changeit".toCharArray();
	
	private KeyStore trustStore, blackListStore, exceptionsStore;
	
	//contain all certificates, used for help in managing certificates, but isn't directly displayed on the certificate table
	private List<CertificateModel> allCertificates = new LinkedList<>(); 
	private List<CertificateModel> trustedCertificates = new LinkedList<>(); // contain certificates which aren't revoked or exempted
	private List<CertificateModel> exemptedCertificates = new LinkedList<>(); // contain only certificates from exempted list
	private List<CertificateModel> blackListedCertificates = new LinkedList<>(); //contain only revoked certificates
	
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

    /**
     * Load KeyStores files.
     */
    public void loadKeysStores() {
        try (InputStream inputStram = new FileInputStream(TRUSTED)) {

            trustStore = KeyStore.getInstance("JKS");
            trustStore.load(inputStram, passwd);
            trustedCertificates = fillTableListWithKeyStoreContent(trustStore, trustedCertificates);

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Log.warning("TrustStore couldn't be loaded: maybe empty");
            try {
                trustStore.load(null, passwd);
            } catch (NoSuchAlgorithmException | CertificateException | IOException e1) {
                Log.warning("TrustStore couldn't be loaded: other bug");
            }
        }
        try (InputStream inputStram = new FileInputStream(EXCEPTIONS)) {

            exceptionsStore = KeyStore.getInstance("JKS");
            exceptionsStore.load(inputStram, passwd);
            exemptedCertificates = fillTableListWithKeyStoreContent(exceptionsStore, exemptedCertificates);

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Log.warning("ExceptionsStore couldn't be loaded: maybe empty");
            try {
                exceptionsStore.load(null, passwd);
            } catch (NoSuchAlgorithmException | CertificateException | IOException e1) {
                Log.warning("ExceptionsStore couldn't be loaded: other bug");
            }
        }
        try (InputStream inputStram = new FileInputStream(BLACKLIST)) {
         
            blackListStore = KeyStore.getInstance("JKS");
            blackListStore.load(inputStram, passwd);
            blackListedCertificates = fillTableListWithKeyStoreContent(blackListStore, blackListedCertificates);
       
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {

            try {
                Log.warning("BlackListStore couldn't be loaded: maybe empty");
                blackListStore.load(null, passwd);
            } catch (NoSuchAlgorithmException | CertificateException | IOException e1) {
                Log.warning("BlackListStore couldn't be loaded: other bug");
            }
        }

    }

    /**
     * Save KeyStores.
     */
    public void overWriteKeyStores() {
        try (OutputStream outputStream = new FileOutputStream(TRUSTED)) {
            trustStore.store(outputStream, passwd);

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Log.error("Couldn't save TrustStore");
        }

        try (OutputStream outputStream = new FileOutputStream(EXCEPTIONS)) {
            exceptionsStore.store(outputStream, passwd);

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Log.error("Couldn't save ExceptionsStore");
        }

        try (OutputStream outputStream = new FileOutputStream(BLACKLIST)) {
            blackListStore.store(outputStream, passwd);

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Log.error("Couldn't save BlackListStore");
        }

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
			@Override
			public boolean isCellEditable(int row, int column) {
			    return column !=2 ? false:true;
			}
		};

		tableModel.setColumnIdentifiers(COLUMN_NAMES);
		certEntry = new Object[NUMBER_OF_COLUMNS];

        if (trustedCertificates != null) {
            // put certificate from arrayList into rows with chosen columns
            for (CertificateModel cert : trustedCertificates) {
                tableModel.addRow(fillTableWithList(certEntry, cert));
            }
        }
        if (exemptedCertificates != null) {
            for (CertificateModel cert : exemptedCertificates) {
                tableModel.addRow(fillTableWithList(certEntry, cert));
            }
        }
        if (blackListedCertificates != null) {
            for (CertificateModel cert : blackListedCertificates) {
                tableModel.addRow(fillTableWithList(certEntry, cert));
            }
        }
    }

    private Object[] fillTableWithList(Object[] certEntry, CertificateModel cert) {
        if (cert.getSubjectCommonName() != null) {
            certEntry[0] = cert.getSubjectCommonName();
        } else {
            certEntry[0] = cert.getSubject();
        }
        certEntry[1] = cert.getValidityStatus();
        certEntry[2] = isOnExceptionList(cert);
        return certEntry;
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
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				Log.error("Error at moving certificate from trusted list to the exception list", ex);
			}
		} else {
			try {
				moveCertificate(EXCEPTIONS, TRUSTED);
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				Log.error("Error at moving certificate from exceptions list to trusted list", ex);
			}
		}
	}
	
	/**
	 * Return information if certificate is on exception list.
	 * 
	 * @param Certificate Model entry
	 */
	public boolean isOnExceptionList(CertificateModel cert) {
		return exemptedCertificates.contains(cert);
    }

    /**
     * Return information if certificate is on blacklist (revoked).
     * 
     * @param Certificate Model entry
     */
    public boolean isOnBlackList(CertificateModel cert) {
        return blackListedCertificates.contains(cert);
    }

	/**
	 * Add certificates from keyStore to list. Useful for displaying in certificate table.
	 * 
	 * @param KeyStore source keystore.
	 * @param List list which will be filled with certificate models. 
	 * @throws KeyStoreException 
	 */
	
	private List<CertificateModel> fillTableListWithKeyStoreContent(KeyStore keyStore, List<CertificateModel> list) throws KeyStoreException {

			Enumeration<String> store = keyStore.aliases();
			while (store.hasMoreElements()) {
				String alias = (String) store.nextElement();
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                CertificateModel certModel = new CertificateModel(certificate, alias);
                list.add(certModel);
                allCertificates.add(certModel);
            }
        
			return list;
	}

	/**
     * Return file path which contains certificate with given alias;
     * 
     * @param alias of the certificate
     * @return File path of KeyStore with certificate
     */
    private KeyStore getAliasKeyStore(String alias) {
        for (CertificateModel model : exemptedCertificates) {
            if (model.getAlias().equals(alias)) {
                return exceptionsStore;
            }
        }
        for (CertificateModel model : blackListedCertificates) {
            if (model.getAlias().equals(alias)) {
                return blackListStore;
            }
        }
        for (CertificateModel model : trustedCertificates) {
            if (model.getAlias().equals(alias)) {
                return trustStore;
            }
        }
        return null;
    }
    
	/**
	 * Return file path which contains certificate with given alias;
	 * 
	 * @param alias of the certificate
	 * @return File path of KeyStore with certificate
	 */
	private File getAliasKeyStorePath(String alias) {
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
		for (CertificateModel model : trustedCertificates) {
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
            KeyStore store = getAliasKeyStore(alias);            
                store.deleteEntry(alias);
                    JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.deleted"));
                    CertificateModel model = null;
                    for (CertificateModel certModel : allCertificates) {
                        if (certModel.getAlias().equals(alias)) {
                            model = certModel;
                        }
                    }
                    exemptedCertificates.remove(model);
                    trustedCertificates.remove(model);
                    blackListedCertificates.remove(model);
                    allCertificates.remove(model);
                }
		    refreshCertTable();
    }

    /**
     * Refresh certificate table to make visible changes in it's model
     */
    private void refreshCertTable() {
        createCertTableModel();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                resizeColumnWidth(CertificatesManagerSettingsPanel.getCertTable());
                CertificatesManagerSettingsPanel.getCertTable().setModel(tableModel);
                tableModel.fireTableDataChanged();
            }
        });
    }
    
    
    /**
     * Resizes certificate table to preferred width.
     */
	public void resizeColumnWidth(JTable table) {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                final TableColumnModel columnModel = table.getColumnModel();
                final int maxWidth = table.getParent().getWidth();
                columnModel.getColumn(1).setPreferredWidth(80);
                columnModel.getColumn(2).setPreferredWidth(60);
                columnModel.getColumn(0).setPreferredWidth(maxWidth - 140);
            }
        });
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
			String alias = allCertificates.get(row).getAlias();
			moveCertificate(source, target, alias);

	}
	/**
	 * This method transfer certificate with given alias to certificate blackList
	 * @param alias
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public void moveCertificateToBlackList(String alias) throws FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
		moveCertificate(getAliasKeyStorePath(alias), BLACKLIST, alias);
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
        KeyStore sourceStore = null;
        if (source.equals(TRUSTED)) {
            sourceStore = trustStore;
        } else if (source.equals(EXCEPTIONS)) {
            sourceStore = exceptionsStore;
        } else if (source.equals(BLACKLIST)) {
            sourceStore = blackListStore;
        }

        KeyStore targetStore = null;
        if (target.equals(TRUSTED)) {
            targetStore = trustStore;
        } else if (target.equals(EXCEPTIONS)) {
            targetStore = exceptionsStore;
        } else if (target.equals(BLACKLIST)) {
            targetStore = blackListStore;
        }
        X509Certificate cert = (X509Certificate) sourceStore.getCertificate(alias);
        targetStore.setCertificateEntry(alias, cert);
        sourceStore.deleteEntry(alias);
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
    public void addCertificateToKeystore(File file) throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, HeadlessException, InvalidNameException {
        if (file == null) {
            throw new IllegalArgumentException();
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate addedCert = (X509Certificate) cf.generateCertificate(inputStream);
            CertificateModel certModel = new CertificateModel(addedCert);
            if (checkForSameCertificate(addedCert) == false) {
                showCertificate(certModel, CertificateDialogReason.ADD_CERTIFICATE);
            }
            // value of addToKeyStore is changed by setter in CertificateDialog
            if (addToKeystore == true) {
                addToKeystore = false;
                
                String alias = useCommonNameAsAlias(addedCert);
                trustStore.setCertificateEntry(alias, addedCert);
                trustedCertificates.add(new CertificateModel(addedCert));
                refreshCertTable();
                JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.added"));
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
		for(CertificateModel model: allCertificates){
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
		for(CertificateModel model :allCertificates){
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
				allCertificates.get(CertificatesManagerSettingsPanel.getCertTable().getSelectedRow()), this, CertificateDialogReason.SHOW_CERTIFICATE);
	}

	/**
	 * Open dialog with certificate.
	 * 
	 * @param CertificateModel Model of the certificate which details are meant to be shown.
	 */
	public void showCertificate(CertificateModel certModel, CertificateDialogReason reason) {
		CertificateDialog certDialog = new CertificateDialog(localPreferences, certModel, this, reason);
	}
	
	public List<CertificateModel> getAllCertificates() {
		return allCertificates;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		CertificateController.tableModel = tableModel;
	}

	public boolean isAddToKeystore() {
		return addToKeystore;
	}

	public void setAddToKeystore(boolean addToKeystore) {
		this.addToKeystore = addToKeystore;
	}
}
