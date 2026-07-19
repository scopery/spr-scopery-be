package com.company.scopery.modules.iam.auditevent.application.service;

import com.company.scopery.common.audit.ImmutableAuditEventJpaEntity;
import com.company.scopery.common.audit.ImmutableAuditEventJpaRepository;
import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.iam.auditevent.application.response.AuditEventResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IamAuditEventQueryService {

    private final ImmutableAuditEventJpaRepository repository;

    public IamAuditEventQueryService(ImmutableAuditEventJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditEventResponse> search(String eventType, String severity,
                                                    UUID actorId, String resourceType,
                                                    UUID organizationId, UUID workspaceId,
                                                    int page, int size) {
        Specification<ImmutableAuditEventJpaEntity> spec = buildSpec(
                eventType, severity, actorId, resourceType, organizationId, workspaceId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"));
        return PageResponse.from(repository.findAll(spec, pageable).map(AuditEventResponse::from));
    }

    private Specification<ImmutableAuditEventJpaEntity> buildSpec(
            String eventType, String severity, UUID actorId,
            String resourceType, UUID organizationId, UUID workspaceId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (eventType != null && !eventType.isBlank()) {
                predicates.add(cb.equal(root.get("eventType"), eventType.trim().toUpperCase()));
            }
            if (severity != null && !severity.isBlank()) {
                predicates.add(cb.equal(root.get("severity"), severity.trim().toUpperCase()));
            }
            if (actorId != null) {
                predicates.add(cb.equal(root.get("actorId"), actorId));
            }
            if (resourceType != null && !resourceType.isBlank()) {
                predicates.add(cb.equal(root.get("resourceType"), resourceType.trim().toUpperCase()));
            }
            if (organizationId != null) {
                predicates.add(cb.equal(root.get("organizationId"), organizationId));
            }
            if (workspaceId != null) {
                predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
