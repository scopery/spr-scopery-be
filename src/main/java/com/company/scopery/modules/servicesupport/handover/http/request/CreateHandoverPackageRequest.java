package com.company.scopery.modules.servicesupport.handover.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateHandoverPackageRequest(@NotNull UUID projectId, @NotBlank String title) {}
