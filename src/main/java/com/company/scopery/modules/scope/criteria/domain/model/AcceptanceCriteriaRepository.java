package com.company.scopery.modules.scope.criteria.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface AcceptanceCriteriaRepository {
    AcceptanceCriteria save(AcceptanceCriteria c);
    Optional<AcceptanceCriteria> findById(UUID id);
    List<AcceptanceCriteria> findByDeliverableId(UUID deliverableId);
}
