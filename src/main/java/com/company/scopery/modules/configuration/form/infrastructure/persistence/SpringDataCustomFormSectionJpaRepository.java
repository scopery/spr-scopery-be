package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataCustomFormSectionJpaRepository extends JpaRepository<CustomFormSectionJpaEntity, UUID> {
    List<CustomFormSectionJpaEntity> findByFormVersionIdOrderBySortOrderAsc(UUID versionId);
}
