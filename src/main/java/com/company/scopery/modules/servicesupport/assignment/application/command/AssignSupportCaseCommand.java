package com.company.scopery.modules.servicesupport.assignment.application.command;
import java.util.UUID;
public record AssignSupportCaseCommand(UUID assigneeUserId, UUID resourceProfileId) {}
