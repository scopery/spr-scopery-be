package com.company.scopery.modules.documenthub.template.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateDocumentTemplateRequest(@NotBlank String code, @NotBlank String name, String description, String category) {}
