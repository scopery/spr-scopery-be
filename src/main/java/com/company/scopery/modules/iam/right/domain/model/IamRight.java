package com.company.scopery.modules.iam.right.domain.model;

import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;

import java.time.Instant;
import java.util.UUID;

public record IamRight(
        UUID id,
        IamRightCode code,
        String name,
        String description,
        String module,
        IamRightStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamRight create(IamRightCode code, String name, String description, String module) {
        Instant now = Instant.now();
        return new IamRight(UUID.randomUUID(), code, name, description, module,
                IamRightStatus.ACTIVE, now, now);
    }

    public IamRight activate() {
        return new IamRight(id, code, name, description, module, IamRightStatus.ACTIVE, createdAt, Instant.now());
    }

    public IamRight deactivate() {
        return new IamRight(id, code, name, description, module, IamRightStatus.INACTIVE, createdAt, Instant.now());
    }
}
