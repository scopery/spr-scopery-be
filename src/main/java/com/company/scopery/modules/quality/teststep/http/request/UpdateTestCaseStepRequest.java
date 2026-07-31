package com.company.scopery.modules.quality.teststep.http.request;
import java.util.UUID;
public record UpdateTestCaseStepRequest(String action, String expectedResult, UUID screenId, UUID componentId, Long version) {}
