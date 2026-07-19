#!/usr/bin/env python3
"""Generate Phase 24 scope-item + criteria HTTP layers."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def W(rel: str, content: str) -> None:
    p = ROOT / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(content.strip() + "\n", encoding="utf-8")
    print("W", rel)


def main() -> None:
    # Update ScopeApiPaths
    W(
        "src/main/java/com/company/scopery/modules/scope/shared/constant/ScopeApiPaths.java",
        """
package com.company.scopery.modules.scope.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class ScopeApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String PACKAGES = BASE + "/scope-packages";
    public static final String ITEMS = BASE + "/scope-items";
    public static final String DELIVERABLES = BASE + "/deliverables";
    public static final String CRITERIA = BASE + "/acceptance-criteria";
    public static final String REPORTS = BASE + "/reports";
    private ScopeApiPaths() {}
}
""",
    )

    # Scope item enums + domain
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/domain/enums/ScopeItemStatus.java",
        """
package com.company.scopery.modules.scope.scopeitem.domain.enums;
public enum ScopeItemStatus { DRAFT, ACTIVE, APPROVED, CHANGED, ARCHIVED }
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/domain/enums/ScopeItemType.java",
        """
package com.company.scopery.modules.scope.scopeitem.domain.enums;
public enum ScopeItemType { FEATURE, WORKSTREAM, DELIVERABLE_GROUP, CONSTRAINT, ASSUMPTION, EXCLUSION, OTHER }
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/domain/model/ScopeItem.java",
        """
