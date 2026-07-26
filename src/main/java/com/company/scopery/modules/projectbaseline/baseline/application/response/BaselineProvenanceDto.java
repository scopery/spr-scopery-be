package com.company.scopery.modules.projectbaseline.baseline.application.response;

import java.util.List;
import java.util.UUID;

public record BaselineProvenanceDto(List<EntryDto> sources) {
    public record EntryDto(String source, UUID id, String label, String status, String capturedAt) {}
}
