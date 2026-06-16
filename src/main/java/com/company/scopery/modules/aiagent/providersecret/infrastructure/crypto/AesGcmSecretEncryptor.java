package com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto;

import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AesGcmSecretEncryptor implements SecretEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;
    private static final int AES_KEY_LENGTH_BYTES = 32;

    private final SecretEncryptionProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmSecretEncryptor(SecretEncryptionProperties properties) {
        this.properties = properties;
    }

    @Override
    public EncryptedSecret encrypt(String plaintext) {
        SecretKey key = loadKey();
        byte[] iv = new byte[IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes());
            return new EncryptedSecret(
                    Base64.getEncoder().encodeToString(cipherText),
                    Base64.getEncoder().encodeToString(iv),
                    properties.getKeyVersion());
        } catch (Exception e) {
            throw AiAgentExceptions.providerSecretEncryptionFailed(e.getMessage());
        }
    }

    @Override
    public String decrypt(EncryptedSecret encryptedSecret) {
        SecretKey key = loadKey();
        try {
            byte[] iv = Base64.getDecoder().decode(encryptedSecret.iv());
            byte[] cipherText = Base64.getDecoder().decode(encryptedSecret.encryptedValue());
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw AiAgentExceptions.providerSecretDecryptionFailed(e.getMessage());
        }
    }

    private SecretKey loadKey() {
        String rawKey = properties.getMasterKey();
        if (rawKey == null || rawKey.isBlank()) {
            throw AiAgentExceptions.providerSecretMasterKeyMissing();
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(rawKey);
        } catch (IllegalArgumentException e) {
            throw AiAgentExceptions.providerSecretMasterKeyInvalid("not valid Base64");
        }
        if (keyBytes.length != AES_KEY_LENGTH_BYTES) {
            throw AiAgentExceptions.providerSecretMasterKeyInvalid(
                    "key must be 32 bytes (256 bits), got " + keyBytes.length);
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
}
