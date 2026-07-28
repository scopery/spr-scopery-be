package com.company.scopery.modules.traceability.requirement.http.request;
public record UpdateRequirementRequest(String title, String description, String priority, String requirementType, java.util.UUID applicationId, java.util.UUID functionalItemId, java.util.UUID nonFunctionalItemId, java.util.UUID scopeItemId, java.util.UUID scopePackageId){}
