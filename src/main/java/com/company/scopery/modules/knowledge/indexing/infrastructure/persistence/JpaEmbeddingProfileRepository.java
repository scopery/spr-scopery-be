package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import com.company.scopery.modules.knowledge.indexing.domain.model.EmbeddingProfile;
import com.company.scopery.modules.knowledge.indexing.domain.model.EmbeddingProfileRepository;
import com.company.scopery.modules.knowledge.indexing.infrastructure.mapper.EmbeddingProfilePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEmbeddingProfileRepository implements EmbeddingProfileRepository {

    private final SpringDataEmbeddingProfileJpaRepository springData;
    private final EmbeddingProfilePersistenceMapper mapper;

    public JpaEmbeddingProfileRepository(SpringDataEmbeddingProfileJpaRepository springData,
                                          EmbeddingProfilePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<EmbeddingProfile> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<EmbeddingProfile> findByCode(String code) {
        return springData.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public Optional<EmbeddingProfile> findActiveDefault() {
        return springData.findFirstByStatus("ACTIVE").map(mapper::toDomain);
    }
}
