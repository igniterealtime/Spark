package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.InvalidNameException;
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

/**
 * This trust manager wrap around SparkExceptionsTrustManager. In case when SparkExceptionsTrustManager fail then this
 * TrustManager will provide certificates from TRUSTED KeyStore which are checked against data validity, revocation,
 * acceptance of self-signed certificates and basic constraints.
 * 
 * 
 * @author Pawel Scibiorsk
 *
 */
public class SparkTrustManager extends GeneralTrustManager implements X509TrustManager {

    static X509Certificate[] lastFailedChain;

    private final boolean checkCRL;
    private final boolean checkOCSP;
    private final boolean acceptExpired;
    private final boolean acceptNotValidYet;
    private final boolean acceptRevoked;
    private final boolean acceptSelfSigned;
    private final boolean allowSoftFail;

    private CertStore crlStore;
    private final X509TrustManager exceptionsTrustManager;
    private KeyStore trustStore;
    private KeyStore displayedCaCerts;
    private final Collection<X509CRL> crlCollection = new ArrayList<>();
    
    public SparkTrustManager() {
        exceptionsTrustManager = new SparkExceptionsTrustManager();
        checkCRL = localPref.isCheckCRL();
        checkOCSP = localPref.isCheckOCSP();
        acceptExpired = localPref.isAcceptExpired();
        acceptNotValidYet = localPref.isAcceptNotValidYet();
        acceptRevoked = localPref.isAcceptRevoked();
        acceptSelfSigned = localPref.isAcceptSelfSigned();
        allowSoftFail = localPref.isAllowSoftFail();

        loadKeyStores();
    }

    public static X509TrustManager[] getTrustManagerList() {
        return new X509TrustManager[] { new SparkTrustManager() };
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        throw new UnsupportedOperationException("This implementation cannot be used to validate client-provided certificate chains.");
    }

    public static X509Certificate[] getLastFailedChain()
    {
        return lastFailedChain;
    }

