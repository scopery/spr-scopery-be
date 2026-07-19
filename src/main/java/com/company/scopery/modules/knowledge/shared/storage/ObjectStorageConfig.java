package com.company.scopery.modules.knowledge.shared.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scopery.storage")
public class ObjectStorageConfig {

    private String provider;
    private String endpoint;
    private String region;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private boolean pathStyleAccess = true;
    private int presignedUploadExpiryMinutes = 10;
    private int presignedDownloadExpiryMinutes = 5;
    private long maxUploadSizeBytes = 104857600L;

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public boolean isPathStyleAccess() { return pathStyleAccess; }
    public void setPathStyleAccess(boolean pathStyleAccess) { this.pathStyleAccess = pathStyleAccess; }

    public int getPresignedUploadExpiryMinutes() { return presignedUploadExpiryMinutes; }
    public void setPresignedUploadExpiryMinutes(int presignedUploadExpiryMinutes) {
        this.presignedUploadExpiryMinutes = presignedUploadExpiryMinutes;
    }

    public int getPresignedDownloadExpiryMinutes() { return presignedDownloadExpiryMinutes; }
    public void setPresignedDownloadExpiryMinutes(int presignedDownloadExpiryMinutes) {
        this.presignedDownloadExpiryMinutes = presignedDownloadExpiryMinutes;
    }

    public long getMaxUploadSizeBytes() { return maxUploadSizeBytes; }
    public void setMaxUploadSizeBytes(long maxUploadSizeBytes) {
        this.maxUploadSizeBytes = maxUploadSizeBytes;
    }
}
