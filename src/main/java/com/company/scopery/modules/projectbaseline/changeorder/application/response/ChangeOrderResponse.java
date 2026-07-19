package com.company.scopery.modules.projectbaseline.changeorder.application.response;

import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrder;
import java.time.Instant; import java.util.UUID;

public record ChangeOrderResponse(
        UUID id, UUID changeRequestId, UUID projectId, UUID workspaceId, String code, String title,
        String description, String status, String commercialImpactJson, UUID sourceQuoteVersionId,
        UUID futureContractId, Instant approvedAt, UUID approvedBy, Instant createdAt, Instant updatedAt
) {
    public static ChangeOrderResponse from(ChangeOrder o) {
        return new ChangeOrderResponse(o.id(), o.changeRequestId(), o.projectId(), o.workspaceId(), o.code(),
                o.title(), o.description(), o.status().name(), o.commercialImpactJson(), o.sourceQuoteVersionId(),
                o.futureContractId(), o.approvedAt(), o.approvedBy(), o.createdAt(), o.updatedAt());
    }
}
