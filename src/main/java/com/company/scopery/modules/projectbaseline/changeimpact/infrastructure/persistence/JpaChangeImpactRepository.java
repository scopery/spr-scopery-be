package com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.persistence;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.*;
import com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.mapper.ChangeImpactPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JpaChangeImpactRepository implements ChangeImpactRepository {
    private final SpringDataChangeImpactJpaRepository springData;
    private final ChangeImpactPersistenceMapper mapper;
    @PersistenceContext private EntityManager em;
    public JpaChangeImpactRepository(SpringDataChangeImpactJpaRepository springData, ChangeImpactPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ChangeImpact save(ChangeImpact impact) {
        var entity = mapper.toJpaEntity(impact);
        var merged = em.merge(entity);
        em.flush();
        return mapper.toDomain(merged);
    }
    @Override public Optional<ChangeImpact> findByChangeRequestId(UUID changeRequestId) {
        return springData.findByChangeRequestId(changeRequestId).map(mapper::toDomain);
    }
}
