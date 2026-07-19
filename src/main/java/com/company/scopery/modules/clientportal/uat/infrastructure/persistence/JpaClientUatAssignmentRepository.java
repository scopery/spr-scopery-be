package com.company.scopery.modules.clientportal.uat.infrastructure.persistence;
import com.company.scopery.modules.clientportal.uat.domain.model.*;
import com.company.scopery.modules.clientportal.uat.infrastructure.mapper.ClientUatAssignmentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaClientUatAssignmentRepository implements ClientUatAssignmentRepository {
    private final SpringDataClientUatAssignmentJpaRepository springData;
    private final ClientUatAssignmentPersistenceMapper mapper;
    public JpaClientUatAssignmentRepository(SpringDataClientUatAssignmentJpaRepository springData, ClientUatAssignmentPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ClientUatAssignment save(ClientUatAssignment e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public List<ClientUatAssignment> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
