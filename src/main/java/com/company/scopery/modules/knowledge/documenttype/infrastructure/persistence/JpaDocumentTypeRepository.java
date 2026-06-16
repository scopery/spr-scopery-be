package com.company.scopery.modules.knowledge.documenttype.infrastructure.persistence;

import com.company.scopery.modules.knowledge.documenttype.domain.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.documenttype.infrastructure.mapper.DocumentTypePersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public boolean existsByCodeAndWorkspaceId(DocumentTypeCode code, UUID workspaceId) {
        return springDataRepository.existsByCodeAndWorkspaceId(code.value(), workspaceId);
    }

    @Override
    public Page<DocumentType> findAll(String keyword, UUID workspaceId, DocumentTypeScope documentScope,
                                       DocumentTypeStatus status, boolean includeDeleted, Pageable pageable) {
        Specification<DocumentTypeJpaEntity> spec =
                buildSpec(keyword, workspaceId, documentScope, status, includeDeleted);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<DocumentTypeJpaEntity> buildSpec(String keyword, UUID workspaceId,
                                                            DocumentTypeScope documentScope,
                                                            DocumentTypeStatus status,
                                                            boolean includeDeleted) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("name")), like)
                ));
            }
            if (workspaceId != null) {
                predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            }
            if (documentScope != null) {
                predicates.add(cb.equal(root.get("documentScope"), documentScope.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (!includeDeleted) {
                predicates.add(cb.isNull(root.get("deletedAt")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
