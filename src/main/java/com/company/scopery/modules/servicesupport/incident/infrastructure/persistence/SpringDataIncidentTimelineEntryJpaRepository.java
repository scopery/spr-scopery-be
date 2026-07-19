package com.company.scopery.modules.servicesupport.incident.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataIncidentTimelineEntryJpaRepository extends JpaRepository<IncidentTimelineEntryJpaEntity, UUID> {
    List<IncidentTimelineEntryJpaEntity> findByIncidentId(UUID incidentId);
}