    public void addChain( X509Certificate[] chain ) {
        certControll.addChain(chain);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            doTheChecks(chain, authType);
            lastFailedChain = null;
        } catch (CertPathValidatorException e) {
            lastFailedChain = chain;
            throw new CertificateException(e);
        }
    }

    /**
     * Do chain validity checks
     * @param chain with certificates from server
     * @param authType
     * @throws CertificateException
     * @throws CertPathValidatorException 
     */
    private void doTheChecks(X509Certificate[] chain, String authType) throws CertificateException, CertPathValidatorException {
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
            if (!isSelfSigned(chain)) {
                // validate certificate path
                try {
                    validatePath(chain);

                } catch (NoSuchAlgorithmException | KeyStoreException | InvalidAlgorithmParameterException | CertPathValidatorException e) {
                    Log.error("Validating path failed", e);
                    throw new CertPathValidatorException("Certificate path validation failed", e);

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
                    throw new CertPathValidatorException("Certificate not in the TrustStore");
                }
                try {
                    loadCRL(chain);
                    for (X509CRL crl : crlCollection) {
                        if (crl.isRevoked(chain[0])) {
                            throw new CertificateException("Certificate is revoked");
                        }
                    }
                } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | CRLException | IOException e) {
                    Log.warning("Couldn't load CRL");
                }
            } else {
                throw new CertificateException("Certificate chain cannot be trusted");
            }
        }
    }

    /**
     * Return true if the certificate chain contain only one Self Signed certificate
     */
    public static boolean isSelfSigned(X509Certificate[] chain) {
        return chain[0].getIssuerX500Principal().getName().equals(chain[0].getSubjectX500Principal().getName())
                && chain.length == 1;
    }

    /**
     * Returns true if a certificate is a 'root' certificate, by verifying that its issuer matches its subject, and the
     * subject of the certificate is a CA (by checking the Basic Constraints).
     *
     * @param cert The certificate to check
     * @return 'true' if the certificate is a root certificate, otherwise false.
     */
    public static boolean isRootCACertificate( final X509Certificate cert ) {
        return cert.getIssuerX500Principal().getName().equals(cert.getSubjectX500Principal().getName()) && cert.getBasicConstraints() > -1;
    }

    /**
     * Verifies if Spark's trust stores recognize the issuer of at least one of the certificates in the chain.
     *
     * @param chain The chain to verify
     * @return false if none of the issuers are present in any of the trust stores of Spark.
     */
    public boolean containsTrustAnchorFor(X509Certificate[] chain) {
        final Collection<String> allAcceptedIssuers = Arrays.stream(getAcceptedIssuers())
            .map(x509Certificate -> x509Certificate.getSubjectDN().getName())
            .collect(Collectors.toSet());
        return Arrays.stream(chain).anyMatch( cert -> allAcceptedIssuers.contains( cert.getIssuerDN().getName()) );
    }

    /**
     * Validate certificate path
     *
     * Note that the provided chain cannot be a self-signed certificate. This method assumes a CA-signed chain is
     * provided.
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws InvalidAlgorithmParameterException
     * @throws CertPathValidatorException
     * @throws CertificateException
     */
    private void validatePath(X509Certificate[] chain)
            throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException,
            CertPathValidatorException, CertificateException
    {
        if (isSelfSigned(chain)) {
            throw new IllegalArgumentException("Method cannot be used with self-signed certificate.");
        }

        // The certificate representing the {@link TrustAnchor TrustAnchor} should not be included in the certification
        // path. If it does, certain validation (like OCSP) might give unexpected results/fail. SPARK-2188
        final List<X509Certificate> certificates = Arrays.stream(chain)
            .filter( cert -> !isRootCACertificate(cert))
            .collect(Collectors.toList());

        // Construct a certPath entity that represents the chain that is to be validated. Does not include the trust anchor.
        final CertPath certPath = CertificateFactory.getInstance("X.509").generateCertPath(certificates);

        // SPARK-2185: Ensure that what we validate is not empty.
        if ( certPath.getCertificates().isEmpty() ) {
            throw new CertificateException("Unable to build a certificate path from the provided chain.");
        }

        // PKIX algorithm is defined in rfc3280
        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
        CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");

        // Selects the target to be validated. This is the end-entity/leaf certificate, typically the first in the chain.
        X509CertSelector toBeValidated = new X509CertSelector();
        toBeValidated.setCertificate((X509Certificate) certPath.getCertificates().get(0));

        // create parameters using trustStore as source of Trust Anchors and using X509CertSelector
        PKIXBuilderParameters parameters = new PKIXBuilderParameters(allStore, toBeValidated);

        // will use PKIXRevocationChecker (or nothing if revocation mechanisms are
        // disabled) instead of the default revocation checker
        parameters.setRevocationEnabled(false);   

        // if revoked certificates aren't accepted, but no revocation checks then only
        // certificates from blacklist will be rejected
        if (!acceptRevoked) {
            
            // OCSP checking is done according to Java PKI Programmer's Guide, PKIXRevocationChecker was added in Java 8:
            // https://docs.oracle.com/javase/8/docs/technotes/guides/security/certpath/CertPathProgGuide.html#PKIXRevocationChecker
            PKIXRevocationChecker checker = (PKIXRevocationChecker) certPathBuilder.getRevocationChecker();

            EnumSet<PKIXRevocationChecker.Option> checkerOptions = EnumSet.noneOf(PKIXRevocationChecker.Option.class);
            // if soft fail isn't enabled then OCSP or CRL must pass validation, in case
            // when any of them cannot be validated verification will fail, if soft fail
            // is enabled then in case of network issues revocation checking is omitted
            if (allowSoftFail) {
                checkerOptions.add(PKIXRevocationChecker.Option.SOFT_FAIL);
            }
            // check OCSP, CRL serve as backup
            if (checkOCSP && checkCRL) {
                checker.setOptions(checkerOptions);
                parameters.addCertPathChecker(checker);
            } else if (!checkOCSP && checkCRL) {
                // check only CRL, if CRL fail then there is no fallback to OCSP
                checkerOptions.add(PKIXRevocationChecker.Option.PREFER_CRLS);
                checkerOptions.add(PKIXRevocationChecker.Option.NO_FALLBACK);
                checker.setOptions(checkerOptions);
                parameters.addCertPathChecker(checker);
            }
                        
        }
        
        try {
            PKIXCertPathValidatorResult validationResult = (PKIXCertPathValidatorResult) certPathValidator
                    .validate(certPath, parameters);
            X509Certificate trustAnchor = validationResult.getTrustAnchor().getTrustedCert();

            if (trustAnchor == null) {
                throw new CertificateException("certificate path failed: Trusted CA is NULL");
            }
            checkBasicConstraints(certPath, trustAnchor);
        } catch (CertificateRevokedException e) {
            Log.warning("Certificate was revoked", e);
            for (Certificate c : certPath.getCertificates()) {
                X509Certificate cert = (X509Certificate) c;
                for (X509CRL crl : crlCollection) {
                    if (crl.isRevoked(cert)) {
                        try {
                            addToBlackList(cert);
                        } catch (HeadlessException | InvalidNameException e1) {
                            Log.error("Couldn't move to the blacklist", e1);
                        }
                        break;
                    }
                }
            }
            throw new CertificateException("Certificate was revoked");
        } catch (CertPathValidatorException e) {
            // Spark can be configured to disregard some of the issues that can pop up through validation.
            if ( e.getReason() == CertPathValidatorException.BasicReason.EXPIRED && acceptExpired ) {
                Log.debug("Chain validation detected expiry, but Spark is configured to allow this. Not failing validation.");
            } else if ( e.getReason() == CertPathValidatorException.BasicReason.NOT_YET_VALID && acceptNotValidYet ) {
                Log.debug("Chain validation detected not-yet-valid, but Spark is configured to allow this. Not failing validation.");
            } else {
                // When Spark is not configured to ignore the validation issue, rethrow the original exception.
                throw e;
            }
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
                Log.warning("Certificate is expired " + cert.getSubjectX500Principal().getName(), e);
                if (!acceptExpired) {
                    throw new CertificateException("Certificate is expired");
                }
            } catch (CertificateNotYetValidException e) {
                Log.warning("Certificate is not valid yet " + cert.getSubjectX500Principal().getName(), e);
                if (!acceptNotValidYet) {
                    throw new CertificateException("Certificate is not valid yet");
                }
            }

        }
    }

    /**
     * Checks the validity of the BasicConstraints extension of each certificate in the chain.
     *
     * Each certificate is assumed to have a BasicConstraints extension, with the exception of the leaf (end-entity)
     * certificate, which _can_ have a certificate.
     *
     * All non-leaf certificates must have the cA field set to 'true'.
     *
     * The pathLen is valid: it defines the maximum amount of intermediate certificates between the CA and the leaf
     * certificate. The leaf certificate itself is not included in the count (eg: a value of 'one' would allow for a
     * chain length of three: the leaf, one intermediate, and the root (where the value of 'one' is defined).
     *
     * This method assumes that the provided chain is in order, where the first chain is the end-entity / leaf certificate.
     *
     * The trust anchor / root CA should not be part of the certPath chain.
     *
     * @param chain The certificate chain, possibly incomplete.
     * @param trustAnchor the root CA certificate.
     * @throws CertificateException When the BasicConstraint verification fails.
     */
    private void checkBasicConstraints(CertPath chain, X509Certificate trustAnchor) throws CertificateException {
        // Intentionally skipping over the first certificate, which is the end-entity certificate.
        for (int i = 1; i<chain.getCertificates().size(); i++)
        {
            final X509Certificate cert = (X509Certificate) chain.getCertificates().get(i);
            // The amount of certificates between the current certificate and the end-entity certificate cannot
            // exceed the value of pathLenConstraint (if the CA flag is not set, -1 will be returned)
            final int pathLenConstraint = cert.getBasicConstraints();
            final int certsSeparatingThisCertFromEndEntity = i - 1;
            if (certsSeparatingThisCertFromEndEntity > pathLenConstraint) {
                throw new CertificateException("Certificate number " + i + " in the chain failed the BasicConstraints check: "
                    + (pathLenConstraint == -1 ? "CA flag not set" : "pathLenConstraint to small (was: " +pathLenConstraint+ " needed:" + certsSeparatingThisCertFromEndEntity+")"));
            }
        }

        // Explicitly check the trustAnchor (as it should not be in the chain)
        final int pathLenConstraint = trustAnchor.getBasicConstraints();
        final int certsSeparatingThisCertFromEndEntity = chain.getCertificates().size() - 1;
        if (certsSeparatingThisCertFromEndEntity > pathLenConstraint) {
            throw new CertificateException("Trust anchor of the chain failed the BasicConstraints check: "
                + (pathLenConstraint == -1 ? "CA flag not set" : "pathLenConstraint to small (was: " + pathLenConstraint + " needed:" + certsSeparatingThisCertFromEndEntity + ")"));
        }
    }

    /**
     * loads truststores and potentially (depending on settings) blacklist
     */
    @Override
    protected void loadKeyStores() {
        certControll.loadKeyStores();
        trustStore = certControll.openKeyStore(CertificateController.TRUSTED);
        displayedCaCerts = certControll.openCacertsKeyStore();

        try {
            loadAllStore();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | HeadlessException e) {
            Log.error("Cannot create allStore KeyStore");
        }

        try {
            addKeyStoreContentToAllStore(trustStore);
        } catch (HeadlessException | KeyStoreException | InvalidNameException e) {
            Log.error("Cannot add trustStore content to allStore", e);
        }
        try {
            addKeyStoreContentToAllStore(displayedCaCerts);
        } catch (HeadlessException | KeyStoreException | InvalidNameException e) {
            Log.error("Cannot add displayedCaCerts to the allStore", e);
        }

    }

    
    public Collection<X509CRL> loadCRL(X509Certificate[] chain) throws IOException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, CRLException {

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
                Log.warning("Certificate " + cert.getSubjectX500Principal().getName() + " have no CRLs");
            }
            // parameters for cert store is collection type, using collection with crl create parameters
            CollectionCertStoreParameters params = new CollectionCertStoreParameters(crlCollection);
            // this parameters are next used for creation of certificate store with crls
            crlStore = CertStore.getInstance("Collection", params);
        }
        return crlCollection;
    }

    /**
     * Move certificate to the blacklist of the revoked certificates.
     * 
     * @param cert certificate which is meant to move into blacklist
     * @throws KeyStoreException
     * @throws InvalidNameException
     * @throws HeadlessException
     */
    private void addToBlackList(X509Certificate cert) throws KeyStoreException,
        HeadlessException, InvalidNameException {
        certControll.addCertificateToBlackList(cert);
    }

    /**
     * Downloads a CRL from given URL
     * 
     * @param url the web address with given CRL
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
