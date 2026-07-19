package com.company.scopery.modules.productivity.search.infrastructure.persistence;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param;
import java.util.UUID;
public interface SpringDataSearchIndexJpaRepository extends JpaRepository<SearchIndexJpaEntity, UUID> {
    @Query("select e from SearchIndexJpaEntity e where e.workspaceId = :workspaceId and e.restricted = false and (lower(e.title) like lower(concat('%', :q, '%')) or lower(e.bodyText) like lower(concat('%', :q, '%')))")
    Page<SearchIndexJpaEntity> search(@Param("workspaceId") UUID workspaceId, @Param("q") String q, Pageable pageable);
}
