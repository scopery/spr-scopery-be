package com.company.scopery.modules.configuration.fieldoption.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataCustomFieldOptionJpaRepository extends JpaRepository<CustomFieldOptionJpaEntity, UUID> {
    List<CustomFieldOptionJpaEntity> findByCustomFieldDefinitionIdOrderBySortOrderAsc(UUID fieldId);
}
