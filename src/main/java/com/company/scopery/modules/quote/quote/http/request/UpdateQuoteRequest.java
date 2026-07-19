package com.company.scopery.modules.quote.quote.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateQuoteRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        @Size(max = 255) String clientName,
        @Size(max = 255) String clientCompany,
        @Size(max = 255) String clientEmail,
        @Size(max = 255) String clientContactName,
        @Size(max = 255) String clientReference
) {}
