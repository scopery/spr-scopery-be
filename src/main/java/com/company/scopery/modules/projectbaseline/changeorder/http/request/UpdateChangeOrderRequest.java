package com.company.scopery.modules.projectbaseline.changeorder.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record UpdateChangeOrderRequest(@NotBlank String title, String description,
        String commercialImpactJson, UUID sourceQuoteVersionId) {}
