package com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scopery.security.secret")
public class SecretEncryptionProperties {

    private String masterKey;
    private String keyVersion = "v1";

    public String getMasterKey() { return masterKey; }
    public void setMasterKey(String masterKey) { this.masterKey = masterKey; }
    public String getKeyVersion() { return keyVersion; }
    public void setKeyVersion(String keyVersion) { this.keyVersion = keyVersion; }
}
