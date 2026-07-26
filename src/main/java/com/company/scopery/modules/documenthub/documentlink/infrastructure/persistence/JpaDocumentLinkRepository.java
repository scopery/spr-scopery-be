package com.company.scopery.modules.documenthub.documentlink.infrastructure.persistence;

import com.company.scopery.modules.documenthub.documentlink.domain.model.DocumentLink;
import com.company.scopery.modules.documenthub.documentlink.domain.model.DocumentLinkRepository;
import com.company.scopery.modules.documenthub.documentlink.infrastructure.mapper.DocumentLinkPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class JpaDocumentLinkRepository implements DocumentLinkRepository {

    private final SpringDataDocumentLinkJpaRepository springData;
    private final DocumentLinkPersistenceMapper mapper;
    private final EntityManager em;

    public JpaDocumentLinkRepository(SpringDataDocumentLinkJpaRepository springData,
                                      DocumentLinkPersistenceMapper mapper,
                                      EntityManager em) {
        this.springData = springData;
        this.mapper = mapper;
        this.em = em;
    }

    @Override
    public DocumentLink save(DocumentLink link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public List<DocumentLink> saveAll(List<DocumentLink> links) {
        List<DocumentLinkJpaEntity> entities = links.stream().map(mapper::toJpaEntity).toList();
        return springData.saveAllAndFlush(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByDocumentIdAndTargetTypeAndTargetIdAndLinkType(
            UUID documentId, String targetType, UUID targetId, String linkType) {
        return springData.existsByDocumentIdAndTargetTypeAndTargetIdAndLinkType(
                documentId, targetType, targetId, linkType);
    }

    @Override
    public List<DocumentLink> findByWorkspaceAndEntityFiltered(
            UUID workspaceId,
            String linkedEntityType,
            UUID linkedEntityId,
            UUID projectId,
            String relationType,
            boolean includeArchived,
            int limit,
            int offset) {

        StringBuilder jpql = new StringBuilder(
                "SELECT l FROM DocumentLinkJpaEntity l " +
                "JOIN DocumentJpaEntity d ON d.id = l.documentId " +
                "WHERE d.workspaceId = :workspaceId " +
                "AND l.targetType = :linkedEntityType " +
                "AND l.targetId = :linkedEntityId");

        if (projectId != null) {
            jpql.append(" AND l.projectId = :projectId");
        }
        if (relationType != null) {
            jpql.append(" AND l.linkType = :relationType");
        }
        if (!includeArchived) {
            jpql.append(" AND l.archivedAt IS NULL");
        }
        jpql.append(" ORDER BY l.createdAt DESC");

        TypedQuery<DocumentLinkJpaEntity> query = em.createQuery(jpql.toString(), DocumentLinkJpaEntity.class);
        query.setParameter("workspaceId", workspaceId);
        query.setParameter("linkedEntityType", linkedEntityType);
        query.setParameter("linkedEntityId", linkedEntityId);
        if (projectId != null) query.setParameter("projectId", projectId);
        if (relationType != null) query.setParameter("relationType", relationType);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList().stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByWorkspaceAndEntityFiltered(
            UUID workspaceId,
            String linkedEntityType,
            UUID linkedEntityId,
            UUID projectId,
            String relationType,
            boolean includeArchived) {

        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(l) FROM DocumentLinkJpaEntity l " +
                "JOIN DocumentJpaEntity d ON d.id = l.documentId " +
                "WHERE d.workspaceId = :workspaceId " +
                "AND l.targetType = :linkedEntityType " +
                "AND l.targetId = :linkedEntityId");

        if (projectId != null) {
            jpql.append(" AND l.projectId = :projectId");
        }
        if (relationType != null) {
            jpql.append(" AND l.linkType = :relationType");
        }
        if (!includeArchived) {
            jpql.append(" AND l.archivedAt IS NULL");
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        query.setParameter("workspaceId", workspaceId);
        query.setParameter("linkedEntityType", linkedEntityType);
        query.setParameter("linkedEntityId", linkedEntityId);
        if (projectId != null) query.setParameter("projectId", projectId);
        if (relationType != null) query.setParameter("relationType", relationType);

        return query.getSingleResult();
    }

    @Override
    public Map<UUID, Long> countActiveLinksByDocumentIds(List<UUID> documentIds) {
        Map<UUID, Long> result = new HashMap<>();
        if (documentIds == null || documentIds.isEmpty()) {
            return result;
        }
        // Initialize all requested document IDs with 0
        for (UUID id : documentIds) {
            result.put(id, 0L);
        }

        List<Object[]> rows = em.createQuery(
                "SELECT l.documentId, COUNT(l) FROM DocumentLinkJpaEntity l " +
                "WHERE l.documentId IN :documentIds AND l.archivedAt IS NULL " +
                "GROUP BY l.documentId",
                Object[].class)
                .setParameter("documentIds", documentIds)
                .getResultList();

        for (Object[] row : rows) {
            result.put((UUID) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public Map<UUID, Long> countActiveLinksByEntityIds(String linkedEntityType, UUID projectId, List<UUID> entityIds) {
        Map<UUID, Long> result = new HashMap<>();
        if (entityIds == null || entityIds.isEmpty()) {
            return result;
        }
        // Initialize all requested entity IDs with 0
        for (UUID id : entityIds) {
            result.put(id, 0L);
        }

        List<Object[]> rows = em.createQuery(
                "SELECT l.targetId, COUNT(l) FROM DocumentLinkJpaEntity l " +
                "WHERE l.targetType = :linkedEntityType " +
                "AND l.projectId = :projectId " +
                "AND l.targetId IN :entityIds " +
                "AND l.archivedAt IS NULL " +
                "GROUP BY l.targetId",
                Object[].class)
                .setParameter("linkedEntityType", linkedEntityType)
                .setParameter("projectId", projectId)
                .setParameter("entityIds", entityIds)
                .getResultList();

        for (Object[] row : rows) {
            result.put((UUID) row[0], (Long) row[1]);
        }
        return result;
    }
}
