package com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List; import java.util.UUID;
public interface SpringDataNfrTargetJpaRepository extends JpaRepository<NfrTargetJpaEntity, UUID> {
    List<NfrTargetJpaEntity> findByRequirementIdOrderByDisplayOrderAsc(UUID requirementId);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM NfrTargetJpaEntity e WHERE e.requirementId = :requirementId")
    int deleteByRequirementId(@Param("requirementId") UUID requirementId);
}
