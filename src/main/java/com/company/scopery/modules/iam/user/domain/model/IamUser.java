package com.company.scopery.modules.iam.user.domain.model;

import com.company.scopery.modules.iam.user.domain.enums.IamRegistrationSource;
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
        Instant updatedAt,
        IamRegistrationSource registrationSource,
        Instant passwordChangedAt,
        boolean passwordResetRequired,
        int failedLoginCount,
        Instant lastLoginAt,
        Instant lastLoginFailedAt,
        boolean emailVerified) {

    public static IamUser create(Username username, EmailAddress email, String fullName) {
        return create(username, email, fullName, IamRegistrationSource.SELF_SIGNUP);
    }

    public static IamUser create(Username username, EmailAddress email, String fullName,
                                 IamRegistrationSource registrationSource) {
        Instant now = Instant.now();
        return new IamUser(
                UUID.randomUUID(), username, email, fullName, null, IamUserStatus.ACTIVE, now, now,
                registrationSource == null ? IamRegistrationSource.SELF_SIGNUP : registrationSource,
                null, false, 0, null, null, false);
    }

    /** Reconstitute with security-field defaults (tests / legacy call sites). */
    public static IamUser of(UUID id, Username username, EmailAddress email, String fullName,
                             String passwordHash, IamUserStatus status,
                             Instant createdAt, Instant updatedAt) {
        return new IamUser(id, username, email, fullName, passwordHash, status, createdAt, updatedAt,
                IamRegistrationSource.SELF_SIGNUP, null, false, 0, null, null, false);
    }

    public IamUser withPassword(String hashedPassword) {
        return new IamUser(id, username, email, fullName, hashedPassword, status, createdAt, Instant.now(),
                registrationSource, Instant.now(), false, failedLoginCount, lastLoginAt, lastLoginFailedAt,
                emailVerified);
    }

    public IamUser update(String fullName) {
        return new IamUser(id, username, email, fullName, passwordHash, status, createdAt, Instant.now(),
                registrationSource, passwordChangedAt, passwordResetRequired, failedLoginCount,
                lastLoginAt, lastLoginFailedAt, emailVerified);
    }

    public IamUser activate() {
        return new IamUser(id, username, email, fullName, passwordHash, IamUserStatus.ACTIVE, createdAt,
                Instant.now(), registrationSource, passwordChangedAt, passwordResetRequired,
                failedLoginCount, lastLoginAt, lastLoginFailedAt, emailVerified);
    }

    public IamUser deactivate() {
        return new IamUser(id, username, email, fullName, passwordHash, IamUserStatus.INACTIVE, createdAt,
                Instant.now(), registrationSource, passwordChangedAt, passwordResetRequired,
                failedLoginCount, lastLoginAt, lastLoginFailedAt, emailVerified);
    }

    public IamUser suspend() {
        return new IamUser(id, username, email, fullName, passwordHash, IamUserStatus.SUSPENDED, createdAt,
                Instant.now(), registrationSource, passwordChangedAt, passwordResetRequired,
                failedLoginCount, lastLoginAt, lastLoginFailedAt, emailVerified);
    }

    public IamUser recordSuccessfulLogin() {
        return new IamUser(id, username, email, fullName, passwordHash, status, createdAt, Instant.now(),
                registrationSource, passwordChangedAt, passwordResetRequired, 0, Instant.now(),
                lastLoginFailedAt, emailVerified);
    }

    public IamUser recordFailedLogin() {
        return new IamUser(id, username, email, fullName, passwordHash, status, createdAt, Instant.now(),
                registrationSource, passwordChangedAt, passwordResetRequired, failedLoginCount + 1,
                lastLoginAt, Instant.now(), emailVerified);
    }
}
