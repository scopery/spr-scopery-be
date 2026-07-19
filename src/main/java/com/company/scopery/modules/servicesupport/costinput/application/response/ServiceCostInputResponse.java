package com.company.scopery.modules.servicesupport.costinput.application.response;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInput;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record ServiceCostInputResponse(UUID id, UUID workspaceId, UUID supportCaseId, String sourceType,
        BigDecimal costAmount, String currency, String status, Instant createdAt) {
    public static ServiceCostInputResponse from(ServiceCostInput d) {
        return new ServiceCostInputResponse(d.id(), d.workspaceId(), d.supportCaseId(), d.sourceType(),
                d.costAmount(), d.currency(), d.status(), d.createdAt());
    }
}
