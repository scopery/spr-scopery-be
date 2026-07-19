package com.company.scopery.modules.quality.testplan.application.response;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlan;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record TestPlanResponse(UUID id, UUID projectId, String code, String name, String testLevel, String status, Instant createdAt) {
    public static TestPlanResponse from(TestPlan e) { return new TestPlanResponse(e.id(), e.projectId(), e.code(), e.name(), e.testLevel().name(), e.status().name(), e.createdAt()); }
}
