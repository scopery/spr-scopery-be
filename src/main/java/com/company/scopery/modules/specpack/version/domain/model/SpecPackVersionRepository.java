package com.company.scopery.modules.specpack.version.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecPackVersionRepository {

    SpecPackVersion save(SpecPackVersion version);

    Optional<SpecPackVersion> findById(UUID versionId);

    List<SpecPackVersion> findAllBySpecPackId(UUID specPackId);

    Optional<Integer> findMaxVersionNumberBySpecPackId(UUID specPackId);
}
