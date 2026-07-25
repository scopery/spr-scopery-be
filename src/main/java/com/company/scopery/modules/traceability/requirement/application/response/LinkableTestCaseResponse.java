package com.company.scopery.modules.traceability.requirement.application.response;

import java.util.UUID;

public record LinkableTestCaseResponse(UUID id, String code, String title, String status) {}
