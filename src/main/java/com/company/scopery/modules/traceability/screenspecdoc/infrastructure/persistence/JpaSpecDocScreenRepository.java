package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence;

import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreen;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreenRepository;
import com.company.scopery.modules.traceability.screenspecdoc.infrastructure.mapper.SpecDocScreenPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSpecDocScreenRepository implements SpecDocScreenRepository {

    private final SpringDataSpecDocScreenJpaRepository springData;
    private final SpecDocScreenPersistenceMapper mapper;

    public JpaSpecDocScreenRepository(SpringDataSpecDocScreenJpaRepository springData,
                                      SpecDocScreenPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public List<SpecDocScreen> findByDocumentId(UUID documentId) {
        return springData.findByIdDocumentId(documentId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<SpecDocScreen> findByDocumentIdAndScreenId(UUID documentId, UUID screenId) {
        return springData.findByIdDocumentIdAndIdScreenId(documentId, screenId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDocumentIdAndScreenId(UUID documentId, UUID screenId) {
        return springData.existsByIdDocumentIdAndIdScreenId(documentId, screenId);
    }

    @Override
    public SpecDocScreen save(SpecDocScreen link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public void deleteByDocumentIdAndScreenId(UUID documentId, UUID screenId) {
        springData.deleteByIdDocumentIdAndIdScreenId(documentId, screenId);
    }
}
