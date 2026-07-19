package com.company.scopery.modules.projectbaseline.changeapproval.infrastructure.persistence;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.*;
import com.company.scopery.modules.projectbaseline.changeapproval.infrastructure.mapper.ChangeApprovalActionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JpaChangeApprovalActionRepository implements ChangeApprovalActionRepository {
    private final SpringDataChangeApprovalActionJpaRepository springData;
    private final ChangeApprovalActionPersistenceMapper mapper;
    public JpaChangeApprovalActionRepository(SpringDataChangeApprovalActionJpaRepository springData,
                                             ChangeApprovalActionPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ChangeApprovalAction save(ChangeApprovalAction action) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(action)));
    }
    @Override public List<ChangeApprovalAction> findByChangeRequestId(UUID changeRequestId) {
        return springData.findByChangeRequestIdOrderByCreatedAtAsc(changeRequestId).stream().map(mapper::toDomain).toList();
    }
}
