package com.company.scopery.modules.trust.sensitiveaccesslog.infrastructure.persistence;
import com.company.scopery.modules.trust.sensitiveaccesslog.domain.model.SensitiveAccessLog;
import com.company.scopery.modules.trust.sensitiveaccesslog.domain.model.SensitiveAccessLogRepository;
import com.company.scopery.modules.trust.sensitiveaccesslog.infrastructure.mapper.SensitiveAccessLogPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaSensitiveAccessLogRepository implements SensitiveAccessLogRepository {
    private final SpringDataSensitiveAccessLogJpaRepository spring;
    private final SensitiveAccessLogPersistenceMapper mapper;
    public JpaSensitiveAccessLogRepository(SpringDataSensitiveAccessLogJpaRepository spring, SensitiveAccessLogPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SensitiveAccessLog save(SensitiveAccessLog log) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(log)));
    }
    @Override public List<SensitiveAccessLog> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
