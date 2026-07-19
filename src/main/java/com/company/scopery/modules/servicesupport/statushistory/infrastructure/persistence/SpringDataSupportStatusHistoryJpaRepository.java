package com.company.scopery.modules.servicesupport.statushistory.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportStatusHistoryJpaRepository extends JpaRepository<SupportStatusHistoryJpaEntity, UUID> {
    List<SupportStatusHistoryJpaEntity> findBySupportCaseId(UUID caseId);
}
