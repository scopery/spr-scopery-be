package com.company.scopery.modules.scope.evidence.application.command;
import java.util.UUID;
public record CreateAcceptanceEvidenceCommand(UUID projectId, UUID deliverableId, UUID acceptanceCriteriaId,
                                              String evidenceType, String title, String contentText,
                                              String linkUrl, String referenceId) {}
