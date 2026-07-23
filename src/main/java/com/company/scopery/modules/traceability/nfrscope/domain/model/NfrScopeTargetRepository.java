package com.company.scopery.modules.traceability.nfrscope.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NfrScopeTargetRepository {
    NfrScopeTarget save(NfrScopeTarget target);
    boolean existsByNfrIdAndTargetId(UUID nfrId, UUID targetId);
    Optional<NfrScopeTarget> findByNfrIdAndTargetId(UUID nfrId, UUID targetId);
    List<NfrScopeTarget> findByNfrId(UUID nfrId);
    List<NfrScopeTarget> findByTargetId(UUID targetId);
    void deleteByNfrIdAndTargetId(UUID nfrId, UUID targetId);
}
