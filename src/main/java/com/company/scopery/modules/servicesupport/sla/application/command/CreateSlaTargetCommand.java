package com.company.scopery.modules.servicesupport.sla.application.command;
import java.util.UUID;
public record CreateSlaTargetCommand(UUID slaPolicyId, String targetType, int durationMinutes,
        UUID requestTypeId, String priority) {}
