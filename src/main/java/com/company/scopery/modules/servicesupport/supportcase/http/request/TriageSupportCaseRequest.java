package com.company.scopery.modules.servicesupport.supportcase.http.request;
import java.util.UUID;
public record TriageSupportCaseRequest(UUID ownerUserId, UUID slaPolicyId) {}
