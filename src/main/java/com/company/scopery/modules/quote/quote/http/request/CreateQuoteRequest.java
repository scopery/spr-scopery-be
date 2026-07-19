package com.company.scopery.modules.quote.quote.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateQuoteRequest(
        @NotBlank @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String title,
        String description,
        @NotNull UUID sourceFinanceScenarioId,
        @Size(max = 255) String clientName,
        @Size(max = 255) String clientCompany,
        @Size(max = 255) String clientEmail,
        @Size(max = 255) String clientContactName,
        @Size(max = 255) String clientReference
) {}
