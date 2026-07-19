package com.company.scopery.modules.quote.quote.application.command;

import java.util.UUID;

public record UpdateQuoteCommand(
        UUID projectId,
        UUID quoteId,
        String title,
        String description,
        String clientName,
        String clientCompany,
        String clientEmail,
        String clientContactName,
        String clientReference
) {}
