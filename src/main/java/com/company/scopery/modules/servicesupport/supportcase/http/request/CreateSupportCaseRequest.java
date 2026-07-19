package com.company.scopery.modules.servicesupport.supportcase.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateSupportCaseRequest(@NotBlank String title, String requestTypeCode, String priority,
        UUID projectId, String source, Boolean portalVisible) {}
