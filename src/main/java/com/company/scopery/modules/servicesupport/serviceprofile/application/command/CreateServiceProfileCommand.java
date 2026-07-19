package com.company.scopery.modules.servicesupport.serviceprofile.application.command;
import java.util.UUID;
public record CreateServiceProfileCommand(String scopeType, UUID projectId, boolean portalIntakeEnabled) {}
