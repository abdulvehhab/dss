package eu.europa.ec.markt.dss.service;

import static org.junit.Assert.assertNotNull;

import java.security.SignatureException;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.tsp.TSPUtil;
import org.junit.Test;

import eu.europa.ec.markt.dss.SignatureAlgorithm;
import eu.europa.ec.markt.dss.signature.token.DSSPrivateKeyEntry;

public class CertificateServiceTest {

	private CertificateService service = new CertificateService();

	@Test
	public void isSelfSigned() throws Exception {
		DSSPrivateKeyEntry entry = service.generateSelfSignedCertificate(SignatureAlgorithm.RSA_SHA256);

		X509Certificate certificate = entry.getCertificate();
		certificate.verify(certificate.getPublicKey());
	}

	@Test(expected = SignatureException.class)
	public void isChildCertificateNotSelfSigned() throws Exception {
		DSSPrivateKeyEntry entryChain = service.generateCertificateChain(SignatureAlgorithm.RSA_SHA256);

		// Child certificate is signed with the issuer's private key
		X509Certificate childCertificate = entryChain.getCertificate();
		childCertificate.verify(childCertificate.getPublicKey());
	}

	@Test
	public void generateTspCertificate() throws Exception {
		DSSPrivateKeyEntry keyEntry = service.generateTspCertificate(SignatureAlgorithm.RSA_SHA256);
		assertNotNull(keyEntry);
		X509Certificate certificate = keyEntry.getCertificate();
		TSPUtil.validateCertificate(new X509CertificateHolder(certificate.getEncoded()));
	}
}