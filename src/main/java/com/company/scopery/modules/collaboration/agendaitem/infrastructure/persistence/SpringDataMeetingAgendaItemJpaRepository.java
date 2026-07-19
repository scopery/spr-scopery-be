package com.company.scopery.modules.collaboration.agendaitem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingAgendaItemJpaRepository extends JpaRepository<MeetingAgendaItemJpaEntity, UUID> {
    Optional<MeetingAgendaItemJpaEntity> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingAgendaItemJpaEntity> findByMeetingIdOrderBySortOrderAsc(UUID meetingId);
}
