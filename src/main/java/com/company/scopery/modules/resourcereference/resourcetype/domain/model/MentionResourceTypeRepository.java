package com.company.scopery.modules.resourcereference.resourcetype.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentionResourceTypeRepository {
    MentionResourceTypeDefinition save(MentionResourceTypeDefinition type);
    Optional<MentionResourceTypeDefinition> findById(UUID id);
    Optional<MentionResourceTypeDefinition> findByCode(String code);
    boolean existsByCode(String code);
    List<MentionResourceTypeDefinition> findAll();
    List<MentionResourceTypeDefinition> findAllEnabled();
}
