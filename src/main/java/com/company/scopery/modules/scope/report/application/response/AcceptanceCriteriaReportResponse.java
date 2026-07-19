package com.company.scopery.modules.scope.report.application.response;
import java.util.Map;
public record AcceptanceCriteriaReportResponse(Map<String, Long> statusCounts, long mandatoryOpenCount) {}
