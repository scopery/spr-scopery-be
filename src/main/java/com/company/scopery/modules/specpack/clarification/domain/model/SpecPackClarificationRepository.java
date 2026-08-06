package com.company.scopery.modules.specpack.clarification.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecPackClarificationRepository {

    SpecPackClarification save(SpecPackClarification clarification);

    Optional<SpecPackClarification> findById(UUID id);

    List<SpecPackClarification> findAllBySessionId(UUID sessionId);

    List<SpecPackClarification> findAllBySessionIdAndStatus(UUID sessionId, String status);

    long countBySessionIdAndPriorityAndStatus(UUID sessionId, String priority, String status);
}
