package com.company.scopery.modules.traceability.screen.infrastructure.persistence;
import com.company.scopery.modules.traceability.screen.domain.model.*;
import com.company.scopery.modules.traceability.screen.infrastructure.mapper.RegistryScreenPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryScreenRepository implements RegistryScreenRepository {
    private final SpringDataRegistryScreenJpaRepository springData;
    private final RegistryScreenPersistenceMapper mapper;
    public JpaRegistryScreenRepository(SpringDataRegistryScreenJpaRepository springData, RegistryScreenPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryScreen save(RegistryScreen e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryScreen> findByIdAndApplicationId(UUID id, UUID applicationId) {
        return springData.findByIdAndApplicationId(id, applicationId).map(mapper::toDomain);
    }
    @Override public List<RegistryScreen> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream().map(mapper::toDomain).toList();
    }
}
