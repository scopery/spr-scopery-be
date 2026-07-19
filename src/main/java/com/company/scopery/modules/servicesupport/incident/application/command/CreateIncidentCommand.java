package com.company.scopery.modules.servicesupport.incident.application.command;
import java.util.UUID;
public record CreateIncidentCommand(String title, String severity, UUID projectId) {}
