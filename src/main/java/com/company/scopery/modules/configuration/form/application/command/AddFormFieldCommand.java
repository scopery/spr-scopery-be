package com.company.scopery.modules.configuration.form.application.command;
import java.util.UUID;
public record AddFormFieldCommand(UUID workspaceId, UUID formId, UUID versionId, String source, UUID sectionId,
                                  UUID customFieldId, String coreKey, Boolean required, Boolean readonly, Integer sortOrder) {}
