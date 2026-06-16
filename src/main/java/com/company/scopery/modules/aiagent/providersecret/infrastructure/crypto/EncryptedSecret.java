package com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto;

public record EncryptedSecret(
        String encryptedValue,
        String iv,
        String keyVersion
) {}
