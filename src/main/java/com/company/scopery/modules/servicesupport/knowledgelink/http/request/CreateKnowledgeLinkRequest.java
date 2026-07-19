package com.company.scopery.modules.servicesupport.knowledgelink.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateKnowledgeLinkRequest(UUID supportCaseId, UUID documentId, @NotBlank String linkType, Boolean clientVisible) {}
