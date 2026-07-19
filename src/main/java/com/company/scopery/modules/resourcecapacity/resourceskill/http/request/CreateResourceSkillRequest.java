package com.company.scopery.modules.resourcecapacity.resourceskill.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateResourceSkillRequest(@NotBlank String skillCode, @NotBlank String name, String description, UUID defaultRateCardId) {}
