package dev.nelit.api.pki;

import dev.nelit.api.config.PkiProperties;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class CertificateAuthority {

    static { Security.addProvider(new BouncyCastleProvider()); }

    private final PrivateKey caPrivateKey;
    private final X509Certificate caCert;
    private final PkiProperties props;

    public CertificateAuthority(PkiProperties props) throws Exception {
        this.props = props;
        this.caPrivateKey = loadPrivateKey(props.caKey());
        this.caCert = loadCert(props.caCert());
    }

    public X509Certificate signNodeCsr(String csrPem, long nodeId) throws Exception {
        String expectedCn = "node-" + nodeId;

        PKCS10CertificationRequest csr;
        try (PEMParser parser = new PEMParser(new StringReader(csrPem))) {
            csr = (PKCS10CertificationRequest) parser.readObject();
        }

        X500Name issuer = new JcaX509CertificateHolder(caCert).getSubject();
        Instant now = Instant.now();
        Duration validity = Duration.ofDays(props.nodeCertValidityDays());
        BigInteger serial = BigInteger.valueOf(now.toEpochMilli());

        X500Name subject = new X500Name("O=nelit,CN=" + expectedCn);

        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
            issuer, serial, Date.from(now), Date.from(now.plus(validity)),
            subject, new JcaPKCS10CertificationRequest(csr).getPublicKey()
        );

        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
        builder.addExtension(Extension.keyUsage, true,
            new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        builder.addExtension(Extension.extendedKeyUsage, false,
            new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth}));
        builder.addExtension(Extension.subjectAlternativeName, false,
            new GeneralNames(new GeneralName(GeneralName.dNSName, expectedCn)));

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA").setProvider("BC").build(caPrivateKey);
        X509CertificateHolder holder = builder.build(signer);
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
    }

    public X509Certificate caCertificate() {
        return caCert;
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        try (PEMParser parser = new PEMParser(new FileReader(path))) {
            PEMKeyPair kp = (PEMKeyPair) parser.readObject();
            return new JcaPEMKeyConverter().setProvider("BC").getKeyPair(kp).getPrivate();
        }
    }

    private X509Certificate loadCert(String path) throws Exception {
        try (var in = new java.io.FileInputStream(path)) {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
        }
    }

}
