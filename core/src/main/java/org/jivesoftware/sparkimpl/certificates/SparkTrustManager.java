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
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
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
    private boolean allowSoftFail;

    private CertStore crlStore;
    private X509TrustManager exceptionsTrustManager;
    private KeyStore trustStore, blackStore;
    private CertificateController certControll = new CertificateController(localPref);
    private Collection<X509CRL> crlCollection = new ArrayList<>();
    
    public SparkTrustManager() {
        exceptionsTrustManager = new SparkExceptionsTrustManager();
        checkCRL = localPref.isCheckCRL();
        checkOCSP = localPref.isCheckOCSP();
        acceptExpired = localPref.isAcceptExpired();
        acceptNotValidYet = localPref.isAcceptNotValidYet();
        acceptRevoked = localPref.isAcceptRevoked();
        acceptSelfSigned = localPref.isAcceptSelfSigned();
        allowSoftFail = localPref.isAllowSoftFail();

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

            // validate chain by date (expired/not valid yet)
            checkDateValidity(chain);

            // check if certificate isn't self signed, self signed certificate still have to be in TrustStore to be
            // accepted
            if (isSelfSigned(chain) == false) {
                // validate certificate path
                try {
                    validatePath(chain);

                } catch (NoSuchAlgorithmException | KeyStoreException | InvalidAlgorithmParameterException
                        | CertPathValidatorException | CertPathBuilderException e) {
                    Log.error("Validating path failed", e);
                    throw new CertificateException("Certificate path validation failed", e);

                }
            } else if (isSelfSigned(chain) && !acceptSelfSigned) {
                // Self Signed certificate while it isn't accepted
                throw new CertificateException("Self Signed certificate");

            } else if (isSelfSigned(chain) && acceptSelfSigned) {
                // check if certificate is in Keystore and check CRL, but do not validate path as certificate is Self
                // Signed important reminder: hostname validation must be also turned off to accept self signed
                // certificate
                List<X509Certificate> certList = new ArrayList<>(Arrays.asList(getAcceptedIssuers()));
                if (!certList.contains(chain[0])) {
                    throw new CertificateException("Certificate not in the TrustStore");
                }
                try {
                    loadCRL(chain);
                    for (X509CRL crl : crlCollection) {
                        if (crl.isRevoked(chain[0])) {
                            throw new CertificateException("Certificate is revoked");
                        }
                    }
                } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | CertStoreException
                        | CRLException | IOException e) {
                    Log.warning("Couldn't load CRL");
                }
            }else {
                throw new CertificateException("Certificate chain cannot be trusted");
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] X509Certs = null;
        try {
            // See how many certificates are in the keystore.
            int numberOfEntry = trustStore.size();
            if (acceptRevoked) {
                numberOfEntry += blackStore.size();
            }
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
                if (acceptRevoked) {
                    Enumeration<String> blackListedAliases = blackStore.aliases();
                    // Add all certificates from black list KeyStore
                    while (aliases.hasMoreElements()) {
                        X509Certs[i] = (X509Certificate) trustStore
                                .getCertificate((String) blackListedAliases.nextElement());
                        i++;
                    }
                }

            }
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            X509Certs = null;
        }
        return X509Certs;
    }

    /**
     * Return true if the certificate chain contain only one Self Signed certificate
     */
    private boolean isSelfSigned(X509Certificate[] chain) {
        return chain[0].getIssuerX500Principal().getName().equals(chain[0].getSubjectX500Principal().getName())
                && chain.length == 1;
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
        // PKIX algorithm is defined in rfc3280
        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
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
            // check OCSP, CRL serve as backup
            if (checkOCSP && checkCRL) {
                // OCSP checking is done according to Java PKI Programmer's Guide, PKIXRevocationChecker was added in Java 8:
                // https://docs.oracle.com/javase/8/docs/technotes/guides/security/certpath/CertPathProgGuide.html#PKIXRevocationChecker
                PKIXRevocationChecker checker = (PKIXRevocationChecker) certPathBuilder.getRevocationChecker();
                // soft fail option mean that OCSP or CRL must pass validation, in case when OCSP cannot be
                // validated it will validate CRL and then if both cannot be checked it will fail
                if (allowSoftFail) {
                    checker.setOptions(EnumSet.of(PKIXRevocationChecker.Option.SOFT_FAIL));
                }
                parameters.addCertPathChecker(checker);
                // will use PKIXRevocationChecker instead of the default revocation checker
                parameters.setRevocationEnabled(false);
            } else if (!checkOCSP && checkCRL) {
                // check only CRL, need custom implementation
                // add CRL store and download CRL list to this store.
                try {
                    loadCRL(chain);
                    parameters.addCertStore(crlStore);
                    parameters.setRevocationEnabled(true);

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
            for (X509Certificate cert : chain) {
                for (X509CRL crl : crlCollection) {
                    if (crl.isRevoked(cert)) {
                        try {
                            moveToBlackList(cert);
                        } catch (IOException e1) {
                            Log.error("Couldn't move to the blacklist", e1);
                        }
                        break;
                    }
                }
            }
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
     * @param certificate given by server
     * @throws CertificateException
     */
    private void checkBasicConstraints(X509Certificate cert) throws CertificateException {
        if (cert.getBasicConstraints() != -1) {
            throw new CertificateException("Certificate have no basic constraints");
        }
    }

    /**
     * loads truststore and potentially (depending on settings) blacklist
     */
    private void loadTrustStore() {
        try {
            trustStore = KeyStore.getInstance("JKS");
            // checking if length >0 prevents EOFExceptions
            if (CertificateController.TRUSTED.exists() && !CertificateController.TRUSTED.isDirectory()
                    && CertificateController.TRUSTED.length() > 0) {
                try (InputStream inputStream = new FileInputStream(CertificateController.TRUSTED)) {
                    trustStore.load(inputStream, CertificateController.passwd);
                } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                    Log.error("Error at accesing exceptions KeyStore");
                }
            } else {
                trustStore.load(null, CertificateController.passwd); // if cannot open KeyStore then new empty one will
                                                                     // be created
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            Log.warning("Cannot create exceptions KeyStore", e);
        }

        if (acceptRevoked) {
            try {
                blackStore = KeyStore.getInstance("JKS");
                if (CertificateController.BLACKLIST.exists() && !CertificateController.BLACKLIST.isDirectory()
                        && CertificateController.BLACKLIST.length() > 0) {
                    try (InputStream inputStream = new FileInputStream(CertificateController.BLACKLIST)) {
                        blackStore.load(inputStream, CertificateController.passwd);
                    } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                        Log.error("Error at accesing exceptions KeyStore");
                    }
                } else {
                    blackStore.load(null, CertificateController.passwd);
                }
            } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
                Log.warning("Cannot create exceptions KeyStore", e);
            }
        }

    }

    private void loadCRL(X509Certificate[] chain) throws IOException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, CertStoreException, CRLException, CertificateException {

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
