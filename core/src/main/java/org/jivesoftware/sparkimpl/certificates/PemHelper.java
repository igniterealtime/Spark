package org.jivesoftware.sparkimpl.certificates;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

public class PemHelper {

    public enum typeOfDelimeter {
        KEY_BEGIN, KEY_END, CERT_BEGIN, CERT_END
    }


    //sometimes key some delimeters might be a bit customized like "RSA PRIVATE KEY" vs "PRIVATE KEY"
    private static final String[] keyBeginDelimeters = {  "-----BEGIN RSA PRIVATE KEY-----",
                                                    "-----BEGIN PRIVATE KEY-----", 
                                    };
    private static final String[] keyEndDelimeters = {    "-----END RSA PRIVATE KEY-----",
                                                    "-----END PRIVATE KEY-----"
                                    };

    private static final String[] certBeginDelimeters = { "-----BEGIN CERTIFICATE-----",
                                                    "-----BEGIN X509 CERTIFICATE-----", 
                                                    "-----BEGIN TRUSTED CERTIFICATE-----"
                                    };
    
    private static final String[] certEndDelimeters = {   "-----END CERTIFICATE-----",
                                                    "-----END X509 CERTIFICATE-----", 
                                                    "-----END TRUSTED CERTIFICATE-----"
                                    };


    protected static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter)
            throws PEMException {
        String data = new String(pem);
        if(!data.contains(beginDelimiter) || !data.contains(endDelimiter)){
            throw new PEMException("File doesn't contains begin delimeter: " +beginDelimiter + "or end delimeter: " +endDelimiter );
        }
        String[] tokens = data.split(beginDelimiter);
        tokens = tokens[1].split(endDelimiter);
        return DatatypeConverter.parseBase64Binary(tokens[0]);        
    }

    protected static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // here Bouncy Castle provider is important as KeyFactory might not work
        Security.addProvider(new BouncyCastleProvider());
        
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey)factory.generatePrivate(spec);        
    }
    
    /**
     * 
     * @param pem it's byte array representation of pem file
     * @param type
     * @return
     * @throws PEMException 
     * @throws Exception 
     */
    protected static String knowDelimeter(byte[] pem, typeOfDelimeter type) throws PEMException {
        if(!(type instanceof typeOfDelimeter)){
            throw new IllegalArgumentException();
        }
        String header = new String(pem);
        String knownDelimeter = null;
        String[] deliArray = null;
        if(type.equals(typeOfDelimeter.KEY_BEGIN)){
            deliArray = keyBeginDelimeters;
        }
        if(type.equals(typeOfDelimeter.KEY_END)){
            deliArray = keyEndDelimeters;
        }
        if(type.equals(typeOfDelimeter.CERT_BEGIN)){
            deliArray = certBeginDelimeters;
        }
        if(type.equals(typeOfDelimeter.CERT_END)){
            deliArray = certEndDelimeters;
        }
        for (String delimeter : deliArray) {
            if (header.contains(delimeter)) {
                knownDelimeter = delimeter;
            }
        }
        if(knownDelimeter == null){
            throw new PEMException("Pem file doesn't include: "+ type.toString() + " kind of delimeter");
        }
        return knownDelimeter;
        
    }
    
    protected static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(certBytes));      
    }
    
    /**
     * Saves object to PEM file
     * 
     * @param object meant to be saved
     * @param file to which object should be saved
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveToPemFile(Object object, File file) throws FileNotFoundException, IOException{
        try (JcaPEMWriter pem = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            pem.writeObject(object);
        }
    }
    
    /**
     * This class can be used to write few object into one PEM file.
     * @author Paweł Ścibiorski
     *
     */
    public static class PemBuilder {
        private final List<Object> buildList = new ArrayList<>();

        public void add(Object object) {
            buildList.add(object);
        }

        public void saveToPemFile(File file) throws IOException {
            try (JcaPEMWriter pem = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
                for (Object object : buildList) {
                    pem.writeObject(object);
                }
            }
        }
    }
    
}
