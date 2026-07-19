package com.company.scopery.modules.resourcecapacity.resourceskill.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataResourceSkillJpaRepository extends JpaRepository<ResourceSkillJpaEntity, UUID> {
    List<ResourceSkillJpaEntity> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndSkillCode(UUID workspaceId, String code);
}
