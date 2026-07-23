package com.company.scopery.modules.traceability.overallstructure.application.response;

import java.util.List;
import java.util.UUID;

public record OverallStructureResponse(UUID applicationId, List<ModuleStructure> modules) {
}
