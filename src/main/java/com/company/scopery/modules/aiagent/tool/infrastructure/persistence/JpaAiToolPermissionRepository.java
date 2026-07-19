package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermission;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermissionRepository;
import com.company.scopery.modules.aiagent.tool.infrastructure.mapper.AiToolPermissionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiToolPermissionRepository implements AiToolPermissionRepository {

    private final SpringDataAiToolPermissionJpaRepository springDataRepository;
    private final AiToolPermissionPersistenceMapper mapper;

    public JpaAiToolPermissionRepository(SpringDataAiToolPermissionJpaRepository springDataRepository,
                                         AiToolPermissionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiToolPermission save(AiToolPermission permission) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(permission)));
    }

    @Override
    public Optional<AiToolPermission> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByToolIdAndPermissionCode(UUID toolId, String permissionCode) {
        return springDataRepository.existsByToolIdAndPermissionCode(toolId, permissionCode);
    }

    @Override
    public List<AiToolPermission> findByToolId(UUID toolId) {
        return springDataRepository.findByToolId(toolId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }
}
