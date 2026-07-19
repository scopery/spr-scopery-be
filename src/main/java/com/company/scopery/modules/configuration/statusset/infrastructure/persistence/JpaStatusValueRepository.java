package com.company.scopery.modules.configuration.statusset.infrastructure.persistence;
import com.company.scopery.modules.configuration.statusset.domain.model.*;
import com.company.scopery.modules.configuration.statusset.infrastructure.mapper.StatusSetPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaStatusValueRepository implements StatusValueRepository {
    private final SpringDataStatusValueJpaRepository springData; private final StatusSetPersistenceMapper mapper;
    public JpaStatusValueRepository(SpringDataStatusValueJpaRepository springData, StatusSetPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public StatusValue save(StatusValue v) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(v))); }
    @Override public List<StatusValue> findBySetId(UUID setId) { return springData.findByStatusSetIdOrderBySortOrderAsc(setId).stream().map(mapper::toDomain).toList(); }
}
