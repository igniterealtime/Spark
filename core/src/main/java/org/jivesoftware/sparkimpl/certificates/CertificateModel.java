package org.jivesoftware.sparkimpl.certificates;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Model that keep certificate fields as String values.
 */
public class CertificateModel {

	private int version;
	private String serialNumber;
	private String signatureValue;
	private String signatureAlgorithm;
	private String issuer;
	private String subject;
	private String notBefore;
	private String notAfter;
	private String publicKey;
	private String publicKeyAlgorithm;
	private String issuerUniqueID;

	private boolean valid;
	private boolean exempted;
	// List<String> extensionList;

	/**
	 * Creates certificate model.
	 * 
	 * @param version
	 * @param serialNumber
	 * @param signatureValue
	 * @param signatureAlgorithm
	 * @param issuer
	 * @param issuerUniqueID
	 * @param subject
	 * @param notBefore
	 * @param notAfter
	 * @param publickKeyInfo
	 */
	public CertificateModel(int version, String serialNumber, String signatureValue, String signatureAlgorithm,
			String issuer, String subject, String notBefore, String notAfter, String publicKey,
			String publicKeyAlgorithm, String issuerUniqueID, boolean valid, Boolean exempted) {
		this.version = version;
		this.serialNumber = serialNumber;
		this.signatureValue = signatureValue;
		this.signatureAlgorithm = signatureAlgorithm;
		this.issuer = issuer;
		this.subject = subject;
		this.notBefore = notBefore;
		this.notAfter = notAfter;
		this.publicKey = publicKey;
		this.publicKeyAlgorithm = publicKeyAlgorithm;
		this.issuerUniqueID = issuerUniqueID;

		this.valid = valid;
		this.exempted = exempted;
		// this.extensionList = extensionList;
	}

	public CertificateModel(X509Certificate certificate) {
		this.version = certificate.getVersion();
		this.serialNumber = certificate.getSerialNumber().toString();
		this.signatureValue = certificate.getSignature().toString();
		this.signatureAlgorithm = certificate.getSigAlgName();
		this.issuer = certificate.getIssuerX500Principal().toString();
		this.subject = certificate.getSubjectX500Principal().getName().toString();
		this.notBefore = certificate.getNotBefore().toString();
		this.notAfter = certificate.getNotAfter().toString();
		this.publicKey = certificate.getPublicKey().toString();
		this.publicKeyAlgorithm = certificate.getPublicKey().getAlgorithm().toString();
		// this.issuerUniqueID = certificate.getIssuerUniqueID().toString();

		this.valid = valid;
		this.exempted = exempted;
	}

	public int getVersion() {
		return version;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getSignatureValue() {
		return signatureValue;
	}

	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public String getIssuer() {
		return issuer;
	}

	public String getSubject() {
		return subject;
	}

	public String getNotBefore() {
		return notBefore;
	}

	public String getNotAfter() {
		return notAfter;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getPublicKeyAlgorithm() {
		return publicKeyAlgorithm;
	}
	
	public String getIssuerUniqueID() {
		return issuerUniqueID;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean isExempted() {
		return exempted;
	}
	
}
