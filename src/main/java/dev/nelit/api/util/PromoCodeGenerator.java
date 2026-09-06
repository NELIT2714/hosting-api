package dev.nelit.api.util;

import java.security.SecureRandom;

public final class PromoCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private PromoCodeGenerator() {
    }

    public static String generate() {
        StringBuilder sb = new StringBuilder(23);

        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                sb.append('-');
            }
            for (int j = 0; j < 5; j++) {
                sb.append(ALPHANUMERIC[RANDOM.nextInt(ALPHANUMERIC.length)]);
            }
        }

        return sb.toString();
    }
}
