package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.naming.InvalidNameException;
import javax.net.ssl.KeyManagerFactory;
import javax.security.auth.x500.X500Principal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.ui.login.CertificateDialog;
import org.jivesoftware.spark.ui.login.MutualAuthenticationSettingsPanel;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

/**
 * This class is used for creating certificate signing request and self-signed certificates.
 */
public class IdentityController extends CertManager {
    private static String commonName, organizationUnit, organization, city, country;

    public final static File IDENTITY = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + "identitystore");
    public final static File SECURITY_DIRECTORY = new File(Spark.getSparkUserHome() + File.separator + "security");
    public static File CSR_FILE = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + commonName + "_csr.pem");
    public static File KEY_FILE = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + commonName + "_key.pem");
    public static File CERT_FILE = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + commonName + "_cert.pem");

    private KeyStore idStore;

    private final static String[] COLUMN_NAMES = {"Identity certificates"};
    private final static int NUMBER_OF_COLUMNS = COLUMN_NAMES.length;
    KeyPair keyPair;

    public IdentityController(LocalPreferences localPreferences) {
        Security.addProvider(new BouncyCastleProvider());
        loadKeyStores();
        if (localPreferences == null) {
            throw new IllegalArgumentException("localPreferences cannot be null");
        }
        this.localPreferences = localPreferences;
        createTableModel();
    }

    @Override
    public void loadKeyStores() {
        idStore = openKeyStore(IDENTITY);
        blackListStore = openKeyStore(BLACKLIST);
        fillTableListWithKeyStoreContent(idStore, null);
    }

    @Override
    public void overWriteKeyStores() {
        saveKeyStore(idStore, IDENTITY);
    }

    public KeyManagerFactory initKeyManagerFactory()
        throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, NoSuchProviderException {
        loadKeyStores();
        KeyManagerFactory keyManFact = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        keyManFact.init(idStore, IdentityController.passwd);
        return keyManFact;
    }

    public void setUpData(String commonName, String organizationUnit, String organization, String country,
                          String city) {
        IdentityController.commonName = commonName;
        IdentityController.organizationUnit = organizationUnit;
        IdentityController.organization = organization;
        IdentityController.country = country;
        IdentityController.city = city;
        CSR_FILE = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + commonName + "_csr.pem");
        KEY_FILE = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + commonName + "_key.pem");
        CERT_FILE = new File(Spark.getSparkUserHome() + File.separator + "security" + File.separator + commonName + "_cert.pem");
    }

    @Override
    public void createTableModel() {
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(COLUMN_NAMES);
        Object[] certEntry = new Object[NUMBER_OF_COLUMNS];
        for (CertificateModel cert : allCertificates) {
            if (cert.getSubjectCommonName() != null) {
                certEntry[0] = cert.getSubjectCommonName();
            } else {
                certEntry[0] = cert.getSubject();
            }
            tableModel.addRow(certEntry);
        }
    }

    @Override
    public void showCertificate() {
        new CertificateDialog(localPreferences,
            allCertificates.get(MutualAuthenticationSettingsPanel.getIdTable().getSelectedRow()), this,
            CertificateDialogReason.SHOW_ID_CERTIFICATE);
    }

    /**
     * Creates Certificate Signing Request.
     */
    public PKCS10CertificationRequest createCSR(KeyPair keyPair) throws OperatorCreationException {
        X500Principal principal = new X500Principal(createX500NameString());
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(principal, keyPair.getPublic());
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = csBuilder.build(keyPair.getPrivate());
        return p10Builder.build(signer);
    }

    /**
     * Create string representation of the X500Principal name with fields as Common Name, Organization Unit, Organization, Location and Country.
     *
     * @return String X500Name
     */
    private String createX500NameString() {
        if (commonName == null || commonName.isEmpty()) {
            throw new IllegalArgumentException("Common Name cannot be empty");
        }
        String sb = "CN=" + commonName;
        if (organizationUnit != null && !organizationUnit.isEmpty()) {
            sb += ", OU=" + organizationUnit;
        }
        if (organization != null && !organization.isEmpty()) {
            sb += ", O=" + organization;
        }
        if (city != null && !city.isEmpty()) {
            sb += ", L=" + city;
        }
        if (country != null && !country.isEmpty()) {
            sb += ", C=" + country;
        }
        return sb;
    }

    /**
     * This method adds certificate from file (*.pem) to Identity Store.
     */
    @Override
    public void deleteEntry(String alias) throws KeyStoreException {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogValue = JOptionPane.showConfirmDialog(null, Res.getString("dialog.certificate.sure.to.delete"), null, dialogButton);
        if (dialogValue == JOptionPane.YES_OPTION) {
            idStore.deleteEntry(alias);
            JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.deleted"));
            CertificateModel model = null;
            for (CertificateModel certModel : allCertificates) {
                if (certModel.getAlias().equals(alias)) {
                    model = certModel;
                    break;
                }
            }
            allCertificates.remove(model);
        }
        refreshCertTable();
    }

    @Override
    public void addOrRemoveFromExceptionList(boolean checked) {
    }

    @Override
    public boolean isOnExceptionList(CertificateModel cert) {
        return false;
    }

    /**
     * Refresh the certificate table to make visible changes in its model
     */
    @Override
    public void refreshCertTable() {
        createTableModel();
        SwingUtilities.invokeLater(() -> {
            MutualAuthenticationSettingsPanel.getIdTable().setModel(tableModel);
            tableModel.fireTableDataChanged();
        });
    }

    /**
     * This method extracts a key and certificate from the file (*.pem) and adds it to the Identity Store.
     *
     * @param file with a certificate that is added and private key entry
     */
    @Override
    public void addEntryFileToKeyStore(File file)
        throws IOException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, InvalidNameException {

        byte[] certAndKey = Files.readAllBytes(file.toPath());
        X509Certificate addedCert = parseCertificate(certAndKey);
        PrivateKey key = parseKey(certAndKey);
        addEntryToKeyStore(addedCert, key);
    }

    public PrivateKey parseKey(byte[] certAndKey) throws PEMException, InvalidKeySpecException, NoSuchAlgorithmException {
        String beginDelimiter = PemHelper.knowDelimeter(certAndKey, PemHelper.typeOfDelimeter.KEY_BEGIN);
        String endDelimiter = PemHelper.knowDelimeter(certAndKey, PemHelper.typeOfDelimeter.KEY_END);
        byte[] keyBytes = PemHelper.parseDERFromPEM(certAndKey, beginDelimiter, endDelimiter);
        return PemHelper.generatePrivateKeyFromDER(keyBytes);
    }

    public X509Certificate parseCertificate(byte[] certAndKey) throws PEMException, CertificateException {
        String beginDelimiter = PemHelper.knowDelimeter(certAndKey, PemHelper.typeOfDelimeter.CERT_BEGIN);
        String endDelimiter = PemHelper.knowDelimeter(certAndKey, PemHelper.typeOfDelimeter.CERT_END);
        byte[] certBytes = PemHelper.parseDERFromPEM(certAndKey, beginDelimiter, endDelimiter);
        return PemHelper.generateCertificateFromDER(certBytes);
    }

    public void addEntryToKeyStore(X509Certificate addedCert, PrivateKey key) throws HeadlessException, InvalidNameException, KeyStoreException {
        CertificateModel certModel = new CertificateModel(addedCert);
        CertificateDialog certDialog = null;
        if (!checkForSameCertificate(addedCert)) {
            certDialog = showCertificate(certModel, CertificateDialogReason.ADD_ID_CERTIFICATE);
        }
        // Adds certificate to keystore if user approves
        if (certDialog != null && certDialog.isAddCert()) {
            String alias = useCommonNameAsAlias(addedCert);
            X509Certificate[] chain = {addedCert};
            idStore.setKeyEntry(alias, key, passwd, chain);
            allCertificates.add(new CertificateModel(addedCert));
            refreshCertTable();
            JOptionPane.showMessageDialog(null, Res.getString("dialog.certificate.has.been.added.to.identity.store"));
        }
    }

    @Override
    protected boolean checkForSameAlias(String alias) throws HeadlessException, KeyStoreException {
        return idStore.containsAlias(alias);
    }

    public KeyPair createKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(2048, new SecureRandom());
        keyPair = generator.generateKeyPair();
        return keyPair;
    }

    public X509Certificate createSelfSignedCertificate(KeyPair keyPair) throws CertIOException, OperatorCreationException, CertificateException {

        long serial = System.currentTimeMillis();
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        X500Name name = new X500Name(createX500NameString());
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(name,
            BigInteger.valueOf(serial),
            new Date(System.currentTimeMillis() - 1000000000),
            new Date(System.currentTimeMillis() + 1000000000),
            name,
            keyInfo
        );
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certBuilder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = csBuilder.build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter().getCertificate(certHolder);
    }
}
