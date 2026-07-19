package com.company.scopery.modules.servicesupport.assignment.http.request;
import java.util.UUID;
public record AssignSupportCaseRequest(UUID assigneeUserId, UUID resourceProfileId) {}
