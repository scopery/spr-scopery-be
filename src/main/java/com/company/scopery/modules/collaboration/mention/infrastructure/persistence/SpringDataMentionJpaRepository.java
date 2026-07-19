package com.company.scopery.modules.collaboration.mention.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataMentionJpaRepository extends JpaRepository<MentionJpaEntity, UUID> {
    List<MentionJpaEntity> findBySourceTypeAndSourceId(String sourceType, UUID sourceId);
}
