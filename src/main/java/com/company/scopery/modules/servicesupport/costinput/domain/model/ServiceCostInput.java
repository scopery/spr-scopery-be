package com.company.scopery.modules.servicesupport.costinput.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record ServiceCostInput(UUID id, UUID workspaceId, UUID projectId, UUID serviceProfileId,
        UUID supportCaseId, UUID incidentId, UUID maintenanceActivityId, UUID resourceProfileId,
        String sourceType, UUID sourceId, BigDecimal effortHours, BigDecimal rateAmount, String currency,
        BigDecimal costAmount, String status, int version, Instant createdAt, Instant updatedAt) {
    public static ServiceCostInput create(UUID workspaceId, UUID supportCaseId, String sourceType, BigDecimal costAmount, String currency) {
        Instant now = Instant.now();
        return new ServiceCostInput(UUID.randomUUID(), workspaceId, null, null, supportCaseId, null, null, null,
                sourceType, null, null, null, currency, costAmount, "DRAFT", 0, now, now);
    }
    public ServiceCostInput approve() {
        return new ServiceCostInput(id, workspaceId, projectId, serviceProfileId, supportCaseId, incidentId,
                maintenanceActivityId, resourceProfileId, sourceType, sourceId, effortHours, rateAmount, currency,
                costAmount, "APPROVED", version, createdAt, Instant.now());
    }
    public ServiceCostInput reject() {
        return new ServiceCostInput(id, workspaceId, projectId, serviceProfileId, supportCaseId, incidentId,
                maintenanceActivityId, resourceProfileId, sourceType, sourceId, effortHours, rateAmount, currency,
                costAmount, "REJECTED", version, createdAt, Instant.now());
    }
}
