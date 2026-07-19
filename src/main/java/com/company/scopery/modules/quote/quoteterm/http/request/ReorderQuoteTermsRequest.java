package com.company.scopery.modules.quote.quoteterm.http.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderQuoteTermsRequest(@NotEmpty List<UUID> termIds) {}
