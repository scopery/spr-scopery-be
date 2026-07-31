package com.company.scopery.modules.quality.testrun.application.response;
import java.util.UUID;
public record RunMembershipItemResponse(String caseKind, UUID caseId, String caseCode, String caseTitle, String sourceGroupName, int displayOrder) {}
