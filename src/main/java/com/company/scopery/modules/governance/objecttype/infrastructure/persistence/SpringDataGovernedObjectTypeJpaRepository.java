package com.company.scopery.modules.governance.objecttype.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataGovernedObjectTypeJpaRepository extends JpaRepository<GovernedObjectTypeJpaEntity, UUID> {
    Optional<GovernedObjectTypeJpaEntity> findByObjectTypeCode(String objectTypeCode);
    List<GovernedObjectTypeJpaEntity> findAllByEnabledTrue();
    boolean existsByObjectTypeCode(String objectTypeCode);
}
