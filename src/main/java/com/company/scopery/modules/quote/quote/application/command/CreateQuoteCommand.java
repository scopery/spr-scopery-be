package com.company.scopery.modules.quote.quote.application.command;

import java.util.UUID;

public record CreateQuoteCommand(
        UUID projectId,
        String code,
        String title,
        String description,
        UUID sourceFinanceScenarioId,
        String clientName,
        String clientCompany,
        String clientEmail,
        String clientContactName,
        String clientReference
) {}
