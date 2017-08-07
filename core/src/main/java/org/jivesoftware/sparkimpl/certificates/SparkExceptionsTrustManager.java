package org.jivesoftware.sparkimpl.certificates;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
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
import java.util.Enumeration;

import javax.net.ssl.X509TrustManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jivesoftware.spark.util.log.Log;

public class SparkExceptionsTrustManager implements X509TrustManager {

    KeyStore exceptionsStore;
    private Provider bcProvider = new BouncyCastleProvider(); // bc provider for path validation

    public SparkExceptionsTrustManager() {
        try (InputStream inputStream = new FileInputStream(CertificateController.EXCEPTIONS)) {
            this.exceptionsStore = KeyStore.getInstance("JKS");
            exceptionsStore.load(inputStream, CertificateController.passwd);
        } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException e) {
            Log.error("Couldn't load keystore for certificate exceptions authentication", e);
            ;
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // doesen't do any checks as there is it use only accepted issuers from exception list
        try {
            validatePath(chain);
        } catch (NoSuchAlgorithmException | KeyStoreException | InvalidAlgorithmParameterException
                | CertPathValidatorException | CertPathBuilderException e) {
            Log.warning("Cannot build certificate chain", e);
            throw new CertificateException("Cannot build certificate chain");
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] X509Certs = null;
        try {
            // See how many certificates are in the keystore.
            int numberOfEntry = exceptionsStore.size();
            // If there are any certificates in the keystore.
            if (numberOfEntry > 0) {
                // Create an array of X509Certificates
                X509Certs = new X509Certificate[numberOfEntry];

                // Get all of the certificate alias out of the keystore.
                Enumeration<String> aliases = exceptionsStore.aliases();

                // Retrieve all of the certificates out of the keystore
                // via the alias name.
                int i = 0;
                while (aliases.hasMoreElements()) {
                    X509Certs[i] = (X509Certificate) exceptionsStore.getCertificate((String) aliases.nextElement());
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

        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX", bcProvider);
        CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
        X509CertSelector certSelector = new X509CertSelector();
        certSelector.setCertificate(chain[chain.length - 1]);
        // checks against time validity aren't done here as it exceptions list
        certSelector.setCertificateValid(null);
        PKIXBuilderParameters parameters = new PKIXBuilderParameters(exceptionsStore, certSelector);
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

}