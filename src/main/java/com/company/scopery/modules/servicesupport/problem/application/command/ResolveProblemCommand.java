package com.company.scopery.modules.servicesupport.problem.application.command;
import java.util.UUID;
public record ResolveProblemCommand(String rootCause, String workaround, UUID resolvedBy) {}
