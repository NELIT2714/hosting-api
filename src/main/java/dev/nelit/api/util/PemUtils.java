package dev.nelit.api.util;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.StringWriter;
import java.security.cert.X509Certificate;

public final class PemUtils {

    public static String toPem(X509Certificate cert) throws Exception {
        StringWriter sw = new StringWriter();
        try (PemWriter writer = new PemWriter(sw)) {
            writer.writeObject(new PemObject("CERTIFICATE", cert.getEncoded()));
        }
        return sw.toString();
    }
}
