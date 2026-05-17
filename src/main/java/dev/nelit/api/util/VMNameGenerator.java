package dev.nelit.api.util;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

public final class VMNameGenerator {

    private static final RandomGenerator RNG = new SecureRandom();

    private VMNameGenerator() {}

    public static String generate(String countryCode, int nodeId) {
        return countryCode.toLowerCase() + String.format("%02d", nodeId) + numbers();
    }

    private static int numbers() {
        return RNG.nextInt(100, 1_000);
    }
}
