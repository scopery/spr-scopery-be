package com.company.scopery.modules.configuration.taxonomy.infrastructure.persistence;
import com.company.scopery.modules.configuration.taxonomy.domain.model.*;
import com.company.scopery.modules.configuration.taxonomy.infrastructure.mapper.TaxonomyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTaxonomyTermRepository implements TaxonomyTermRepository {
    private final SpringDataTaxonomyTermJpaRepository springData; private final TaxonomyPersistenceMapper mapper;
    public JpaTaxonomyTermRepository(SpringDataTaxonomyTermJpaRepository springData, TaxonomyPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public TaxonomyTerm save(TaxonomyTerm t) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(t))); }
    @Override public List<TaxonomyTerm> findByTaxonomyId(UUID taxonomyId) { return springData.findByTaxonomyIdOrderByTermCodeAsc(taxonomyId).stream().map(mapper::toDomain).toList(); }
}
