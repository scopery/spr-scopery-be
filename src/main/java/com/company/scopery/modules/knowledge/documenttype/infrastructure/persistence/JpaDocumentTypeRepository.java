package com.company.scopery.modules.knowledge.documenttype.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.infrastructure.mapper.DocumentTypePersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentTypeRepository implements DocumentTypeRepository {

    private final SpringDataDocumentTypeJpaRepository springDataRepository;
    private final DocumentTypePersistenceMapper mapper;

    public JpaDocumentTypeRepository(SpringDataDocumentTypeJpaRepository springDataRepository,
                                     DocumentTypePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public DocumentType save(DocumentType documentType) {
        DocumentTypeJpaEntity entity = mapper.toJpaEntity(documentType);
        DocumentTypeJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<DocumentType> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCodeAndScopeSystem(DocumentTypeCode code) {
        return springDataRepository.existsByCodeAndScopeSystem(code.value());
    }

    @Override
    public Optional<DocumentType> findByCodeAndScopeSystem(DocumentTypeCode code) {
        return springDataRepository.findByCodeAndScopeSystem(code.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCodeAndOrganizationId(DocumentTypeCode code, UUID organizationId) {
        return springDataRepository.existsByCodeAndOrganizationId(code.value(), organizationId);
    }

    @Override
    public boolean existsByCodeAndWorkspaceId(DocumentTypeCode code, UUID workspaceId) {
        return springDataRepository.existsByCodeAndWorkspaceId(code.value(), workspaceId);
    }

    @Override
    public PageResult<DocumentType> findAll(String keyword, UUID organizationId, UUID workspaceId,
                                            DocumentTypeScope documentScope, DocumentTypeStatus status,
                                            Boolean builtIn, boolean includeArchived, PageQuery pageQuery) {
        Specification<DocumentTypeJpaEntity> spec =
                buildSpec(keyword, organizationId, workspaceId, documentScope, status, builtIn, includeArchived);
        Pageable pageable = toPageable(pageQuery);
        Page<DocumentType> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<DocumentTypeJpaEntity> buildSpec(String keyword, UUID organizationId, UUID workspaceId,
                                                           DocumentTypeScope documentScope,
                                                           DocumentTypeStatus status, Boolean builtIn,
                                                           boolean includeArchived) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("name")), like)
                ));
            }
            if (organizationId != null) {
                predicates.add(cb.equal(root.get("organizationId"), organizationId));
            }
            if (workspaceId != null) {
                // Include SYSTEM types plus workspace-scoped types for this workspace
                predicates.add(cb.or(
                        cb.equal(root.get("documentScope"), DocumentTypeScope.SYSTEM.name()),
                        cb.equal(root.get("workspaceId"), workspaceId)
                ));
            }
            if (documentScope != null) {
                predicates.add(cb.equal(root.get("documentScope"), documentScope.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (builtIn != null) {
                predicates.add(cb.equal(root.get("builtIn"), builtIn));
            }
            if (!includeArchived) {
                predicates.add(cb.isNull(root.get("archivedAt")));
                predicates.add(cb.isNull(root.get("deletedAt")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
