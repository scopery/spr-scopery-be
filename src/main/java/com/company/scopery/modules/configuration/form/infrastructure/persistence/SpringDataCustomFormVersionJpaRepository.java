package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query;
import java.util.*; import java.util.UUID;
public interface SpringDataCustomFormVersionJpaRepository extends JpaRepository<CustomFormVersionJpaEntity, UUID> {
    List<CustomFormVersionJpaEntity> findByFormDefinitionIdOrderByVersionNumberDesc(UUID formId);
    @Query("select coalesce(max(e.versionNumber), 0) from CustomFormVersionJpaEntity e where e.formDefinitionId = :formId")
    int maxVersionNumber(UUID formId);
}
