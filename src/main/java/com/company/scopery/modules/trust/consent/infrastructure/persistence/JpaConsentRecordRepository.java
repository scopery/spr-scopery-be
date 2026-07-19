package com.company.scopery.modules.trust.consent.infrastructure.persistence;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecord;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecordRepository;
import com.company.scopery.modules.trust.consent.infrastructure.mapper.ConsentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaConsentRecordRepository implements ConsentRecordRepository {
    private final SpringDataConsentRecordJpaRepository spring;
    private final ConsentPersistenceMapper mapper;
    public JpaConsentRecordRepository(SpringDataConsentRecordJpaRepository spring, ConsentPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ConsentRecord save(ConsentRecord r) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(r)));
    }
    @Override public Optional<ConsentRecord> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }
    @Override public List<ConsentRecord> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
