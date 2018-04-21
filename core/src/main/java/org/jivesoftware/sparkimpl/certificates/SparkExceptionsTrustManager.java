package org.jivesoftware.sparkimpl.certificates;

import java.awt.HeadlessException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;

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
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // if end entity certificate is on the exception list then pass checks
        try {
            if (!isFirstCertExempted(chain)) {
                // else exempted certificate can be higher in chain, in this case certificate list have to be build
                validatePath(chain);
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | InvalidAlgorithmParameterException
                | CertPathValidatorException | CertPathBuilderException e) {
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
     * @throws CertPathBuilderException
     * @throws CertificateException
     */
    private void validatePath(X509Certificate[] chain)
            throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException,
            CertPathValidatorException, CertPathBuilderException, CertificateException {

        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
        CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
        X509CertSelector certSelector = new X509CertSelector();
        certSelector.setCertificate(chain[chain.length - 1]);
        // checks against time validity aren't done here as it exceptions list
        certSelector.setCertificateValid(null);
        PKIXBuilderParameters parameters = new PKIXBuilderParameters(allStore, certSelector);
        // no checks against revocation as it is exception
        parameters.setRevocationEnabled(false);

        CertPathBuilderResult pathResult = certPathBuilder.build(parameters);
        CertPath certPath = pathResult.getCertPath();
        PKIXCertPathValidatorResult validationResult = (PKIXCertPathValidatorResult) certPathValidator
                .validate(certPath, parameters);
        X509Certificate trustedCert = validationResult.getTrustAnchor().getTrustedCert();

        if (trustedCert == null) {
            throw new CertificateException("Certificate path failed");
        } else {
            Log.debug("ClientTrustManager: Trusted CA: " + trustedCert.getSubjectDN());
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