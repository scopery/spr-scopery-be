package com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto;

public final class SecretMasker {

    private static final int MIN_VISIBLE_LENGTH = 8;
    private static final int PREFIX_CHARS = 3;
    private static final int SUFFIX_CHARS = 4;

    private SecretMasker() {}

    public static String mask(String secret) {
        if (secret == null || secret.length() < MIN_VISIBLE_LENGTH) {
            return "****";
        }
        return secret.substring(0, PREFIX_CHARS) + "..." + secret.substring(secret.length() - SUFFIX_CHARS);
    }
}
