package com.company.scopery.modules.collaboration.artifactlink.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateArtifactLinkRequest(UUID agendaItemId, UUID noteId, UUID actionItemId, @NotBlank String targetType, @NotNull UUID targetId, @NotBlank String linkType) {}
