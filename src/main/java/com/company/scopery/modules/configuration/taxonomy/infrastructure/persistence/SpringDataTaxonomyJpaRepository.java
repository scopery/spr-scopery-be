package com.company.scopery.modules.configuration.taxonomy.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataTaxonomyJpaRepository extends JpaRepository<TaxonomyJpaEntity, UUID> {
    Optional<TaxonomyJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<TaxonomyJpaEntity> findByWorkspaceIdOrderByNameAsc(UUID workspaceId);
}
