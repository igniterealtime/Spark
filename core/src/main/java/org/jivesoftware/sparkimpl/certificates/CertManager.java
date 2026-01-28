package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.ui.login.CertificateDialog;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

/**
 * This class contains some methods and fields that are common for CertificateController and IdentityController classes.
 * According to MVC, it stands as Controller behind CertificatesManagerSettingsPanel and
 * MutualAuthenticationSettingsPanel which are Views.
 *
 * @author Paweł Ścibiorski
 */
public abstract class CertManager {

    protected LocalPreferences localPreferences;
    public final static char[] passwd = "changeit".toCharArray();
    protected boolean addToKeystore;

    /**
     * BLACKLIST is a KeyStore with revoked certificates. It isn't directly displayed, but when other
     * KeyStores content is added, then it is compared with this list and information about revocation
     * is added to certificate status
     */
    public final static File BLACKLIST = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + "blacklist");

    /**
     * Contain all certificates, used for help in managing certificates, but isn't directly displayed on the certificate table
     */
    protected KeyStore blackListStore;

    protected final List<CertificateModel> allCertificates = new LinkedList<>();

    /**
     * Contains only revoked certificates
     */
    protected final List<CertificateModel> blackListedCertificates = new LinkedList<>();

    protected DefaultTableModel tableModel;

    public abstract void deleteEntry(String alias) throws KeyStoreException;

    public abstract void addOrRemoveFromExceptionList(boolean checked);

    public abstract boolean isOnExceptionList(CertificateModel cert);

    public abstract void createTableModel();

    protected abstract void refreshCertTable();

    public abstract void addEntryFileToKeyStore(File file)
        throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, HeadlessException,
        InvalidNameException, InvalidKeySpecException;

    /**
     * Check if there is a certificate entry in KeyStore with the same alias.
     *
     * @param alias which is checked if it already exist in keystore
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
     * Check if this certificate already exists in TrustStore.
     *
     * @param addedCert the certificate for which it method look in the model list
     * @return true if KeyStore already have this certificate.
     */
    protected boolean checkForSameCertificate(X509Certificate addedCert) {
        // check if this certificate isn't already added to TrustStore
        for (CertificateModel model : allCertificates) {
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
     * Check if a given certificate is revoked looking on it's CRL (if exist).
     *
     * @param cert which is validated
     * @return true if the certificate is revoked, false if it isn't or CRL cannot be accessed (because it might not exist).
     */
    public boolean checkRevocation(X509Certificate cert) {
        boolean revoked = false;
        try {
            SparkTrustManager man = new SparkTrustManager();
            Collection<X509CRL> crls = man.loadCRL(new X509Certificate[]{cert});
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            for (X509CRL crl : crls) {
                if (crl.isRevoked(cert)) {
                    revoked = true;
                    break;
                }
            }
        } catch (Exception e) {
            Log.warning("Cannot check validity", e);
        }
        return revoked;
    }

    /**
     * This method adds certificate to blackList
     *
     * @param cert the certificate to be added to the blacklist
     */
    public void addCertificateToBlackList(X509Certificate cert) throws KeyStoreException,
        HeadlessException, InvalidNameException {
        blackListStore.setCertificateEntry(useCommonNameAsAlias(cert), cert);
        blackListedCertificates.add(new CertificateModel(cert));
    }


    /**
     * Extract from the certificate its Common Name ("CN") and return it to use as certificates name.
     * This method also assures that it will not add the second same alias to TrustStore by adding a number to alias.
     * In case when a Common Name cannot be extracted, the method will return "cert{number}".
     *
     * @param cert Certificate which Common Name is meant to use
     * @return String Common Name of the certificate
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
                    alias = alias + i;
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
                alias = alias + i;
                i++;
            }
        }
        return alias;
    }

    /**
     * Open dialog with certificate.
     */
    public abstract void showCertificate();

    /**
     * Open dialog with certificate.
     *
     * @param certModel Model of the certificate which details are meant to be shown.
     * @param reason    The reason for the Certificate dialog to be shown.
     */
    public CertificateDialog showCertificate(CertificateModel certModel, CertificateDialogReason reason) {
        return new CertificateDialog(localPreferences, certModel, this, reason);
    }

    protected KeyStore openKeyStore(File file) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
            // checking if length >0 prevents EOFExceptions
            if (file.exists() && !file.isDirectory() && file.length() > 0) {
                try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                    keyStore.load(inputStream, passwd);
                } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                    // try to open without a password
                    try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                        keyStore.load(inputStream, "".toCharArray());
                    } catch (IOException | NoSuchAlgorithmException | CertificateException e2) {
                        Log.error("Unable to access KeyStore " + file, e2);
                    }
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
     * Add certificates from keyStore to the list. Useful for displaying in the certificate table.
     *
     * @param keyStore source keystore.
     * @param list     list which will be filled with certificate models.
     */
    protected List<CertificateModel> fillTableListWithKeyStoreContent(KeyStore keyStore, List<CertificateModel> list) {
        if (keyStore != null) {
            try {
                Enumeration<String> store = keyStore.aliases();
                while (store.hasMoreElements()) {
                    String alias = store.nextElement();
                    X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                    CertificateModel certModel = new CertificateModel(certificate, alias);
                    certModel.setRevoked(blackListStore.getCertificateAlias(certificate) != null);
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

    protected void saveKeyStore(KeyStore keyStore, File file) {
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            if (keyStore != null) {
                keyStore.store(outputStream, passwd);
            }
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Log.error("Couldn't save KeyStore", e);
        }
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
