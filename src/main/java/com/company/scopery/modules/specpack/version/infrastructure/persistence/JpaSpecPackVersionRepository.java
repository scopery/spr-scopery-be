package com.company.scopery.modules.specpack.version.infrastructure.persistence;

import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersion;
import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersionRepository;
import com.company.scopery.modules.specpack.version.infrastructure.mapper.SpecPackVersionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSpecPackVersionRepository implements SpecPackVersionRepository {

    private final SpringDataSpecPackVersionJpaRepository springDataRepository;
    private final SpecPackVersionPersistenceMapper mapper;

    public JpaSpecPackVersionRepository(SpringDataSpecPackVersionJpaRepository springDataRepository,
                                        SpecPackVersionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public SpecPackVersion save(SpecPackVersion version) {
        SpecPackVersionJpaEntity entity = mapper.toJpaEntity(version);
        SpecPackVersionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecPackVersion> findById(UUID versionId) {
        return springDataRepository.findById(versionId).map(mapper::toDomain);
    }

    @Override
    public List<SpecPackVersion> findAllBySpecPackId(UUID specPackId) {
        return springDataRepository.findBySpecPackIdOrderByVersionNumberDesc(specPackId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Integer> findMaxVersionNumberBySpecPackId(UUID specPackId) {
        return springDataRepository.findMaxVersionNumberBySpecPackId(specPackId);
    }
}
