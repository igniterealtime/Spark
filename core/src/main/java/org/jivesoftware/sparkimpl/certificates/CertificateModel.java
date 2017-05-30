package org.jivesoftware.sparkimpl.certificates;

import java.util.List;

/**
 * Model that keep certificate fields as String values.
 */
public class CertificateModel {

	private String version;
	private String serialNumber;
	private String signatureValue;
	private String signatureAlgorithm;
	private String issuer;
	private String subject;
	private String notBefore;
	private String notAfter;
	private String publicKeyInfo;
	private String issuerUniqueID;
	
	private boolean valid;
	private boolean exempted;
	//List<String> extensionList;

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
	public CertificateModel(String version, String serialNumber, String signatureValue, String signatureAlgorithm,
			String issuer, String subject, String notBefore, String notAfter, String publicKeyInfo,
			String issuerUniqueID, boolean valid, Boolean exempted) {
		super();
		this.version = version;
		this.serialNumber = serialNumber;
		this.signatureValue = signatureValue;
		this.signatureAlgorithm = signatureAlgorithm;
		this.issuer = issuer;
		this.subject = subject;
		this.notBefore = notBefore;
		this.notAfter = notAfter;
		this.publicKeyInfo = publicKeyInfo;
		this.issuerUniqueID = issuerUniqueID;
		this.valid = valid;
		this.exempted = exempted;
		//this.extensionList = extensionList;
	}

	public String getVersion() {
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

	public String getPublicKeyInfo() {
		return publicKeyInfo;
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
