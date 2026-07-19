package com.company.scopery.modules.servicesupport.problem.application.command;
import java.util.UUID;
public record CreateProblemCommand(String title, UUID projectId) {}
