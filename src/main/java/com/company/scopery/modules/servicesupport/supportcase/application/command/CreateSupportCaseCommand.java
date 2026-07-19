package com.company.scopery.modules.servicesupport.supportcase.application.command;
import java.util.UUID;
public record CreateSupportCaseCommand(String title, String requestTypeCode, String priority, UUID projectId,
        String source, boolean portalVisible) {}
