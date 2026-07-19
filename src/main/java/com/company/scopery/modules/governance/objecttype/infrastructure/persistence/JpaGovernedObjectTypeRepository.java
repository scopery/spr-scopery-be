package com.company.scopery.modules.governance.objecttype.infrastructure.persistence;
import com.company.scopery.modules.governance.objecttype.domain.model.GovernedObjectType;
import com.company.scopery.modules.governance.objecttype.domain.model.GovernedObjectTypeRepository;
import com.company.scopery.modules.governance.objecttype.infrastructure.mapper.GovernedObjectTypePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional;
@Repository
public class JpaGovernedObjectTypeRepository implements GovernedObjectTypeRepository {
    private final SpringDataGovernedObjectTypeJpaRepository springData;
    private final GovernedObjectTypePersistenceMapper mapper;
    public JpaGovernedObjectTypeRepository(SpringDataGovernedObjectTypeJpaRepository springData, GovernedObjectTypePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public GovernedObjectType save(GovernedObjectType t) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(t))); }
    @Override public Optional<GovernedObjectType> findByCode(String code) { return springData.findByObjectTypeCode(code).map(mapper::toDomain); }
    @Override public List<GovernedObjectType> findAllEnabled() { return springData.findAllByEnabledTrue().stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByCode(String code) { return springData.existsByObjectTypeCode(code); }
}
