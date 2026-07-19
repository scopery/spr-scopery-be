package com.company.scopery.modules.servicesupport.supportcase.application.command;
import java.util.UUID;
public record TriageSupportCaseCommand(UUID ownerUserId, UUID slaPolicyId) {}
