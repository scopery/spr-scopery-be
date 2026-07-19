package com.company.scopery.modules.configuration.statusset.domain.model;
import java.util.*; import java.util.UUID;
public interface StatusValueRepository {
    StatusValue save(StatusValue v);
    List<StatusValue> findBySetId(UUID setId);
}
