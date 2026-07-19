package com.company.scopery.modules.servicesupport.knowledgelink.application.command;
import java.util.UUID;
public record CreateKnowledgeLinkCommand(UUID supportCaseId, UUID documentId, String linkType, boolean clientVisible) {}
