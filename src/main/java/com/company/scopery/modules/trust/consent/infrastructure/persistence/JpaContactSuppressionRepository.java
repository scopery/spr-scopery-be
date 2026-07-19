package com.company.scopery.modules.trust.consent.infrastructure.persistence;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppression;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppressionRepository;
import com.company.scopery.modules.trust.consent.infrastructure.mapper.ConsentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaContactSuppressionRepository implements ContactSuppressionRepository {
    private final SpringDataContactSuppressionJpaRepository spring;
    private final ConsentPersistenceMapper mapper;
    public JpaContactSuppressionRepository(SpringDataContactSuppressionJpaRepository spring, ConsentPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ContactSuppression save(ContactSuppression s) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(s)));
    }
    @Override public Optional<ContactSuppression> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }
    @Override public List<ContactSuppression> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
