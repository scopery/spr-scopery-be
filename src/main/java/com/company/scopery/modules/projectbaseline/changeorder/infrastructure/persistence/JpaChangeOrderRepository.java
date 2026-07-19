package com.company.scopery.modules.projectbaseline.changeorder.infrastructure.persistence;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.*;
import com.company.scopery.modules.projectbaseline.changeorder.infrastructure.mapper.ChangeOrderPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JpaChangeOrderRepository implements ChangeOrderRepository {
    private final SpringDataChangeOrderJpaRepository springData;
    private final ChangeOrderPersistenceMapper mapper;
    public JpaChangeOrderRepository(SpringDataChangeOrderJpaRepository springData, ChangeOrderPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ChangeOrder save(ChangeOrder changeOrder) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(changeOrder)));
    }
    @Override public Optional<ChangeOrder> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public Optional<ChangeOrder> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ChangeOrder> findByChangeRequestId(UUID changeRequestId) {
        return springData.findByChangeRequestIdOrderByCreatedAtDesc(changeRequestId).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springData.existsByProjectIdAndCode(projectId, code);
    }
}
