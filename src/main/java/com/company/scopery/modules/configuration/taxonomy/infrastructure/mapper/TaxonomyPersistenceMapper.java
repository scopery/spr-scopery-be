package com.company.scopery.modules.configuration.taxonomy.infrastructure.mapper;
import com.company.scopery.modules.configuration.taxonomy.domain.model.*;
import com.company.scopery.modules.configuration.taxonomy.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class TaxonomyPersistenceMapper {
    public Taxonomy toDomain(TaxonomyJpaEntity e) {
        return new Taxonomy(e.getId(), e.getWorkspaceId(), e.getTaxonomyCode(), e.getName(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TaxonomyJpaEntity toJpa(Taxonomy d) {
        TaxonomyJpaEntity e = new TaxonomyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setTaxonomyCode(d.taxonomyCode()); e.setName(d.name()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public TaxonomyTerm toDomain(TaxonomyTermJpaEntity e) {
        return new TaxonomyTerm(e.getId(), e.getTaxonomyId(), e.getParentTermId(), e.getTermCode(), e.getLabel(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TaxonomyTermJpaEntity toJpa(TaxonomyTerm d) {
        TaxonomyTermJpaEntity e = new TaxonomyTermJpaEntity();
        e.setId(d.id()); e.setTaxonomyId(d.taxonomyId()); e.setParentTermId(d.parentTermId()); e.setTermCode(d.termCode()); e.setLabel(d.label()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
