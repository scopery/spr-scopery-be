package com.company.scopery.modules.configuration.taxonomy.infrastructure.persistence;
import com.company.scopery.modules.configuration.taxonomy.domain.model.*;
import com.company.scopery.modules.configuration.taxonomy.infrastructure.mapper.TaxonomyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTaxonomyRepository implements TaxonomyRepository {
    private final SpringDataTaxonomyJpaRepository springData; private final TaxonomyPersistenceMapper mapper;
    public JpaTaxonomyRepository(SpringDataTaxonomyJpaRepository springData, TaxonomyPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public Taxonomy save(Taxonomy t) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(t))); }
    @Override public Optional<Taxonomy> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<Taxonomy> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByNameAsc(workspaceId).stream().map(mapper::toDomain).toList(); }
}
