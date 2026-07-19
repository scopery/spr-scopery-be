package com.company.scopery.modules.clientportal.portalform.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record SubmitPortalFormRequest(@NotNull UUID formVersionId, String objectTypeCode, UUID targetId, @NotBlank String payloadJson) {}
