package com.company.scopery.modules.servicesupport.handover.application.command;
import java.util.UUID;
public record CreateHandoverPackageCommand(UUID projectId, String title) {}
