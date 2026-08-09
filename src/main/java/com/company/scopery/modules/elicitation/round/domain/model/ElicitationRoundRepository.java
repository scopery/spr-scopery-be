package com.company.scopery.modules.elicitation.round.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElicitationRoundRepository {

    ElicitationRound save(ElicitationRound round);

    Optional<ElicitationRound> findById(UUID id);

    List<ElicitationRound> findAllBySessionId(UUID sessionId);

    int countBySessionId(UUID sessionId);
}
