package org.jivesoftware.sparkimpl.certificates;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * This trust manager wrap around SparkExceptionsTrustManager. In case when SparkExceptionsTrustManager fail then this
 * TrustManager will provide certificates from TRUSTED KeyStore which are checked against data validity, revocation,
 * acceptance of self-signed certificates and basic constraints.
 * 
 * 
 * @author A
 *
 */
public class SparkTrustManager implements X509TrustManager {

    private LocalPreferences localPref = SettingsManager.getLocalPreferences();
    private boolean checkCRL;
    private boolean checkOCSP;
    private boolean acceptExpired;
    private boolean acceptNotValidYet;
    private boolean acceptRevoked;
    private boolean acceptSelfSigned;

    private CertStore crlStore;
    private X509TrustManager exceptionsTrustManager;
    private Provider bcProvider = new BouncyCastleProvider(); // bc provider for path validation
    private KeyStore trustStore;
    private CertificateController certControll = new CertificateController(localPref);

    public SparkTrustManager() {
        exceptionsTrustManager = new SparkExceptionsTrustManager();
        Security.addProvider(bcProvider);

        checkCRL = localPref.isCheckCRL();
        checkOCSP = localPref.isCheckOCSP();
        acceptExpired = localPref.isAcceptExpired();
        // acceptNotValidYet = localPref.isAcceptNotValidYet();
        acceptRevoked = localPref.isAcceptRevoked();
        acceptSelfSigned = localPref.isAcceptSelfSigned();

        loadTrustStore();
    }

