package com.company.scopery.modules.ratecard.ratecard.application.command;
import java.util.UUID;
public record UpdateRateCardCommand(UUID id, String name, String description, String defaultCurrencyCode) {}
