package com.company.scopery.modules.knowledge.shared.storage;

import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;

@Component
public class S3CompatibleObjectStorageProvider implements ObjectStorageProvider {

    private final ObjectStorageConfig config;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public S3CompatibleObjectStorageProvider(ObjectStorageConfig config) {
        this.config = config;
        var credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey()));
        var region = Region.of(config.getRegion());
        var endpoint = URI.create(config.getEndpoint());

        this.s3Client = S3Client.builder()
                .credentialsProvider(credentials)
                .region(region)
                .endpointOverride(endpoint)
                .forcePathStyle(config.isPathStyleAccess())
                .build();

        this.presigner = S3Presigner.builder()
                .credentialsProvider(credentials)
                .region(region)
                .endpointOverride(endpoint)
                .build();
    }

    @Override
    public PresignedUpload createPresignedUpload(String objectKey, String contentType, long maxSizeBytes) {
        try {
            var duration = Duration.ofMinutes(config.getPresignedUploadExpiryMinutes());
            var putRequest = PutObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(objectKey)
                    .contentType(contentType)
                    .build();
            var presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .putObjectRequest(putRequest)
                    .build();
            var presigned = presigner.presignPutObject(presignRequest);
            return new PresignedUpload(
                    presigned.url().toString(),
                    objectKey,
                    Instant.now().plus(duration));
        } catch (Exception e) {
            throw KnowledgeExceptions.documentStorageProviderUnavailable();
        }
    }

    @Override
    public PresignedDownload createPresignedDownload(String objectKey, String contentDispositionFilename) {
        try {
            var duration = Duration.ofMinutes(config.getPresignedDownloadExpiryMinutes());
            var getRequest = GetObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(objectKey)
                    .responseContentDisposition("attachment; filename=\"" + contentDispositionFilename + "\"")
                    .build();
            var presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .getObjectRequest(getRequest)
                    .build();
            var presigned = presigner.presignGetObject(presignRequest);
            return new PresignedDownload(
                    presigned.url().toString(),
                    objectKey,
                    Instant.now().plus(duration));
        } catch (Exception e) {
            throw KnowledgeExceptions.documentStorageProviderUnavailable();
        }
    }

    @Override
    public StoredObjectMetadata head(String objectKey) {
        try {
            HeadObjectResponse response = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(config.getBucket())
                            .key(objectKey)
                            .build());
            return new StoredObjectMetadata(
                    objectKey,
                    response.contentLength() != null ? response.contentLength() : 0L,
                    response.contentType(),
                    response.eTag());
        } catch (NoSuchKeyException e) {
            return null;
        } catch (Exception e) {
            throw KnowledgeExceptions.documentStorageProviderUnavailable();
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(objectKey)
                    .build());
        } catch (Exception e) {
            throw KnowledgeExceptions.documentStorageProviderUnavailable();
        }
    }
}
