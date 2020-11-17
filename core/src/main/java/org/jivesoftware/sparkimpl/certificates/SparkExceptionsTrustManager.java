package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.InvalidNameException;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.spark.util.log.Log;

/**
 * This TrustManager serves for purpose of accepting certificates from exceptions lists. The only check it does is check
 * if certification path can be builded. It doesn't do any checks against time of expiration or revocation.
 * 
 * @author Pawel Scibiorski
 *
 */
public class SparkExceptionsTrustManager extends GeneralTrustManager implements X509TrustManager {

    KeyStore exceptionsStore, cacertsExceptionsStore;
    public SparkExceptionsTrustManager() {
        loadKeyStores();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        throw new UnsupportedOperationException("This implementation cannot be used to validate client-provided certificate chains.");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // if end entity certificate is on the exception list then pass checks
        try {
            if (!isFirstCertExempted(chain) && !SparkTrustManager.isSelfSigned(chain)) {
                // else exempted certificate can be higher in chain, in this case certificate list have to be build
                validatePath(chain);
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | InvalidAlgorithmParameterException
                | CertPathValidatorException e) {
            Log.warning("Cannot build certificate chain", e);
            throw new CertificateException("Cannot build certificate chain");
        }
    }

    /**
     * Validate certificate path. As it is exception, no checks against revocation or time validity are done but path
     * still have to be validated in order to find connection between certificate presented by server and root CA in
     * KeyStore
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws InvalidAlgorithmParameterException
     * @throws CertPathValidatorException
     * @throws CertificateException
     */
    private void validatePath(X509Certificate[] chain)
            throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException,
            CertPathValidatorException, CertificateException {

        if (SparkTrustManager.isSelfSigned(chain)) {
            throw new IllegalArgumentException("Method cannot be used with self-signed certificate.");
        }

        // The certificate representing the {@link TrustAnchor TrustAnchor} should not be included in the certification
        // path. If it does, certain validation (like OCSP) might give unexpected results/fail. SPARK-2188
        final List<X509Certificate> certificates = Arrays.stream(chain)
            .filter( cert -> !SparkTrustManager.isRootCACertificate(cert))
            .collect(Collectors.toList());

        // Construct a certPath entity that represents the chain that is to be validated. Does not include the trust anchor.
        final CertPath certPath = CertificateFactory.getInstance("X.509").generateCertPath(certificates);

        // SPARK-2185: Ensure that what we validate is not empty.
        if ( certPath.getCertificates().isEmpty() ) {
            throw new CertificateException("Unable to build a certificate path from the provided chain.");
        }

        // PKIX algorithm is defined in rfc3280
        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");

        // Selects the target to be validated. This is the end-entity/leaf certificate, typically the first in the chain.
        X509CertSelector toBeValidated = new X509CertSelector();
        toBeValidated.setCertificate((X509Certificate) certPath.getCertificates().get(0));

        // create parameters using trustStore as source of Trust Anchors and using X509CertSelector
        PKIXBuilderParameters parameters = new PKIXBuilderParameters(allStore, toBeValidated);

        // no checks against revocation as this is the 'exception' trust manager.
        parameters.setRevocationEnabled(false);

        try {
            PKIXCertPathValidatorResult validationResult = (PKIXCertPathValidatorResult) certPathValidator
                .validate(certPath, parameters);
            X509Certificate trustAnchor = validationResult.getTrustAnchor().getTrustedCert();

            if (trustAnchor == null) {
                throw new CertificateException("certificate path failed: Trusted CA is NULL");
            }
        } catch (CertPathValidatorException e) {
            // This exception trust manager ignores the expiration dates
            if ( e.getReason() == CertPathValidatorException.BasicReason.EXPIRED) {
                Log.debug("Chain validation detected expiry, but this 'exception' trust manager allows this Not failing validation.");
            } else if ( e.getReason() == CertPathValidatorException.BasicReason.NOT_YET_VALID) {
                Log.debug("Chain validation detected not-yet-valid, but this 'exception' trust manager allows this Not failing validation.");
            } else {
                throw e;
            }
        }
    }

    /**
     * Check if the first certificate in chain is exempted
     * 
     * @param chain contain certificates from server
     * @return true if the first certificate is in exceptionsStore
     * @throws KeyStoreException
     */
    private boolean isFirstCertExempted(X509Certificate[] chain) throws KeyStoreException {
        if (isOrderFromSubjectToIssuer(chain)) {
            return exceptionsStore.getCertificateAlias(chain[0]) != null;
        } else {
            return exceptionsStore.getCertificateAlias(chain[chain.length - 1]) != null;
        }
        
    }
    
    /**
     * Load KeyStores and add it's content to the allStore
     */
    @Override
    protected void loadKeyStores() {
        exceptionsStore = certControll.openKeyStore(CertificateController.EXCEPTIONS);
        cacertsExceptionsStore = certControll.openKeyStore(CertificateController.CACERTS_EXCEPTIONS);
        try {
            loadAllStore();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | HeadlessException e) {
            Log.error("Cannot create allStore KeyStore");
        }
        
        try {
            addKeyStoreContentToAllStore(exceptionsStore);
        } catch (HeadlessException | KeyStoreException | InvalidNameException e) {
            Log.error("Cannot add exceptionStore content to allStore", e);
        }
        try {
            addKeyStoreContentToAllStore(cacertsExceptionsStore);
        } catch (HeadlessException | KeyStoreException | InvalidNameException e) {
           Log.error("Cannot add cacertsExceptionsStore to allStore", e);
        }

    }

}
