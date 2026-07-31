package com.company.scopery.modules.traceability.requirement.application.response;

import java.util.UUID;

public record LinkableUseCaseResponse(UUID id, String key, String name, String status, String completenessStatus) {}
