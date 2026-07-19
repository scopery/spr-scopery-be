package com.company.scopery.modules.configuration.taxonomy.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataTaxonomyTermJpaRepository extends JpaRepository<TaxonomyTermJpaEntity, UUID> {
    List<TaxonomyTermJpaEntity> findByTaxonomyIdOrderByTermCodeAsc(UUID taxonomyId);
}
