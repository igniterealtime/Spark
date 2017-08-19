package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.ui.login.CertificateDialog;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

/**
 * This class contain some methods and fields that are common for CertificateController and IdentityController classes.
 * According to MVC it stands as Controller behind CertificatesManagerSettingsPanel and
 * MutualAuthenticationSettingsPanel which are Views.
 * 
 * @author Paweł Ścibiorski
 *
 */
public abstract class CertManager {

    protected LocalPreferences localPreferences;
    public final static char[] passwd = "changeit".toCharArray();       
    protected boolean addToKeystore;
    
    //contain all certificates, used for help in managing certificates, but isn't directly displayed on the certificate table
    protected List<CertificateModel> allCertificates = new LinkedList<>(); 
    protected static DefaultTableModel tableModel;
    
    public abstract void deleteEntry(String alias) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException;
    public abstract void addOrRemoveFromExceptionList(boolean checked);
    public abstract boolean isOnExceptionList(CertificateModel cert);

    protected abstract void refreshCertTable();

    public abstract void addEntryToKeyStore(File file)
            throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, HeadlessException,
            InvalidNameException, UnrecoverableKeyException, InvalidKeySpecException;
/**
 * Check if there is certificate entry in KeyStore with the same alias.
 * @param alias which is checked if it already exist in keystore
 * @return
 * @throws HeadlessException
 * @throws KeyStoreException
 */
    protected abstract boolean checkForSameAlias(String alias) throws HeadlessException, KeyStoreException;

    /**
     * Save the KeyStores.
     */
    public abstract void loadKeyStores();
    public abstract void overWriteKeyStores();

    public void setAddToKeystore(boolean addToKeystore) {
        this.addToKeystore = addToKeystore;
    }
    
    public boolean isAddToKeystore() {
        return addToKeystore;
    }
    
    /**
     * Check if this certificate already exist in Truststore.
     * 
     * @param alias Alias of the certificate for which it method look in the model list
     * @return true if KeyStore already have this certificate.
     * @throws KeyStoreException 
     */ 
    protected boolean checkForSameCertificate(X509Certificate addedCert) throws KeyStoreException{
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
    protected String useCommonNameAsAlias(X509Certificate cert) throws InvalidNameException, HeadlessException, KeyStoreException {
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
     * Open dialog with certificate.
     * 
     */
    public abstract void showCertificate();
    
    /**
     * Open dialog with certificate.
     * 
     * @param CertificateModel Model of the certificate which details are meant to be shown.
     */
    public void showCertificate(CertificateModel certModel, CertificateDialogReason reason) {

        new CertificateDialog(localPreferences, certModel, this, reason);
    }
    
    protected KeyStore openKeyStore(File file){
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
            // checking if length >0 prevents EOFExceptions
            if (file.exists() && !file.isDirectory() && file.length() > 0) {
                try (InputStream inputStream = new FileInputStream(file)) {
                    keyStore.load(inputStream, passwd);
                } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                    Log.error("Error at accesing exceptions KeyStore", e);
                }
            } else {
                keyStore.load(null, passwd); // if cannot open KeyStore then new empty one will be created
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            Log.warning("Cannot create exceptions KeyStore", e);
        }
        return keyStore;
    }
    
    /**
     * Add certificates from keyStore to list. Useful for displaying in certificate table.
     * 
     * @param KeyStore source keystore.
     * @param List list which will be filled with certificate models. 
     * @throws KeyStoreException 
     */

    protected List<CertificateModel> fillTableListWithKeyStoreContent(KeyStore keyStore, List<CertificateModel> list) {
        if (keyStore != null) {
            Enumeration<String> store;
            try {
                store = keyStore.aliases();

                while (store.hasMoreElements()) {
                    String alias = (String) store.nextElement();
                    X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                    CertificateModel certModel = new CertificateModel(certificate, alias);
                    if (list != null) {
                        list.add(certModel);
                    }
                    allCertificates.add(certModel);
                }
            } catch (KeyStoreException e) {
                Log.error("Cannot read KeyStore", e);
            }
        }
        return list;
    }
}
