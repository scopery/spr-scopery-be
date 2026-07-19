package com.company.scopery.modules.servicesupport.problem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportProblemRecordJpaRepository extends JpaRepository<SupportProblemRecordJpaEntity, UUID> {
    List<SupportProblemRecordJpaEntity> findByWorkspaceId(UUID workspaceId);
}
