package com.company.scopery.modules.traceability.screencomponent.application.response;

import java.util.List;
import java.util.UUID;

public record BindComponentToSectionResponse(
        UUID screenId, UUID sectionId, UUID componentId,
        int fieldsImported, List<String> importedFieldKeys) {}
