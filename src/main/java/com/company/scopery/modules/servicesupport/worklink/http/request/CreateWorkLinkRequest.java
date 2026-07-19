package com.company.scopery.modules.servicesupport.worklink.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateWorkLinkRequest(@NotNull UUID supportCaseId, @NotBlank String targetObjectType,
        @NotNull UUID targetObjectId, @NotBlank String linkType) {}
