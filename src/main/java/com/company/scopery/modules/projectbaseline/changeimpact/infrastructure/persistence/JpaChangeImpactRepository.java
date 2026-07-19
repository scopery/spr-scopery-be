package com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.persistence;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.*;
import com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.mapper.ChangeImpactPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JpaChangeImpactRepository implements ChangeImpactRepository {
    private final SpringDataChangeImpactJpaRepository springData;
    private final ChangeImpactPersistenceMapper mapper;
    public JpaChangeImpactRepository(SpringDataChangeImpactJpaRepository springData, ChangeImpactPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ChangeImpact save(ChangeImpact impact) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(impact)));
    }
    @Override public Optional<ChangeImpact> findByChangeRequestId(UUID changeRequestId) {
        return springData.findByChangeRequestId(changeRequestId).map(mapper::toDomain);
    }
}
