package com.company.scopery.modules.trust.datasubject.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataDataSubjectIndexJpaRepository extends JpaRepository<DataSubjectIndexJpaEntity, UUID> {
    List<DataSubjectIndexJpaEntity> findByWorkspaceId(UUID workspaceId);
}
