package com.company.scopery.modules.quality.testrun.application.response;
import java.util.List; import java.util.UUID;
public record RunMembershipResponse(UUID runId, List<RunMembershipItemResponse> items) {}
