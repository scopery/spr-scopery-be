package com.company.scopery.modules.quote.quoteversion.http.request;

import jakarta.validation.constraints.NotBlank;

public record RejectQuoteVersionRequest(@NotBlank String reason) {}