    public static X509TrustManager[] getTrustManagerList() {
        return new X509TrustManager[] { new SparkTrustManager() };
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            // first check if certificate is accepted as as certificate from exceptions list, exceptionsTrustManager
            // will make use of chain provided by exceptions KeyStore
            exceptionsTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException ex) {
            // in case when certificate isn't on exceptions list then make use of this Trust Manager

            // check if certificate isn't self signed, self signed certificate still have to be in TrustStore to be
            // accepted
            if (chain.length == 1 && acceptSelfSigned == false) {
                throw new CertificateException("SelfSigned certificate");
            }

            // validate chain by date (expired/not valid yet)
            checkDateValidity(chain);

            // validate certificate path
            try {
                validatePath(chain);
            } catch (NoSuchAlgorithmException | KeyStoreException | InvalidAlgorithmParameterException
                    | CertPathValidatorException | CertPathBuilderException e) {
                Log.error("Validating path failed", e);
                throw new CertificateException("Certificate path validation failed", e);
            }
        }
        // check if have basic constraints
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] X509Certs = null;
        try {
            // See how many certificates are in the keystore.
            int numberOfEntry = trustStore.size();
            // If there are any certificates in the keystore.
            if (numberOfEntry > 0) {
                // Create an array of X509Certificates
                X509Certs = new X509Certificate[numberOfEntry];

                // Get all of the certificate alias out of the keystore.
                Enumeration<String> aliases = trustStore.aliases();

                // Retrieve all of the certificates out of the keystore
                // via the alias name.
                int i = 0;
                while (aliases.hasMoreElements()) {
                    X509Certs[i] = (X509Certificate) trustStore.getCertificate((String) aliases.nextElement());
                    i++;
                }

            }
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            X509Certs = null;
        }
        return X509Certs;
    }

    /**
     * Validate certificate path
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws InvalidAlgorithmParameterException
     * @throws CertPathValidatorException
     * @throws CertPathBuilderException
     * @throws CertificateException
     */
    private void validatePath(X509Certificate[] chain)
            throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException,
            CertPathValidatorException, CertPathBuilderException, CertificateException {

        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX", bcProvider);
        CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");

        X509CertSelector certSelector = new X509CertSelector();

        // set root CA certificate from chain for CertSelector so trust store must contain it
        certSelector.setCertificate(chain[chain.length - 1]);

        // checks against time validity aren't done here as are already done in checkDateValidity (X509Certificate[]
        // chain)
        certSelector.setCertificateValid(null);
        // create parameters using trustStore as source of Trust Anchors and using X509CertSelector
        PKIXBuilderParameters parameters = new PKIXBuilderParameters(trustStore, certSelector);

        if (acceptRevoked == false) {
            if (checkCRL) {
                // check add CRL store and download CRL list to this store.
                try {
                    loadCRL(chain);
                    parameters.addCertStore(crlStore);
                    if (checkOCSP) {
                        // check OCSP, important reminder if CRL is disabled then then OCSP will not work either, for
                        // reference:
                        // http://docs.oracle.com/javase/7/docs/technotes/guides/security/certpath/CertPathProgGuide.html#AppC
                        // parameters.setCertPathCheckers(checkers);
                    }

                } catch (CertStoreException | IOException | CRLException e) {
                    Log.error("Couldn't load crl", e);
                    throw new CertificateException();
                }

            } else {
                parameters.setRevocationEnabled(false);
            }

        } else {
            parameters.setRevocationEnabled(false);
        }
        try {
            CertPathBuilderResult pathResult = certPathBuilder.build(parameters);
            CertPath certPath = pathResult.getCertPath();

            PKIXCertPathValidatorResult validationResult = (PKIXCertPathValidatorResult) certPathValidator
                    .validate(certPath, parameters);
            X509Certificate trustedCert = validationResult.getTrustAnchor().getTrustedCert();

            if (trustedCert == null) {
                throw new CertificateException("certificate path failed: Trusted CA is NULL");
            }
            // check if all certificates in path have Basic Constraints, only certificate that isn't required to have
            // this extension is last certificate: root CA
            for (int i = 0; i < chain.length - 1; i++) {
                checkBasicConstraints(chain[i]);
            }
        } catch (CertificateRevokedException e) {
            Log.warning("Certificate was revoked", e);
            // moveToBlackList(cert);
            throw new CertificateException("Certificate was revoked");
        }
    }

    /**
     * check time date validity of certificates
     * 
     * @throws CertificateException
     */
    private void checkDateValidity(X509Certificate[] chain) throws CertificateException {

        for (X509Certificate cert : chain) {
            // expiration check
            try {
                cert.checkValidity();
            } catch (CertificateExpiredException e) {
                Log.warning("Certificate is expired " + cert.getSubjectX500Principal().getName().toString(), e);
                if (acceptExpired == false) {
                    throw new CertificateException("Certificate is expired");
                }
            } catch (CertificateNotYetValidException e) {
                Log.warning("Certificate is not valid yet " + cert.getSubjectX500Principal().getName().toString(), e);
                if (acceptNotValidYet == false) {
                    throw new CertificateException("Certificate is not valid yet");
                }
            }

        }
    }

    /**
     * Check if certificate have basic constraints exception.
     * 
     * @param chain
     *            - with certificates given by server
     * @throws CertificateException
     */
    private void checkBasicConstraints(X509Certificate cert) throws CertificateException {
        if (cert.getBasicConstraints() != -1) {
            throw new CertificateException("Certificate have no basic constraints");
        }
    }

    /**
     * loads truststore
     */
    private void loadTrustStore() {
        try (FileInputStream inputStream = new FileInputStream(CertificateController.TRUSTED)) {
            trustStore = KeyStore.getInstance("JKS");
            trustStore.load(inputStream, CertificateController.passwd);
        } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException e) {
            Log.error("Error at accesing truststore", e);
        }
    }

    private void loadCRL(X509Certificate[] chain) throws IOException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, CertStoreException, CRLException, CertificateException {

        Collection<X509CRL> crlCollection = new ArrayList<>();

        // for each certificate in chain
        for (X509Certificate cert : chain) {
            if (cert.getExtensionValue(Extension.cRLDistributionPoints.getId()) != null) {
                ASN1Primitive primitive = JcaX509ExtensionUtils
                        .parseExtensionValue(cert.getExtensionValue(Extension.cRLDistributionPoints.getId()));
                // extract distribution point extension
                CRLDistPoint distPoint = CRLDistPoint.getInstance(primitive);
                DistributionPoint[] dp = distPoint.getDistributionPoints();
                // each distribution point extension can hold number of distribution points
                for (DistributionPoint d : dp) {
                    DistributionPointName dpName = d.getDistributionPoint();
                    // Look for URIs in fullName
                    if (dpName != null && dpName.getType() == DistributionPointName.FULL_NAME) {
                        GeneralName[] genNames = GeneralNames.getInstance(dpName.getName()).getNames();
                        // Look for an URI
                        for (GeneralName genName : genNames) {
                            // extract url
                            URL url = new URL(genName.getName().toString());
                            try {
                                // download from Internet to the collection
                                crlCollection.add(downloadCRL(url));
                            } catch (CertificateException | CRLException e) {
                                throw new CRLException("Couldn't download CRL");
                            }
                        }
                    }
                }
            } else {
                Log.warning("Certificate " + cert.getSubjectX500Principal().getName().toString() + " have no CRLs");
            }
            // parameters for cert store is collection type, using collection with crl create parameters
            CollectionCertStoreParameters params = new CollectionCertStoreParameters(crlCollection);
            // this parameters are next used for creation of certificate store with crls
            crlStore = CertStore.getInstance("Collection", params);
        }
    }

    /**
     * Move certificate to the blacklist of the revoked certificates.
     * 
     * @param cert
     *            - certificate which is meant to move into blacklist
     * @throws FileNotFoundException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    private void moveToBlackList(X509Certificate cert) throws FileNotFoundException, KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        certControll.moveCertificateToBlackList(trustStore.getCertificateAlias(cert));
    }

    /**
     * Downloads a CRL from given URL
     * 
     * @param url
     *            - the web address with given CRL
     * @throws IOException
     * @throws CertificateException
     * @throws CRLException
     */
    private X509CRL downloadCRL(URL url) throws IOException, CertificateException, CRLException {

        try (InputStream crlStream = url.openStream()) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509CRL) cf.generateCRL(crlStream);
        }
    }

}
