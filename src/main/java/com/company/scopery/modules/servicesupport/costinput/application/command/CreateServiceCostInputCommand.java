package com.company.scopery.modules.servicesupport.costinput.application.command;
import java.math.BigDecimal; import java.util.UUID;
public record CreateServiceCostInputCommand(UUID supportCaseId, String sourceType, BigDecimal costAmount, String currency) {}
