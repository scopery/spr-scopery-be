package com.company.scopery.modules.trust.classification.infrastructure.persistence;
import com.company.scopery.modules.trust.classification.domain.model.DataClassificationPolicy;
import com.company.scopery.modules.trust.classification.domain.model.DataClassificationPolicyRepository;
import com.company.scopery.modules.trust.classification.infrastructure.mapper.DataClassificationPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaDataClassificationPolicyRepository implements DataClassificationPolicyRepository {
    private final SpringDataDataClassificationPolicyJpaRepository spring;
    private final DataClassificationPolicyPersistenceMapper mapper;
    public JpaDataClassificationPolicyRepository(SpringDataDataClassificationPolicyJpaRepository spring, DataClassificationPolicyPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public DataClassificationPolicy save(DataClassificationPolicy p) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(p)));
    }
    @Override public Optional<DataClassificationPolicy> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).map(mapper::toDomain);
    }
    @Override public List<DataClassificationPolicy> findAll() {
        return spring.findAll().stream().map(mapper::toDomain).toList();
    }
}
