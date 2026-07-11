package com.company.scopery.modules.iam.me.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface MeProfileRepository {

    Optional<MeProfile> findByUserId(UUID userId);
}
