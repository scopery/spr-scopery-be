package com.company.scopery.modules.configuration.taxonomy.domain.model;
import java.util.*; import java.util.UUID;
public interface TaxonomyRepository {
    Taxonomy save(Taxonomy t);
    Optional<Taxonomy> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<Taxonomy> findByWorkspaceId(UUID workspaceId);
}
