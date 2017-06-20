package org.jivesoftware.sparkimpl.certificates;

import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import org.jivesoftware.spark.util.log.Log;

/**
 * Model that keep certificate fields as String values.Together with CertificateController and
 * CertificateManagerSettingsPanel classes this apply MVC pattern.
 */
public class CertificateModel {

	private String alias;
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
	private String subjectUniqueID;

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
	public CertificateModel(String alias, int version, String serialNumber, String signatureValue,
			String signatureAlgorithm, String issuer, String subject, String notBefore, String notAfter,
			String publicKey, String publicKeyAlgorithm, String issuerUniqueID, String subjectUniqueID, boolean valid,
			Boolean exempted) {

		if (version != 3 || version != 2 || version != 1) {
			throw new IllegalArgumentException("Version have to be 1, 2 or 3");
		}
		if (serialNumber == null || signatureValue == null || signatureAlgorithm == null || issuer == null
				|| subject == null || notBefore == null || notAfter == null || publicKey == null
				|| publicKeyAlgorithm == null || exempted == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (version == 1 && issuerUniqueID != null) {
			throw new IllegalArgumentException(
					"Unique Identifiers are present then certificate version must be 2 or 3");
		}
		this.alias = alias;
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
		this.subjectUniqueID = subjectUniqueID;
		this.valid = valid;
		this.exempted = exempted;
		// this.extensionList = extensionList;
	}

	public CertificateModel(X509Certificate certificate, String alias) {
		this(certificate);
		this.alias = alias;
	}

	public CertificateModel(X509Certificate certificate) {
		this.version = certificate.getVersion();
		this.serialNumber = certificate.getSerialNumber().toString();
		this.signatureValue = Base64.getEncoder().encodeToString(certificate.getSignature());
		this.signatureAlgorithm = certificate.getSigAlgName();
		this.issuer = certificate.getIssuerX500Principal().toString();
		this.subject = certificate.getSubjectX500Principal().getName().toString();
		this.notBefore = certificate.getNotBefore().toString();
		this.notAfter = certificate.getNotAfter().toString();
		this.publicKey = certificate.getPublicKey().toString();
		this.publicKeyAlgorithm = certificate.getPublicKey().getAlgorithm().toString();
		try {
			this.issuerUniqueID = certificate.getIssuerUniqueID().toString();
		} catch (NullPointerException e) {
			Log.warning("Certificate doesn't have issuerUniqueID ", e);
		}
		try {
			this.subjectUniqueID = certificate.getIssuerUniqueID().toString();
		} catch (NullPointerException e) {
			Log.warning("Certificate doesn't have subjectUniqueID ", e);
		}
		this.valid = valid;
		this.exempted = exempted;
	}

	public String getAlias() {
		return alias;
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

	public String getSubjectUniqueID() {
		return subjectUniqueID;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean isExempted() {
		return exempted;
	}

}
