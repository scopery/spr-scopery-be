package com.company.scopery.platform.web.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "app_idempotency_key")
public class IdempotencyKeyJpaEntity {
    @Id @Column(name = "key_hash", nullable = false, length = 64) private String keyHash;
    @Column(name = "request_method", nullable = false) private String requestMethod;
    @Column(name = "request_path", nullable = false) private String requestPath;
    @Column(name = "response_status", nullable = false) private int responseStatus;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_body", nullable = false, columnDefinition = "jsonb") private String responseBody;
    @Column(name = "content_type") private String contentType;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
}
