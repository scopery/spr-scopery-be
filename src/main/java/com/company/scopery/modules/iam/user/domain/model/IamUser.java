package com.company.scopery.modules.iam.user.domain.model;

import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;

import java.time.Instant;
import java.util.UUID;

public record IamUser(
        UUID id,
        Username username,
        EmailAddress email,
        String fullName,
        String passwordHash,
        IamUserStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamUser create(Username username, EmailAddress email, String fullName) {
        Instant now = Instant.now();
        return new IamUser(UUID.randomUUID(), username, email, fullName, null, IamUserStatus.ACTIVE, now, now);
    }

    public IamUser withPassword(String hashedPassword) {
        return new IamUser(id, username, email, fullName, hashedPassword, status, createdAt, Instant.now());
    }

    public IamUser update(String fullName) {
        return new IamUser(id, username, email, fullName, passwordHash, status, createdAt, Instant.now());
    }

    public IamUser activate() {
        return new IamUser(id, username, email, fullName, passwordHash, IamUserStatus.ACTIVE, createdAt, Instant.now());
    }

    public IamUser deactivate() {
        return new IamUser(id, username, email, fullName, passwordHash, IamUserStatus.INACTIVE, createdAt, Instant.now());
    }

    public IamUser suspend() {
        return new IamUser(id, username, email, fullName, passwordHash, IamUserStatus.SUSPENDED, createdAt, Instant.now());
    }
}
