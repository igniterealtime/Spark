package org.jivesoftware.sparkimpl.certificates;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyConstraints;
import org.bouncycastle.asn1.x509.PolicyQualifierInfo;
import org.bouncycastle.asn1.x509.SubjectDirectoryAttributes;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.util.encoders.Hex;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.log.Log;

/**
 * Model that keep certificate fields as String values.Together with CertificateController and
 * CertificateManagerSettingsPanel classes this apply MVC pattern.
 */
public class CertificateModel {

	private X509Certificate certificate;
	private String alias;
	private String subjectCommonName;
	private String issuerCommonName;
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
	private boolean expired;
	private boolean revoked;
	private boolean notValidYet;
	private boolean exempted;
	private Set<String> criticalExtensionSet;
	private Set<String> nonCriticalExtensionSet;
	private HashMap<String, String> extensions = new HashMap<String,String>();
	private ArrayList<String> unsupportedExtensions = new ArrayList<String>();
	
	private String subjectDirectoryAttributesExtension; // OID 2.5.29.9
	private String subjectKeyIdentifierExtension; // OID 2.5.29.14
	private String keyUsageExtension; // OID 2.5.29.15
	private String subjectAlternativeNameExtension; // OID 2.5.29.17
	private String issuerAlternativeNameExtension; // OID 2.5.29.18
	private String basicConstraintsExtension; // OID 2.5.29.19
	private String nameConstraintsExtension; // OID 2.5.29.30
	private String CRLDistributionPointsExtension; // OID 2.5.29.31
	private String policyMappingsExtension; // OID 2.5.29.33
	private String authorityKeyIdentifierExtension; // OID 2.5.29.35
	private String policyConstraintsExtension; // OID 2.5.29.36
	private String extendedKeyUsageExtension; // OID 2.5.29.37

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
		this.certificate = certificate;
		this.version = certificate.getVersion();
		this.serialNumber = certificate.getSerialNumber().toString();
		this.signatureValue = Base64.getEncoder().encodeToString(certificate.getSignature());
		this.signatureAlgorithm = certificate.getSigAlgName();
		this.issuer = certificate.getIssuerX500Principal().getName().toString();
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
		try {
			this.subjectCommonName = extractCommonName(subject);
		} catch (InvalidNameException e) {
			Log.warning("Couldn't extract subject Common Name (CN)", e);
		}
		try {
			this.issuerCommonName = extractCommonName(issuer);
		} catch (InvalidNameException e) {
			Log.warning("Couldn't extract issuer Common Name (CN)", e);
		}
		this.valid = checkValidity();
		this.exempted = exempted;

