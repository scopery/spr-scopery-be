package com.company.scopery.modules.servicesupport.worklink.application.command;
import java.util.UUID;
public record CreateWorkLinkCommand(UUID supportCaseId, String targetObjectType, UUID targetObjectId, String linkType) {}
