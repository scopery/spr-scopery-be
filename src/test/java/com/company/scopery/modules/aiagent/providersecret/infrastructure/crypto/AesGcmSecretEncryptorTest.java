package com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto;

import com.company.scopery.common.exception.AppException;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

class AesGcmSecretEncryptorTest {

    private AesGcmSecretEncryptor encryptor(String masterKey) {
        SecretEncryptionProperties props = new SecretEncryptionProperties();
        props.setMasterKey(masterKey);
        props.setKeyVersion("v1");
        return new AesGcmSecretEncryptor(props);
    }

    private String validBase64Key() {
        // 32 random bytes encoded as Base64 — represents a valid AES-256 key
        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    @Test
    void encrypt_andDecrypt_returnsOriginalPlaintext() {
        AesGcmSecretEncryptor enc = encryptor(validBase64Key());
        String original = "sk-proj-abc123xyz789";

        EncryptedSecret encrypted = enc.encrypt(original);
        String decrypted = enc.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void encrypt_repeatedCalls_produceDifferentIv() {
        AesGcmSecretEncryptor enc = encryptor(validBase64Key());
        String plaintext = "same-secret-value";

        EncryptedSecret first  = enc.encrypt(plaintext);
        EncryptedSecret second = enc.encrypt(plaintext);

        assertThat(first.iv()).isNotEqualTo(second.iv());
    }

    @Test
    void encrypt_repeatedCalls_produceDifferentCiphertext() {
        AesGcmSecretEncryptor enc = encryptor(validBase64Key());
        String plaintext = "same-secret-value";

        EncryptedSecret first  = enc.encrypt(plaintext);
        EncryptedSecret second = enc.encrypt(plaintext);

        assertThat(first.encryptedValue()).isNotEqualTo(second.encryptedValue());
    }

    @Test
    void encrypt_missingMasterKey_throwsAppException() {
        AesGcmSecretEncryptor enc = encryptor("");

        assertThatThrownBy(() -> enc.encrypt("sk-test"))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo("PROVIDER_SECRET_MASTER_KEY_MISSING"));
    }

    @Test
    void encrypt_invalidBase64Key_throwsAppException() {
        AesGcmSecretEncryptor enc = encryptor("not-valid-base64!!!");

        assertThatThrownBy(() -> enc.encrypt("sk-test"))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo("PROVIDER_SECRET_MASTER_KEY_INVALID"));
    }

    @Test
    void encrypt_wrongKeySizeKey_throwsAppException() {
        // 16 bytes = AES-128, not AES-256
        byte[] shortKey = new byte[16];
        String base64ShortKey = Base64.getEncoder().encodeToString(shortKey);
        AesGcmSecretEncryptor enc = encryptor(base64ShortKey);

        assertThatThrownBy(() -> enc.encrypt("sk-test"))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo("PROVIDER_SECRET_MASTER_KEY_INVALID"));
    }

    @Test
    void encryptedSecret_doesNotContainPlaintext() {
        AesGcmSecretEncryptor enc = encryptor(validBase64Key());
        String plaintext = "sk-proj-supersecretkey";

        EncryptedSecret encrypted = enc.encrypt(plaintext);

        assertThat(encrypted.encryptedValue()).doesNotContain(plaintext);
        assertThat(encrypted.keyVersion()).isEqualTo("v1");
    }
}