		setupExtensions(certificate);

	}

	private void setupExtensions(X509Certificate cert) {

		criticalExtensionSet = cert.getCriticalExtensionOIDs();
		nonCriticalExtensionSet = cert.getNonCriticalExtensionOIDs();
		if (criticalExtensionSet != null) {
			for (String oid : criticalExtensionSet) {
				
					extensionExtractHandler(cert, oid);
			
			}
		}
		if (nonCriticalExtensionSet != null) {
			for (String oid : nonCriticalExtensionSet) {
					extensionExtractHandler(cert, oid);
				
			}
		}
	}

	private void extensionExtractHandler(X509Certificate cert, String oid) {

		ASN1Primitive primitive;
		if (oid.equals("2.5.29.9")) {
			try {
				primitive = JcaX509ExtensionUtils.parseExtensionValue(cert.getExtensionValue(oid));
				SubjectDirectoryAttributes sub = SubjectDirectoryAttributes.getInstance(primitive);
				subjectDirectoryAttributesExtension = sub.toString();
				extensions.put(oid, subjectDirectoryAttributesExtension);
			} catch (IOException e) {
				Log.warning("Couldn't extract subject directory attributes extension", e);
			}
		} else if (oid.equals("2.5.29.14")) {
			try {
				SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier
						.fromExtensions(new JcaX509CertificateHolder(cert).getExtensions());
				subjectKeyIdentifierExtension = Hex.toHexString(subjectKeyIdentifier.getKeyIdentifier());
				extensions.put(oid, subjectKeyIdentifierExtension);
			} catch (CertificateEncodingException e) {
				Log.warning("Couldn't extract subject key identifier from certificate", e);
			}

		} else if (oid.equals("2.5.29.15")) {
			keyUsageExtension = Res.getString("cert.extension.extended.usage.digital.signature") + ": "
					+ cert.getKeyUsage()[0] + "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.non.repudiation") + ": "
					+ cert.getKeyUsage()[1] + "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.key.encipherment") + ": "
					+ cert.getKeyUsage()[2] + "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.data.encipherment") + ": "
					+ cert.getKeyUsage()[3] + "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.key.agreement") + ": "
					+ cert.getKeyUsage()[4] + "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.key.cert.sign") + ": "
					+ cert.getKeyUsage()[5] + "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.crl.sign") + ": " 
					+ cert.getKeyUsage()[6]	+ "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.encipher.only") + ": "
					+ cert.getKeyUsage()[7] + "\n";
			keyUsageExtension += Res.getString("cert.extension.extended.usage.decipher.only") + ": "
					+ cert.getKeyUsage()[8];
			extensions.put(oid, keyUsageExtension);
		} else if (oid.equals("2.5.29.16")) {
			// irivateKeyUsagePeriodExtension;

		} else if (oid.equals("2.5.29.17")) {
			try {
				subjectAlternativeNameExtension ="";
				Collection<List<?>> rootNames = cert.getIssuerAlternativeNames();
				for(List names:rootNames){
					for(Object name:names){
						issuerAlternativeNameExtension =name.toString() + "\n";
					}
				}
				extensions.put(oid, issuerAlternativeNameExtension);
			} catch (CertificateParsingException | NullPointerException e) {
				Log.warning("Couldn't extract issuer alternatives name extension", e);
			}

		} else if (oid.equals("2.5.29.18")) {
			try {
				issuerAlternativeNameExtension ="";
				Collection<List<?>> rootNames = cert.getIssuerAlternativeNames();
				for(List names:rootNames){
					for(Object name:names){
						issuerAlternativeNameExtension =name.toString() + "\n";
					}
				}
				extensions.put(oid, issuerAlternativeNameExtension);
			} catch (CertificateParsingException e) {
				Log.warning("Couldn't extract issuer alternatives name extension", e);
			}

		} else if (oid.equals("2.5.29.19")) {
			try {
				primitive = JcaX509ExtensionUtils.parseExtensionValue(cert.getExtensionValue(oid));
				BasicConstraints bc = BasicConstraints.getInstance(primitive);
				basicConstraintsExtension = Res.getString("cert.extension.basic.constraints.is.ca") + ": " + bc.isCA();
				if (bc.getPathLenConstraint() != null) {
					basicConstraintsExtension += "\n" + Res.getString("cert.extension.basic.constraints.path.length")
							+ ": " + bc.getPathLenConstraint();
				}
				extensions.put(oid, basicConstraintsExtension);
			} catch (IOException e) {
				Log.warning("Couldn't extract basic constraints extension", e);
			}

		} else if (oid.equals("2.5.29.30")) {
			try {
				primitive = JcaX509ExtensionUtils.parseExtensionValue(cert.getExtensionValue(oid));
				NameConstraints nc = NameConstraints.getInstance(primitive);
				nameConstraintsExtension = Res.getString("cert.extension.name.constraints.permitted.subtrees") + ": \n";
				for (GeneralSubtree subtree : nc.getPermittedSubtrees()) {
					nameConstraintsExtension += subtree.toString() + "\n";
				}
				nameConstraintsExtension = Res.getString("cert.extension.name.constraints.excluded.subtrees") + ": \n";
				for (GeneralSubtree subtree : nc.getExcludedSubtrees()) {
					nameConstraintsExtension += subtree.toString() + "\n";
				}
				extensions.put(oid, nameConstraintsExtension);
			} catch (IOException e) {
				Log.warning("Couldn't extract name constraints extension", e);
			}

		} else if (oid.equals("2.5.29.31")) {
			// CRLDistributionPointsExtension;
			try {
				primitive = JcaX509ExtensionUtils.parseExtensionValue(cert.getExtensionValue(oid));
				CRLDistPoint point = CRLDistPoint.getInstance(primitive);
				CRLDistributionPointsExtension = point.toString();
				extensions.put(oid, CRLDistributionPointsExtension);
			} catch (IOException e) {
				Log.warning("Couldn't extract CRL Distribution Points extension from certificate", e);
			}

		} else if (oid.equals("2.5.29.33")) {
				ASN1OctetString oct = ASN1OctetString.getInstance(cert.getExtensionValue(oid));
				policyMappingsExtension = oct.toString();
				extensions.put(oid, policyMappingsExtension);
		} else if (oid.equals("2.5.29.35")) {
			try {
				AuthorityKeyIdentifier authorityKeyIdentifier = AuthorityKeyIdentifier
						.fromExtensions(new JcaX509CertificateHolder(cert).getExtensions());
				authorityKeyIdentifierExtension = Hex.toHexString(authorityKeyIdentifier.getKeyIdentifier());
			} catch (CertificateEncodingException e) {
				Log.warning("Couldn't extract authority key identifier extension", e);
			}
			extensions.put(oid, authorityKeyIdentifierExtension);
		} else if (oid.equals("2.5.29.36")) {
			try {
				primitive = JcaX509ExtensionUtils.parseExtensionValue(cert.getExtensionValue(oid));
				PolicyConstraints pc = PolicyConstraints.getInstance(primitive);
				policyConstraintsExtension = Res.getString("cert.extension.policy.constraints.inhibit.policy.mapping")
						+ ": " + pc.getInhibitPolicyMapping() + "\n"
						+ Res.getString("cert.extension.policy.constraints.require.explicit.policy.mapping") + ": "
						+ pc.getRequireExplicitPolicyMapping();
				extensions.put(oid, policyConstraintsExtension);
			} catch (IOException e) {
				Log.warning("Couldn't extract policy constraints exception", e);
			}

		} else if (oid.equals("2.5.29.37")) {
			try {
				extendedKeyUsageExtension = "";
				List<String> extKeyUsage = cert.getExtendedKeyUsage();
				for (String use : extKeyUsage) {
					extendedKeyUsageExtension += use + ": " + OIDTranslator.getDescription(use) + "\n";
				}
				extensions.put(oid, extendedKeyUsageExtension);
			} catch (CertificateParsingException e) {
				Log.warning("Couldn't parse extended key usage extension", e);
			}
		} else {
			unsupportedExtensions.add(oid);
		}
	}

	private String extractCommonName(String certName) throws InvalidNameException {
		String name = null;
		LdapName ldapDN = new LdapName(certName);
		for (Rdn rdn : ldapDN.getRdns()) {
			if (rdn.getType().equals("CN")) {
				name = rdn.getValue().toString();
			}
		}
		return name;
	}

	public String getValidityStatus() {
		if (checkRevoked()) {
			return Res.getString("cert.revoked");
			
		} else if (isAfterNotAfter() == true) {
			return Res.getString("cert.expired");
			
		} else if (isBeforeNotBefore()) {
			return "cert.not.valid.yet";
			
		} else if (isSelfSigned()) {
			return Res.getString("cert.self.signed");
			
		} else {
			return Res.getString("cert.valid");
		}
	}
	
	private boolean isSelfSigned(){
		if(subject.equals(issuer)){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean checkValidity() {
		if (isAfterNotAfter() && isBeforeNotBefore() && checkRevoked()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkRevoked() {
		// TO-DO
		return false;
	}

	private boolean isBeforeNotBefore() {
		Calendar today = Calendar.getInstance();

		if (today.before(certificate.getNotBefore())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isAfterNotAfter() {
		Calendar today = Calendar.getInstance();

		if (today.after(certificate.getNotAfter())) {
			return true;
		} else {
			return false;
		}
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

	public String getSubjectCommonName() {
		return subjectCommonName;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public boolean isNotValidYet() {
		return notValidYet;
	}

	public boolean isExpired() {
		return expired;
	}

	public String getIssuerCommonName() {
		return issuerCommonName;
	}
	
	public Set<String> getCriticalExtensionSet() {
		return criticalExtensionSet;
	}

	public String getSubjectDirectoryAttributesExtension() {
		return subjectDirectoryAttributesExtension;
	}

	public String getSubjectKeyIdentifierExtension() {
		return subjectKeyIdentifierExtension;
	}

	public String getKeyUsageExtension() {
		return keyUsageExtension;
	}

	public String getIubjectAlternativeNameExtension() {
		return subjectAlternativeNameExtension;
	}

	public String getIssuerAlternativeNameExtension() {
		return issuerAlternativeNameExtension;
	}

	public String getBasicConstraintsExtension() {
		return basicConstraintsExtension;
	}

	public String getNameConstraintsExtension() {
		return nameConstraintsExtension;
	}

	public String getCRLDistributionPointsExtension() {
		return CRLDistributionPointsExtension;
	}

	public String getPolicyMappingsExtension() {
		return policyMappingsExtension;
	}

	public String getAuthorityKeyIdentifier() {
		return authorityKeyIdentifierExtension;
	}

	public String getPolicyConstraintsExtension() {
		return policyConstraintsExtension;
	}

	public String getExtendedKeyUsageExtension() {
		return extendedKeyUsageExtension;
	}
	

	public HashMap<String, String> getExtensions() {
		return extensions;
	}

	public ArrayList<String> getUnsupportedExtensions() {
		return unsupportedExtensions;
	}
}
