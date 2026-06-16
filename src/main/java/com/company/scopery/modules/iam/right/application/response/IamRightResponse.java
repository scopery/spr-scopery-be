package com.company.scopery.modules.iam.right.application.response;

import com.company.scopery.modules.iam.right.domain.IamRight;

import java.time.Instant;
import java.util.UUID;

public record IamRightResponse(
        UUID id,
        String code,
        String name,
        String description,
        String module,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamRightResponse from(IamRight right) {
        return new IamRightResponse(
                right.id(), right.code().value(), right.name(),
                right.description(), right.module(), right.status().name(),
                right.createdAt(), right.updatedAt());
    }
}
