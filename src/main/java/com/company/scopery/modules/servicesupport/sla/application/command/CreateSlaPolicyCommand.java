package com.company.scopery.modules.servicesupport.sla.application.command;
public record CreateSlaPolicyCommand(String policyCode, String name, Integer firstResponseMinutes, Integer resolveMinutes) {}
