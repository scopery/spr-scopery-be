package com.company.scopery.modules.scope.review.domain.model;
import java.util.Optional; import java.util.UUID;
public interface DeliverableAcceptanceRepository {
    DeliverableAcceptance save(DeliverableAcceptance acceptance);
    Optional<DeliverableAcceptance> findLatestByDeliverableId(UUID deliverableId);
}
