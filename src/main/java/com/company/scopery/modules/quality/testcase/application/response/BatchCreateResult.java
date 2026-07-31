package com.company.scopery.modules.quality.testcase.application.response;
import java.util.List;
public record BatchCreateResult(List<TestCaseResponse> created, List<BatchRowError> errors) {
    public record BatchRowError(int rowIndex, String reason) {}
}
