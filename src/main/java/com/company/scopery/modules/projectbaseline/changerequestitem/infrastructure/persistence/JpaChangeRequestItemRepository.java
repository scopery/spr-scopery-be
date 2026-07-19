package com.company.scopery.modules.projectbaseline.changerequestitem.infrastructure.persistence;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.*;
import com.company.scopery.modules.projectbaseline.changerequestitem.infrastructure.mapper.ChangeRequestItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JpaChangeRequestItemRepository implements ChangeRequestItemRepository {
    private final SpringDataChangeRequestItemJpaRepository springData;
    private final ChangeRequestItemPersistenceMapper mapper;
    public JpaChangeRequestItemRepository(SpringDataChangeRequestItemJpaRepository springData, ChangeRequestItemPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ChangeRequestItem save(ChangeRequestItem item) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item)));
    }
    @Override public Optional<ChangeRequestItem> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public Optional<ChangeRequestItem> findByIdAndChangeRequestId(UUID id, UUID changeRequestId) {
        return springData.findByIdAndChangeRequestId(id, changeRequestId).map(mapper::toDomain);
    }
    @Override public List<ChangeRequestItem> findByChangeRequestId(UUID changeRequestId) {
        return springData.findByChangeRequestIdOrderByCreatedAtAsc(changeRequestId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(ChangeRequestItem item) { springData.deleteById(item.id()); }
}
