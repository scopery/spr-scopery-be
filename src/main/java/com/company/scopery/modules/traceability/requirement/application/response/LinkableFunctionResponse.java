package com.company.scopery.modules.traceability.requirement.application.response;

import java.util.UUID;

public record LinkableFunctionResponse(UUID id, String code, String title, String type, String status) {}
