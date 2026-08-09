package com.company.scopery.modules.elicitation.session.infrastructure.persistence;

import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.session.infrastructure.mapper.ElicitationSessionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaElicitationSessionRepository implements ElicitationSessionRepository {

    private final SpringDataElicitationSessionJpaRepository springDataRepo;
    private final ElicitationSessionPersistenceMapper mapper;

    public JpaElicitationSessionRepository(SpringDataElicitationSessionJpaRepository springDataRepo,
                                            ElicitationSessionPersistenceMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public ElicitationSession save(ElicitationSession session) {
        ElicitationSessionJpaEntity entity = mapper.toJpaEntity(session);
        ElicitationSessionJpaEntity saved = springDataRepo.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ElicitationSession> findById(UUID id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ElicitationSession> findAllByProjectId(UUID projectId) {
        return springDataRepo.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsActiveByProjectIdAndScopePackageId(UUID projectId, UUID scopePackageId) {
        return springDataRepo.existsByProjectIdAndScopePackageIdAndStatus(projectId, scopePackageId, "ACTIVE");
    }

    @Override
    public Optional<ElicitationSession> findActiveByProjectIdAndScopePackageId(UUID projectId, UUID scopePackageId) {
        return springDataRepo.findFirstByProjectIdAndScopePackageIdAndStatus(projectId, scopePackageId, "ACTIVE")
                .map(mapper::toDomain);
    }
}