package com.company.scopery.modules.scope.scopeitem.domain.model;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemStatus;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import java.time.Instant; import java.util.UUID;
public record ScopeItem(UUID id, UUID scopePackageId, UUID projectId, UUID workspaceId, UUID sourceQuoteLineId,
        UUID sourceChangeRequestId, UUID parentScopeItemId, ScopeItemType type, String code, String title,
        String description, boolean inScope, boolean outOfScope, String priority, boolean acceptanceRequired,
        ScopeItemStatus status, Integer sortOrder, Instant archivedAt, UUID archivedBy, int version,
        Instant createdAt, Instant updatedAt) {
    public static ScopeItem create(UUID scopePackageId, UUID projectId, UUID workspaceId, ScopeItemType type,
                                   String code, String title, String description, boolean inScope, boolean outOfScope,
                                   String priority, boolean acceptanceRequired, Integer sortOrder) {
        if (inScope && outOfScope) throw new IllegalArgumentException("Cannot be both in-scope and out-of-scope");
        Instant now = Instant.now();
        return new ScopeItem(UUID.randomUUID(), scopePackageId, projectId, workspaceId, null, null, null, type, code,
                title, description, inScope, outOfScope, priority, acceptanceRequired, ScopeItemStatus.DRAFT, sortOrder,
                null, null, 0, now, now);
    }
    public ScopeItem update(String title, String description, boolean inScope, boolean outOfScope, String priority,
                            boolean acceptanceRequired, Integer sortOrder) {
        if (inScope && outOfScope) throw new IllegalArgumentException("Cannot be both in-scope and out-of-scope");
        return new ScopeItem(id, scopePackageId, projectId, workspaceId, sourceQuoteLineId, sourceChangeRequestId,
                parentScopeItemId, type, code, title, description, inScope, outOfScope, priority, acceptanceRequired,
                status, sortOrder, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public ScopeItem archive(UUID actorId) {
        return new ScopeItem(id, scopePackageId, projectId, workspaceId, sourceQuoteLineId, sourceChangeRequestId,
                parentScopeItemId, type, code, title, description, inScope, outOfScope, priority, acceptanceRequired,
                ScopeItemStatus.ARCHIVED, sortOrder, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/domain/model/ScopeItemRepository.java",
        """
package com.company.scopery.modules.scope.scopeitem.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ScopeItemRepository {
    ScopeItem save(ScopeItem item);
    Optional<ScopeItem> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopeItem> findByScopePackageId(UUID scopePackageId);
    List<ScopeItem> findByProjectId(UUID projectId);
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/infrastructure/persistence/ScopeItemJpaEntity.java",
        """
package com.company.scopery.modules.scope.scopeitem.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter; lombok.NoArgsConstructor; lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name=ScopeTableNames.ITEM) @Getter @Setter @NoArgsConstructor
public class ScopeItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="scope_package_id", nullable=false) private UUID scopePackageId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="source_quote_line_id") private UUID sourceQuoteLineId;
    @Column(name="source_change_request_id") private UUID sourceChangeRequestId;
    @Column(name="parent_scope_item_id") private UUID parentScopeItemId;
    @Column(nullable=false) private String type;
    private String code;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="in_scope", nullable=false) private boolean inScope;
    @Column(name="out_of_scope", nullable=false) private boolean outOfScope;
    private String priority;
    @Column(name="acceptance_required", nullable=false) private boolean acceptanceRequired;
    @Column(nullable=false) private String status;
    @Column(name="sort_order") private Integer sortOrder;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/infrastructure/mapper/ScopeItemPersistenceMapper.java",
        """
package com.company.scopery.modules.scope.scopeitem.infrastructure.mapper;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemStatus;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.infrastructure.persistence.ScopeItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ScopeItemPersistenceMapper {
    public ScopeItem toDomain(ScopeItemJpaEntity e) {
        return new ScopeItem(e.getId(), e.getScopePackageId(), e.getProjectId(), e.getWorkspaceId(), e.getSourceQuoteLineId(),
                e.getSourceChangeRequestId(), e.getParentScopeItemId(), ScopeItemType.valueOf(e.getType()), e.getCode(),
                e.getTitle(), e.getDescription(), e.isInScope(), e.isOutOfScope(), e.getPriority(), e.isAcceptanceRequired(),
                ScopeItemStatus.valueOf(e.getStatus()), e.getSortOrder(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ScopeItemJpaEntity toJpaEntity(ScopeItem d) {
        ScopeItemJpaEntity e = new ScopeItemJpaEntity();
        e.setId(d.id()); e.setScopePackageId(d.scopePackageId()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setSourceQuoteLineId(d.sourceQuoteLineId()); e.setSourceChangeRequestId(d.sourceChangeRequestId());
        e.setParentScopeItemId(d.parentScopeItemId()); e.setType(d.type().name()); e.setCode(d.code()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setInScope(d.inScope()); e.setOutOfScope(d.outOfScope()); e.setPriority(d.priority());
        e.setAcceptanceRequired(d.acceptanceRequired()); e.setStatus(d.status().name()); e.setSortOrder(d.sortOrder());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/infrastructure/persistence/SpringDataScopeItemJpaRepository.java",
        """
package com.company.scopery.modules.scope.scopeitem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataScopeItemJpaRepository extends JpaRepository<ScopeItemJpaEntity, UUID> {
    Optional<ScopeItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopeItemJpaEntity> findByScopePackageIdOrderBySortOrderAscCreatedAtAsc(UUID scopePackageId);
    List<ScopeItemJpaEntity> findByProjectIdOrderByCreatedAtAsc(UUID projectId);
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/infrastructure/persistence/JpaScopeItemRepository.java",
        """
package com.company.scopery.modules.scope.scopeitem.infrastructure.persistence;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopeitem.infrastructure.mapper.ScopeItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaScopeItemRepository implements ScopeItemRepository {
    private final SpringDataScopeItemJpaRepository springData; private final ScopeItemPersistenceMapper mapper;
    public JpaScopeItemRepository(SpringDataScopeItemJpaRepository springData, ScopeItemPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ScopeItem save(ScopeItem item){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item))); }
    @Override public Optional<ScopeItem> findByIdAndProjectId(UUID id, UUID projectId){ return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<ScopeItem> findByScopePackageId(UUID scopePackageId){ return springData.findByScopePackageIdOrderBySortOrderAscCreatedAtAsc(scopePackageId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ScopeItem> findByProjectId(UUID projectId){ return springData.findByProjectIdOrderByCreatedAtAsc(projectId).stream().map(mapper::toDomain).toList(); }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/application/response/ScopeItemResponse.java",
        """
package com.company.scopery.modules.scope.scopeitem.application.response;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import java.time.Instant; import java.util.UUID;
public record ScopeItemResponse(UUID id, UUID scopePackageId, UUID projectId, String type, String code, String title,
        String description, boolean inScope, boolean outOfScope, String priority, boolean acceptanceRequired,
        String status, Integer sortOrder, Instant createdAt, Instant updatedAt) {
    public static ScopeItemResponse from(ScopeItem i) {
        return new ScopeItemResponse(i.id(), i.scopePackageId(), i.projectId(), i.type().name(), i.code(), i.title(),
                i.description(), i.inScope(), i.outOfScope(), i.priority(), i.acceptanceRequired(), i.status().name(),
                i.sortOrder(), i.createdAt(), i.updatedAt());
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/application/action/CreateScopeItemAction.java",
        """
package com.company.scopery.modules.scope.scopeitem.application.action;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.shared.util.ScopeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class CreateScopeItemAction {
    private final ScopePackageRepository packages; private final ScopeItemRepository items;
    private final ScopeAuthorizationService authorization; private final ScopeActivityLogger activityLogger;
    public CreateScopeItemAction(ScopePackageRepository packages, ScopeItemRepository items,
                                 ScopeAuthorizationService authorization, ScopeActivityLogger activityLogger) {
        this.packages=packages; this.items=items; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ScopeItemResponse execute(UUID projectId, UUID packageId, Map<String, Object> body) {
        authorization.requireScopeCreate(projectId);
        var pkg = packages.findByIdAndProjectId(packageId, projectId).orElseThrow(() -> ScopeExceptions.packageNotFound(packageId));
        if (!pkg.isEditable()) throw ScopeExceptions.packageImmutable(packageId);
        String title = body.get("title") == null ? null : String.valueOf(body.get("title"));
        if (title == null || title.isBlank()) throw ScopeExceptions.itemNotFound(null);
        ScopeItemType type = ScopeEnumParser.parseRequired(ScopeItemType.class, String.valueOf(body.get("type")), "type");
        boolean inScope = body.get("inScope") == null || Boolean.parseBoolean(String.valueOf(body.get("inScope")));
        boolean outOfScope = body.get("outOfScope") != null && Boolean.parseBoolean(String.valueOf(body.get("outOfScope")));
        try {
            ScopeItem item = ScopeItem.create(pkg.id(), projectId, pkg.workspaceId(), type,
                    body.get("code")==null?null:String.valueOf(body.get("code")), title.trim(),
                    body.get("description")==null?null:String.valueOf(body.get("description")),
                    inScope, outOfScope,
                    body.get("priority")==null?null:String.valueOf(body.get("priority")),
                    body.get("acceptanceRequired")==null || Boolean.parseBoolean(String.valueOf(body.get("acceptanceRequired"))),
                    body.get("sortOrder")==null?null:Integer.valueOf(String.valueOf(body.get("sortOrder"))));
            item = items.save(item);
            activityLogger.logSuccess(ScopeEntityTypes.ITEM, item.id(), ScopeActivityActions.ITEM_CREATED, "Scope item created");
            return ScopeItemResponse.from(item);
        } catch (IllegalArgumentException ex) {
            throw ScopeExceptions.invalidFlags();
        }
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/application/action/UpdateScopeItemAction.java",
        """
package com.company.scopery.modules.scope.scopeitem.application.action;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class UpdateScopeItemAction {
    private final ScopeItemRepository items; private final ScopePackageRepository packages; private final ScopeAuthorizationService authorization;
    public UpdateScopeItemAction(ScopeItemRepository items, ScopePackageRepository packages, ScopeAuthorizationService authorization) {
        this.items=items; this.packages=packages; this.authorization=authorization;
    }
    @Transactional
    public ScopeItemResponse execute(UUID projectId, UUID itemId, Map<String, Object> body) {
        authorization.requireScopeUpdate(projectId);
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> ScopeExceptions.itemNotFound(itemId));
        var pkg = packages.findByIdAndProjectId(item.scopePackageId(), projectId).orElseThrow(() -> ScopeExceptions.packageNotFound(item.scopePackageId()));
        if (!pkg.isEditable()) throw ScopeExceptions.packageImmutable(pkg.id());
        boolean inScope = body.containsKey("inScope") ? Boolean.parseBoolean(String.valueOf(body.get("inScope"))) : item.inScope();
        boolean outOfScope = body.containsKey("outOfScope") ? Boolean.parseBoolean(String.valueOf(body.get("outOfScope"))) : item.outOfScope();
        try {
            item = items.save(item.update(
                    body.containsKey("title") ? String.valueOf(body.get("title")) : item.title(),
                    body.containsKey("description") ? (body.get("description")==null?null:String.valueOf(body.get("description"))) : item.description(),
                    inScope, outOfScope,
                    body.containsKey("priority") ? (body.get("priority")==null?null:String.valueOf(body.get("priority"))) : item.priority(),
                    body.containsKey("acceptanceRequired") ? Boolean.parseBoolean(String.valueOf(body.get("acceptanceRequired"))) : item.acceptanceRequired(),
                    body.containsKey("sortOrder") ? (body.get("sortOrder")==null?null:Integer.valueOf(String.valueOf(body.get("sortOrder")))) : item.sortOrder()));
            return ScopeItemResponse.from(item);
        } catch (IllegalArgumentException ex) {
            throw ScopeExceptions.invalidFlags();
        }
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/application/action/ArchiveScopeItemAction.java",
        """
package com.company.scopery.modules.scope.scopeitem.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveScopeItemAction {
    private final ScopeItemRepository items; private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveScopeItemAction(ScopeItemRepository items, ScopeAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser) {
        this.items=items; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public ScopeItemResponse execute(UUID projectId, UUID itemId) {
        authorization.requireScopeUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> ScopeExceptions.itemNotFound(itemId));
        return ScopeItemResponse.from(items.save(item.archive(actor.id())));
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/application/service/ScopeItemQueryService.java",
        """
package com.company.scopery.modules.scope.scopeitem.application.service;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ScopeItemQueryService {
    private final ScopeItemRepository items; private final ScopePackageRepository packages; private final ScopeAuthorizationService authorization;
    public ScopeItemQueryService(ScopeItemRepository items, ScopePackageRepository packages, ScopeAuthorizationService authorization) {
        this.items=items; this.packages=packages; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ScopeItemResponse> listByPackage(UUID projectId, UUID packageId) {
        authorization.requireScopeView(projectId);
        packages.findByIdAndProjectId(packageId, projectId).orElseThrow(() -> ScopeExceptions.packageNotFound(packageId));
        return items.findByScopePackageId(packageId).stream().map(ScopeItemResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public ScopeItemResponse get(UUID projectId, UUID itemId) {
        authorization.requireScopeView(projectId);
        return items.findByIdAndProjectId(itemId, projectId).map(ScopeItemResponse::from).orElseThrow(() -> ScopeExceptions.itemNotFound(itemId));
    }
}
""",
    )

    # Controllers for items under package + standalone
    W(
        "src/main/java/com/company/scopery/modules/scope/scopeitem/http/controller/ScopeItemController.java",
        """
package com.company.scopery.modules.scope.scopeitem.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.scopeitem.application.action.ArchiveScopeItemAction;
import com.company.scopery.modules.scope.scopeitem.application.action.CreateScopeItemAction;
import com.company.scopery.modules.scope.scopeitem.application.action.UpdateScopeItemAction;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.application.service.ScopeItemQueryService;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @Tag(name="Scope - Items")
public class ScopeItemController {
    private final CreateScopeItemAction create; private final UpdateScopeItemAction update;
    private final ArchiveScopeItemAction archive; private final ScopeItemQueryService query;
    public ScopeItemController(CreateScopeItemAction create, UpdateScopeItemAction update,
                               ArchiveScopeItemAction archive, ScopeItemQueryService query) {
        this.create=create; this.update=update; this.archive=archive; this.query=query;
    }
    @PostMapping(ScopeApiPaths.PACKAGES + "/{packageId}/items") @Operation(summary="Create scope item")
    public ApiResponse<ScopeItemResponse> create(@PathVariable UUID projectId, @PathVariable UUID packageId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(create.execute(projectId, packageId, body));
    }
    @GetMapping(ScopeApiPaths.PACKAGES + "/{packageId}/items") @Operation(summary="List package scope items")
    public ApiResponse<List<ScopeItemResponse>> list(@PathVariable UUID projectId, @PathVariable UUID packageId) {
        return ApiResponse.success(query.listByPackage(projectId, packageId));
    }
    @GetMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}") @Operation(summary="Get scope item")
    public ApiResponse<ScopeItemResponse> get(@PathVariable UUID projectId, @PathVariable UUID scopeItemId) {
        return ApiResponse.success(query.get(projectId, scopeItemId));
    }
    @PutMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}") @Operation(summary="Update scope item")
    public ApiResponse<ScopeItemResponse> update(@PathVariable UUID projectId, @PathVariable UUID scopeItemId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(update.execute(projectId, scopeItemId, body));
    }
    @PatchMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}/archive") @Operation(summary="Archive scope item")
    public ApiResponse<ScopeItemResponse> archive(@PathVariable UUID projectId, @PathVariable UUID scopeItemId) {
        return ApiResponse.success(archive.execute(projectId, scopeItemId));
    }
}
""",
    )

    # Criteria application + controller
    W(
        "src/main/java/com/company/scopery/modules/scope/criteria/application/response/AcceptanceCriteriaResponse.java",
        """
package com.company.scopery.modules.scope.criteria.application.response;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteria;
import java.time.Instant; import java.util.UUID;
public record AcceptanceCriteriaResponse(UUID id, UUID deliverableId, UUID projectId, String type, String title,
        String description, boolean mandatory, String status, Instant createdAt) {
    public static AcceptanceCriteriaResponse from(AcceptanceCriteria c) {
        return new AcceptanceCriteriaResponse(c.id(), c.deliverableId(), c.projectId(), c.type(), c.title(),
                c.description(), c.mandatory(), c.status().name(), c.createdAt());
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/criteria/application/action/CreateAcceptanceCriteriaAction.java",
        """
package com.company.scopery.modules.scope.criteria.application.action;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteria;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class CreateAcceptanceCriteriaAction {
    private final DeliverableRepository deliverables; private final AcceptanceCriteriaRepository criteria;
    private final ScopeAuthorizationService authorization;
    public CreateAcceptanceCriteriaAction(DeliverableRepository deliverables, AcceptanceCriteriaRepository criteria,
                                          ScopeAuthorizationService authorization) {
        this.deliverables=deliverables; this.criteria=criteria; this.authorization=authorization;
    }
    @Transactional
    public AcceptanceCriteriaResponse execute(UUID projectId, UUID deliverableId, Map<String, Object> body) {
        authorization.requireDeliverableUpdate(projectId);
        deliverables.findByIdAndProjectId(deliverableId, projectId).orElseThrow(() -> ScopeExceptions.deliverableNotFound(deliverableId));
        String title = body.get("title") == null ? null : String.valueOf(body.get("title"));
        if (title == null || title.isBlank()) throw ScopeExceptions.itemNotFound(null);
        String type = body.get("type") == null ? "FUNCTIONAL" : String.valueOf(body.get("type"));
        boolean mandatory = body.get("mandatory") == null || Boolean.parseBoolean(String.valueOf(body.get("mandatory")));
        AcceptanceCriteria c = criteria.save(AcceptanceCriteria.create(deliverableId, projectId, type, title.trim(),
                body.get("description")==null?null:String.valueOf(body.get("description")), mandatory));
        return AcceptanceCriteriaResponse.from(c);
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/criteria/application/action/SatisfyAcceptanceCriteriaAction.java",
        """
package com.company.scopery.modules.scope.criteria.application.action;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class SatisfyAcceptanceCriteriaAction {
    private final AcceptanceCriteriaRepository criteria; private final ScopeAuthorizationService authorization;
    public SatisfyAcceptanceCriteriaAction(AcceptanceCriteriaRepository criteria, ScopeAuthorizationService authorization) {
        this.criteria=criteria; this.authorization=authorization;
    }
    @Transactional
    public AcceptanceCriteriaResponse execute(UUID projectId, UUID criteriaId) {
        authorization.requireDeliverableUpdate(projectId);
        var c = criteria.findById(criteriaId).orElseThrow(() -> ScopeExceptions.itemNotFound(criteriaId));
        if (!c.projectId().equals(projectId)) throw ScopeExceptions.itemNotFound(criteriaId);
        return AcceptanceCriteriaResponse.from(criteria.save(c.satisfy()));
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/criteria/application/action/WaiveAcceptanceCriteriaAction.java",
        """
package com.company.scopery.modules.scope.criteria.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class WaiveAcceptanceCriteriaAction {
    private final AcceptanceCriteriaRepository criteria;
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    private final CurrentUserAuthorizationService currentUser;
    public WaiveAcceptanceCriteriaAction(AcceptanceCriteriaRepository criteria,
                                         ProjectWorkspaceAuthorizationService projectAuthorization,
                                         CurrentUserAuthorizationService currentUser) {
        this.criteria=criteria; this.projectAuthorization=projectAuthorization; this.currentUser=currentUser;
    }
    @Transactional
    public AcceptanceCriteriaResponse execute(UUID projectId, UUID criteriaId, Map<String, Object> body) {
        try { projectAuthorization.requireProjectPermission(projectId, IamAuthorities.ACCEPTANCE_CRITERIA_WAIVE); }
        catch (RuntimeException ex) { throw ScopeExceptions.accessDenied(); }
        String reason = body == null || body.get("reason") == null ? null : String.valueOf(body.get("reason"));
        if (reason == null || reason.isBlank()) throw ScopeExceptions.reopenReasonRequired();
        var actor = currentUser.resolveCurrentUser();
        var c = criteria.findById(criteriaId).orElseThrow(() -> ScopeExceptions.itemNotFound(criteriaId));
        if (!c.projectId().equals(projectId)) throw ScopeExceptions.itemNotFound(criteriaId);
        return AcceptanceCriteriaResponse.from(criteria.save(c.waive(actor.id(), reason.trim())));
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/criteria/application/service/AcceptanceCriteriaQueryService.java",
        """
package com.company.scopery.modules.scope.criteria.application.service;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AcceptanceCriteriaQueryService {
    private final AcceptanceCriteriaRepository criteria; private final DeliverableRepository deliverables;
    private final ScopeAuthorizationService authorization;
    public AcceptanceCriteriaQueryService(AcceptanceCriteriaRepository criteria, DeliverableRepository deliverables,
                                          ScopeAuthorizationService authorization) {
        this.criteria=criteria; this.deliverables=deliverables; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<AcceptanceCriteriaResponse> listByDeliverable(UUID projectId, UUID deliverableId) {
        authorization.requireDeliverableView(projectId);
        deliverables.findByIdAndProjectId(deliverableId, projectId).orElseThrow(() -> ScopeExceptions.deliverableNotFound(deliverableId));
        return criteria.findByDeliverableId(deliverableId).stream().map(AcceptanceCriteriaResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public AcceptanceCriteriaResponse get(UUID projectId, UUID criteriaId) {
        authorization.requireDeliverableView(projectId);
        var c = criteria.findById(criteriaId).orElseThrow(() -> ScopeExceptions.itemNotFound(criteriaId));
        if (!c.projectId().equals(projectId)) throw ScopeExceptions.itemNotFound(criteriaId);
        return AcceptanceCriteriaResponse.from(c);
    }
}
""",
    )
    W(
        "src/main/java/com/company/scopery/modules/scope/criteria/http/controller/AcceptanceCriteriaController.java",
        """
package com.company.scopery.modules.scope.criteria.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.criteria.application.action.CreateAcceptanceCriteriaAction;
import com.company.scopery.modules.scope.criteria.application.action.SatisfyAcceptanceCriteriaAction;
import com.company.scopery.modules.scope.criteria.application.action.WaiveAcceptanceCriteriaAction;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.application.service.AcceptanceCriteriaQueryService;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @Tag(name="Scope - Acceptance Criteria")
public class AcceptanceCriteriaController {
    private final CreateAcceptanceCriteriaAction create; private final SatisfyAcceptanceCriteriaAction satisfy;
    private final WaiveAcceptanceCriteriaAction waive; private final AcceptanceCriteriaQueryService query;
    public AcceptanceCriteriaController(CreateAcceptanceCriteriaAction create, SatisfyAcceptanceCriteriaAction satisfy,
                                        WaiveAcceptanceCriteriaAction waive, AcceptanceCriteriaQueryService query) {
        this.create=create; this.satisfy=satisfy; this.waive=waive; this.query=query;
    }
    @PostMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/acceptance-criteria") @Operation(summary="Create acceptance criteria")
    public ApiResponse<AcceptanceCriteriaResponse> create(@PathVariable UUID projectId, @PathVariable UUID deliverableId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(create.execute(projectId, deliverableId, body));
    }
    @GetMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/acceptance-criteria") @Operation(summary="List acceptance criteria")
    public ApiResponse<List<AcceptanceCriteriaResponse>> list(@PathVariable UUID projectId, @PathVariable UUID deliverableId) {
        return ApiResponse.success(query.listByDeliverable(projectId, deliverableId));
    }
    @GetMapping(ScopeApiPaths.CRITERIA + "/{criteriaId}") @Operation(summary="Get acceptance criteria")
    public ApiResponse<AcceptanceCriteriaResponse> get(@PathVariable UUID projectId, @PathVariable UUID criteriaId) {
        return ApiResponse.success(query.get(projectId, criteriaId));
    }
    @PatchMapping(ScopeApiPaths.CRITERIA + "/{criteriaId}/satisfy") @Operation(summary="Satisfy criteria")
    public ApiResponse<AcceptanceCriteriaResponse> satisfy(@PathVariable UUID projectId, @PathVariable UUID criteriaId) {
        return ApiResponse.success(satisfy.execute(projectId, criteriaId));
    }
    @PatchMapping(ScopeApiPaths.CRITERIA + "/{criteriaId}/waive") @Operation(summary="Waive criteria")
    public ApiResponse<AcceptanceCriteriaResponse> waive(@PathVariable UUID projectId, @PathVariable UUID criteriaId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(waive.execute(projectId, criteriaId, body));
    }
}
""",
    )

    # ScopeEnumParser if missing
    enum_parser = ROOT / "src/main/java/com/company/scopery/modules/scope/shared/util/ScopeEnumParser.java"
    if not enum_parser.exists():
        W(
            "src/main/java/com/company/scopery/modules/scope/shared/util/ScopeEnumParser.java",
            """
package com.company.scopery.modules.scope.shared.util;
import com.company.scopery.common.exception.ValidationException;
public final class ScopeEnumParser {
    private ScopeEnumParser() {}
    public static <E extends Enum<E>> E parseRequired(Class<E> type, String raw, String field) {
        if (raw == null || raw.isBlank()) throw new ValidationException("VALIDATION_ERROR", field + " is required");
        try { return Enum.valueOf(type, raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ValidationException("VALIDATION_ERROR", "Invalid " + field + ": " + raw); }
    }
}
""",
        )

    # Ensure activity actions / entity types
    print("Scope item/criteria generated")


if __name__ == "__main__":
    main()
