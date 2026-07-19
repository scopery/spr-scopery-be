package com.company.scopery.modules.servicesupport.requesttype.application.command;
public record CreateSupportRequestTypeCommand(String typeCode, String name, String defaultPriority, boolean portalVisible) {}
