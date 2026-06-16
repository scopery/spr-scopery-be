package com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto;

public interface SecretEncryptor {

    EncryptedSecret encrypt(String plaintext);

    String decrypt(EncryptedSecret encryptedSecret);
}
