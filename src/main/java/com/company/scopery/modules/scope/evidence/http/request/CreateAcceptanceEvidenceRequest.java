package com.company.scopery.modules.scope.evidence.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateAcceptanceEvidenceRequest(String evidenceType, UUID acceptanceCriteriaId,
                                              @NotBlank String title, String contentText,
                                              String linkUrl, String referenceId) {}
