package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataCustomFormFieldJpaRepository extends JpaRepository<CustomFormFieldJpaEntity, UUID> {
    List<CustomFormFieldJpaEntity> findByFormVersionIdOrderBySortOrderAsc(UUID versionId);
}
