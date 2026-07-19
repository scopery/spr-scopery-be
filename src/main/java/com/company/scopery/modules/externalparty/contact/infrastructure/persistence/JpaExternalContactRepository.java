package com.company.scopery.modules.externalparty.contact.infrastructure.persistence;
import com.company.scopery.modules.externalparty.contact.domain.model.*;
import com.company.scopery.modules.externalparty.contact.infrastructure.mapper.ExternalContactPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaExternalContactRepository implements ExternalContactRepository {
    private final SpringDataExternalContactJpaRepository springData;
    private final ExternalContactPersistenceMapper mapper;
    public JpaExternalContactRepository(SpringDataExternalContactJpaRepository springData, ExternalContactPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ExternalContact save(ExternalContact e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ExternalContact> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<ExternalContact> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
