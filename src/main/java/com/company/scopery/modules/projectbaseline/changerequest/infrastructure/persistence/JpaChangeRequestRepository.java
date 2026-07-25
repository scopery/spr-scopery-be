package com.company.scopery.modules.projectbaseline.changerequest.infrastructure.persistence;

import com.company.scopery.modules.projectbaseline.changerequest.domain.model.*;
import com.company.scopery.modules.projectbaseline.changerequest.infrastructure.mapper.ChangeRequestPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JpaChangeRequestRepository implements ChangeRequestRepository {
    private final SpringDataChangeRequestJpaRepository springData;
    private final ChangeRequestPersistenceMapper mapper;
    @PersistenceContext private EntityManager em;
    public JpaChangeRequestRepository(SpringDataChangeRequestJpaRepository springData, ChangeRequestPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ChangeRequest save(ChangeRequest cr) {
        var entity = mapper.toJpaEntity(cr);
        var merged = em.merge(entity);
        em.flush();
        return mapper.toDomain(merged);
    }
    @Override public Optional<ChangeRequest> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public Optional<ChangeRequest> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ChangeRequest> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springData.existsByProjectIdAndCode(projectId, code);
    }
}
