package com.company.scopery.modules.configuration.taxonomy.domain.model;
import java.util.*; import java.util.UUID;
public interface TaxonomyTermRepository {
    TaxonomyTerm save(TaxonomyTerm t);
    List<TaxonomyTerm> findByTaxonomyId(UUID taxonomyId);
}
