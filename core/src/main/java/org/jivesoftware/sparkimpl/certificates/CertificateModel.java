package org.jivesoftware.sparkimpl.certificates;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

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
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyConstraints;
import org.bouncycastle.asn1.x509.SubjectDirectoryAttributes;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
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
	private Set<String> criticalExtensionSet;
	private Set<String> nonCriticalExtensionSet;
	private HashMap<String, String> extensions = new HashMap<String,String>();
	private ArrayList<String> unsupportedCriticalExtensions = new ArrayList<String>();
	private ArrayList<String> unsupportedNonCriticalExtensions = new ArrayList<String>();

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

		setupExtensions(certificate);

	}

	private void setupExtensions(X509Certificate cert) {

		criticalExtensionSet = cert.getCriticalExtensionOIDs();
		nonCriticalExtensionSet = cert.getNonCriticalExtensionOIDs();
		if (criticalExtensionSet != null) {
			for (String oid : criticalExtensionSet) {
				extensionExtractHandler(cert, oid, true);
			}
		}
		if (nonCriticalExtensionSet != null) {
			for (String oid : nonCriticalExtensionSet) {
				extensionExtractHandler(cert, oid, false);
			}
		}
	}

	/**
	 * Get values of the extension and format them into readable Strings.
	 * 
	 * @param cert
	 * @param oid
	 */
	private void extensionExtractHandler(X509Certificate cert, String oid, boolean critical) {
		try {
			ASN1Primitive primitive = JcaX509ExtensionUtils.parseExtensionValue(cert.getExtensionValue(oid));
			String value = Res.getString("cert.is.critical") + critical + "\n";
			boolean isSupported = true;

			if (oid.equals(Extension.subjectDirectoryAttributes.toString())) {
				value += subjectDirectoryAttributesExtractor(primitive);

			} else if (oid.equals(Extension.subjectKeyIdentifier.toString())) {
				value += subjectKeyIdentifierExtractor(primitive);

			} else if (oid.equals(Extension.keyUsage.toString())) {
				value += keyUsageExtractor(cert);

			} else if (oid.equals(Extension.subjectAlternativeName.toString())) {
				value += alternativeNameExtractor(cert.getSubjectAlternativeNames());

			} else if (oid.equals(Extension.issuerAlternativeName.toString())) {
				value += alternativeNameExtractor(cert.getIssuerAlternativeNames());

			} else if (oid.equals(Extension.basicConstraints.toString())) {
				value += basicConstraintsExtractor(primitive);

			} else if (oid.equals(Extension.nameConstraints.toString())) {
				value += NameConstraintsExtractor(primitive);

			} else if (oid.equals(Extension.cRLDistributionPoints.toString())) {
				value += CRLPointsExtractor(primitive);

			} else if (oid.equals(Extension.policyMappings.toString())) {
				value += policyMappingsExtractor(cert);

			} else if (oid.equals(Extension.authorityKeyIdentifier.toString())) {
				value += authorityKeyIdentifierExtractor(primitive);

			} else if (oid.equals(Extension.policyConstraints.toString())) {
				value += policyConstraintsExtractor(primitive);

			} else if (oid.equals(Extension.extendedKeyUsage.toString())) {
				value += extendedKeyUsageExtractor(cert);

			} else {
				addToUnsupported(critical, oid);
				isSupported = false;
			}
			if (isSupported) {
				extensions.put(oid, value);
			}
		} catch (NullPointerException | IOException | CertificateParsingException e) {
			Log.error("Couldn't extract " + oid + ": " + OIDTranslator.getDescription(oid) + "extension.", e);
			addToUnsupported(critical, oid);
		}
	}

	private String subjectDirectoryAttributesExtractor(ASN1Primitive primitive) {
		SubjectDirectoryAttributes sub = SubjectDirectoryAttributes.getInstance(primitive);
		return sub.toString();
	}

	private String extendedKeyUsageExtractor(X509Certificate cert) throws CertificateParsingException {
		String value = "";
		List<String> extKeyUsage = cert.getExtendedKeyUsage();
		for (String use : extKeyUsage) {
			value += use + ": " + OIDTranslator.getDescription(use) + "\n";
		}
		return value;
	}

	private String policyConstraintsExtractor(ASN1Primitive primitive) {
		PolicyConstraints pc = PolicyConstraints.getInstance(primitive);
		String value = "";
		if (pc.getInhibitPolicyMapping() != null) {
			value += Res.getString("cert.extension.policy.constraints.inhibit.policy.mapping") + ": "
					+ pc.getInhibitPolicyMapping() + "\n";
		}
		if (pc.getRequireExplicitPolicyMapping() != null) {
			value += Res.getString("cert.extension.policy.constraints.require.explicit.policy.mapping") + ": "
					+ pc.getRequireExplicitPolicyMapping();
		}
		return value;
	}

	private String authorityKeyIdentifierExtractor(ASN1Primitive primitive) {
		AuthorityKeyIdentifier authorityKeyIdentifier = AuthorityKeyIdentifier.getInstance(primitive);
		return Hex.toHexString(authorityKeyIdentifier.getKeyIdentifier());
	}
	
	private String subjectKeyIdentifierExtractor(ASN1Primitive primitive) {
		SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier.getInstance(primitive);
		return Hex.toHexString(subjectKeyIdentifier.getKeyIdentifier());
	}

	private String policyMappingsExtractor(X509Certificate cert) {
		ASN1OctetString oct = ASN1OctetString.getInstance(cert.getExtensionValue(Extension.policyMappings.toString()));
		return oct.toString();
	}

	private String CRLPointsExtractor(ASN1Primitive primitive) {
		CRLDistPoint point = CRLDistPoint.getInstance(primitive);
		return point.toString();
	}

	private String NameConstraintsExtractor(ASN1Primitive primitive) {
		NameConstraints nc = NameConstraints.getInstance(primitive);
		String value = "";
		if (nc.getPermittedSubtrees() != null) {
			value += Res.getString("cert.extension.name.constraints.permitted.subtrees") + ": \n";
			for (GeneralSubtree subtree : nc.getPermittedSubtrees()) {
				value += subtree.toString() + "\n";
			}
		}
		if (nc.getExcludedSubtrees() != null) {
			value += Res.getString("cert.extension.name.constraints.excluded.subtrees") + ": \n";
			for (GeneralSubtree subtree : nc.getExcludedSubtrees()) {
				value += subtree.toString() + "\n";
			}
		}
		return value;
	}

	private String basicConstraintsExtractor(ASN1Primitive primitive) {
		BasicConstraints bc = BasicConstraints.getInstance(primitive);
		String value = Res.getString("cert.extension.basic.constraints.is.ca") + ": " + bc.isCA();
		if (bc.getPathLenConstraint() != null) {
			value += "\n" + Res.getString("cert.extension.basic.constraints.path.length") + ": "
					+ bc.getPathLenConstraint();
		}
		return value;
	}

	private String alternativeNameExtractor(Collection<List<?>> rootNames) throws CertificateParsingException {
		String value = "";
		if (rootNames != null) {
			for (List names : rootNames) {
				if (names != null) {
					for (Object name : names) {
						value += name.toString() + "\n";
					}
				}
			}
		}
		return value;
	}

	
	private String keyUsageExtractor(X509Certificate cert){
		String value;
		value = Res.getString("cert.extension.extended.usage.digital.signature") + ": " + cert.getKeyUsage()[0]
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.non.repudiation") + ": " + cert.getKeyUsage()[1]
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.key.encipherment") + ": " + cert.getKeyUsage()[2]
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.data.encipherment") + ": " + cert.getKeyUsage()[3]
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.key.agreement") + ": " + cert.getKeyUsage()[4]
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.key.cert.sign") + ": " + cert.getKeyUsage()[5]
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.crl.sign") + ": " + cert.getKeyUsage()[6] 
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.encipher.only") + ": " + cert.getKeyUsage()[7]
				+ "\n";
		value += Res.getString("cert.extension.extended.usage.decipher.only") + ": " + cert.getKeyUsage()[8];

		return value;
	}
	
	private void addToUnsupported(boolean critical, String oid){
		if (critical) {
			unsupportedCriticalExtensions.add(oid);
		} else {
			unsupportedNonCriticalExtensions.add(oid);
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
			
		} else {
			return Res.getString("cert.valid");
		}
	}
	
	public String getCertStatusAll() {
		String status = "";
		if (checkRevoked()) {
			status += Res.getString("cert.revoked") + "\n";
		}
		if (isAfterNotAfter()) {
			status += Res.getString("cert.expired") + "\n";
		}
		if (isBeforeNotBefore()) {
			status += Res.getString("cert.not.valid.yet") + "\n";
		}
		if (!checkRevoked() && !isAfterNotAfter() && !isBeforeNotBefore()) {
			status += Res.getString("cert.valid") + "\n";
		}
		if (isSelfSigned()) {
			status += Res.getString("cert.self.signed") + "\n";
		}
		return status;
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
		Date today = new Date();
		if (today.before(certificate.getNotBefore())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isAfterNotAfter() {
		Date today = new Date();
		if (today.after(certificate.getNotAfter())) {
			return true;
		} else {
			return false;
		}
	}

	public X509Certificate getCertificate() {
		return certificate;
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

	public HashMap<String, String> getExtensions() {
		return extensions;
	}

	public ArrayList<String> getUnsupportedCriticalExtensions() {
		return unsupportedCriticalExtensions;
	}
	
	public ArrayList<String> getUnsupportedNonCriticalExtensions() {
		return unsupportedNonCriticalExtensions;
	}
}
