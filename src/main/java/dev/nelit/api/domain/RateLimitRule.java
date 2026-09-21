package dev.nelit.api.domain;

public record RateLimitRule(int capacity, double refillPerSecond) {
    public static final RateLimitRule USER = new RateLimitRule(60, 1.0);
    public static final RateLimitRule ANONYMOUS = new RateLimitRule(20, 0.33);
}
