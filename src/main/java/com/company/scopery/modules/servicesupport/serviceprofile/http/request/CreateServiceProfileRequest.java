package com.company.scopery.modules.servicesupport.serviceprofile.http.request;
import java.util.UUID;
public record CreateServiceProfileRequest(String scopeType, UUID projectId, Boolean portalIntakeEnabled) {}
