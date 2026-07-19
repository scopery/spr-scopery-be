#!/usr/bin/env python3
"""Generate Phase 26 entity submodules with full Action/Query/Controller stacks."""
from pathlib import Path
from textwrap import dedent

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/company/scopery/modules/quality"
TEST = ROOT / "src/test/java/com/company/scopery/modules/quality"
PKG = "com.company.scopery.modules.quality"

def w(rel, content, test=False):
    base = TEST if test else JAVA
    p = base / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(dedent(content).lstrip() if content.startswith(" ") or content.startswith("\n") else content.rstrip() + "\n")
    # fix: always write cleaned
    p.write_text(content.strip() + "\n")

# ═══════════════════════════════════════════════════════════════════════════
# Generic helper: full CRUD entity stack for project-scoped entities
# ═══════════════════════════════════════════════════════════════════════════

def gen_crud(
    subpkg, Entity, table_const, entity_type_const,
    # domain field list as strings for record
    domain_fields,  # list of "Type name"
    jpa_fields_code,  # body of JPA entity fields
    enum_defs,  # dict name -> list values
    create_factory,  # java method body inside record
    extra_domain_methods="",
    mapper_to_domain="",  # full method bodies if custom; else auto
    mapper_to_jpa="",
    response_fields="",  # record fields
    response_from="",
    create_cmd_fields="",
    create_req_fields="",
    create_action_body="",
    update_action=None,  # optional
    lifecycle_actions=None,  # list of (ActionName, path_suffix, http_method, cmd_fields, body)
    auth_view="requireQualityView",
    auth_create="requireQualityCreate",
    auth_update="requireQualityUpdate",
    activity_create="QUALITY_PLAN_CREATED",
    controller_tag="Quality",
    api_path_const="QUALITY_PLANS",
    nest_under=None,  # e.g. ("testPlanId", "suites") for nested routes - handled manually
    spring_extra="",
    list_filter=None,
):
    enums = enum_defs or {}
    for ename, values in enums.items():
        w(f"{subpkg}/domain/enums/{ename}.java", f"""package {PKG}.{subpkg}.domain.enums;
public enum {ename} {{
    {', '.join(values)}
}}
""")

    enum_imports = "\n".join(f"import {PKG}.{subpkg}.domain.enums.{e};" for e in enums)
    fields_joined = ",\n        ".join(domain_fields)
    w(f"{subpkg}/domain/model/{Entity}.java", f"""package {PKG}.{subpkg}.domain.model;
{enum_imports}
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record {Entity}(
        {fields_joined}
) {{
{create_factory}
{extra_domain_methods}
}}
""")

    w(f"{subpkg}/domain/model/{Entity}Repository.java", f"""package {PKG}.{subpkg}.domain.model;
import java.util.*;
public interface {Entity}Repository {{
    {Entity} save({Entity} entity);
    Optional<{Entity}> findByIdAndProjectId(UUID id, UUID projectId);
    List<{Entity}> findByProjectId(UUID projectId);
}}
""")

    w(f"{subpkg}/infrastructure/persistence/{Entity}JpaEntity.java", f"""package {PKG}.{subpkg}.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import {PKG}.shared.constant.QualityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = QualityTableNames.{table_const}) @Getter @Setter @NoArgsConstructor
public class {Entity}JpaEntity extends AuditableJpaEntity {{
{jpa_fields_code}
    @Version private Integer version;
}}
""")

    w(f"{subpkg}/infrastructure/persistence/SpringData{Entity}JpaRepository.java", f"""package {PKG}.{subpkg}.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringData{Entity}JpaRepository extends JpaRepository<{Entity}JpaEntity, UUID> {{
    Optional<{Entity}JpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<{Entity}JpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
{spring_extra}
}}
""")

    w(f"{subpkg}/infrastructure/persistence/Jpa{Entity}Repository.java", f"""package {PKG}.{subpkg}.infrastructure.persistence;
import {PKG}.{subpkg}.domain.model.{Entity};
import {PKG}.{subpkg}.domain.model.{Entity}Repository;
import {PKG}.{subpkg}.infrastructure.mapper.{Entity}PersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class Jpa{Entity}Repository implements {Entity}Repository {{
    private final SpringData{Entity}JpaRepository springData;
    private final {Entity}PersistenceMapper mapper;
    public Jpa{Entity}Repository(SpringData{Entity}JpaRepository springData, {Entity}PersistenceMapper mapper) {{
        this.springData = springData; this.mapper = mapper;
    }}
    @Override public {Entity} save({Entity} e) {{ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }}
    @Override public Optional<{Entity}> findByIdAndProjectId(UUID id, UUID projectId) {{
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }}
    @Override public List<{Entity}> findByProjectId(UUID projectId) {{
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }}
}}
""")

    w(f"{subpkg}/infrastructure/mapper/{Entity}PersistenceMapper.java", f"""package {PKG}.{subpkg}.infrastructure.mapper;
{enum_imports}
import {PKG}.{subpkg}.domain.model.{Entity};
import {PKG}.{subpkg}.infrastructure.persistence.{Entity}JpaEntity;
import org.springframework.stereotype.Component;
@Component
public class {Entity}PersistenceMapper {{
{mapper_to_domain}
{mapper_to_jpa}
}}
""")

    w(f"{subpkg}/application/response/{Entity}Response.java", f"""package {PKG}.{subpkg}.application.response;
import {PKG}.{subpkg}.domain.model.{Entity};
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record {Entity}Response({response_fields}) {{
    public static {Entity}Response from({Entity} e) {{
        return {response_from};
    }}
}}
""")

    w(f"{subpkg}/application/command/Create{Entity}Command.java", f"""package {PKG}.{subpkg}.application.command;
import java.util.UUID;
public record Create{Entity}Command({create_cmd_fields}) {{}}
""")

    w(f"{subpkg}/http/request/Create{Entity}Request.java", f"""package {PKG}.{subpkg}.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record Create{Entity}Request({create_req_fields}) {{}}
""")

    # Create action
    w(f"{subpkg}/application/action/Create{Entity}Action.java", f"""package {PKG}.{subpkg}.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import {PKG}.{subpkg}.application.command.Create{Entity}Command;
import {PKG}.{subpkg}.application.response.{Entity}Response;
import {PKG}.{subpkg}.domain.model.{Entity};
import {PKG}.{subpkg}.domain.model.{Entity}Repository;
import {PKG}.shared.activity.QualityActivityLogger;
import {PKG}.shared.authorization.QualityAuthorizationService;
import {PKG}.shared.constant.QualityActivityActions;
import {PKG}.shared.constant.QualityEntityTypes;
import {PKG}.shared.error.QualityExceptions;
import {PKG}.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class Create{Entity}Action {{
    private final ProjectRepository projects;
    private final {Entity}Repository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public Create{Entity}Action(ProjectRepository projects, {Entity}Repository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {{
        this.projects = projects; this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }}
    @Transactional
    public {Entity}Response execute(Create{Entity}Command command) {{
{create_action_body}
    }}
}}
""")

    # Query service
    not_found_method = {
        "QualityPlan": "qualityPlanNotFound",
        "TestPlan": "testPlanNotFound",
        "TestSuite": "testSuiteNotFound",
        "TestCase": "testCaseNotFound",
        "TestRun": "testRunNotFound",
        "Defect": "defectNotFound",
        "ReleasePackage": "releaseNotFound",
        "DeploymentEnvironment": "deploymentEnvNotFound",
        "DeploymentRecord": "deploymentNotFound",
        "RollbackPlan": "rollbackPlanNotFound",
        "TestCaseResult": "testResultNotFound",
        "TestStep": "testStepNotFound",
        "DefectLink": "defectNotFound",
        "ReleaseItem": "releaseNotFound",
    }.get(Entity, "qualityPlanNotFound")

    w(f"{subpkg}/application/service/{Entity}QueryService.java", f"""package {PKG}.{subpkg}.application.service;
import {PKG}.{subpkg}.application.response.{Entity}Response;
import {PKG}.{subpkg}.domain.model.{Entity}Repository;
import {PKG}.shared.authorization.QualityAuthorizationService;
import {PKG}.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class {Entity}QueryService {{
    private final {Entity}Repository repo;
    private final QualityAuthorizationService authorization;
    public {Entity}QueryService({Entity}Repository repo, QualityAuthorizationService authorization) {{
        this.repo = repo; this.authorization = authorization;
    }}
    @Transactional(readOnly = true)
    public List<{Entity}Response> list(UUID projectId) {{
        authorization.{auth_view}(projectId);
        return repo.findByProjectId(projectId).stream().map({Entity}Response::from).toList();
    }}
    @Transactional(readOnly = true)
    public {Entity}Response get(UUID projectId, UUID id) {{
        authorization.{auth_view}(projectId);
        return repo.findByIdAndProjectId(id, projectId).map({Entity}Response::from)
                .orElseThrow(() -> QualityExceptions.{not_found_method}(id));
    }}
}}
""")

    print(f"Generated stack: {Entity}")


# ── QualityPlan ────────────────────────────────────────────────────────────
gen_crud(
    "qualityplan", "QualityPlan", "QUALITY_PLAN", "QUALITY_PLAN",
    domain_fields=[
        "UUID id", "UUID projectId", "UUID workspaceId", "UUID sourceBaselineId", "String code",
        "String name", "String description", "QualityPlanStatus status", "boolean currentFlag",
        "String qualityObjectives", "String testStrategy", "String entryCriteria", "String exitCriteria",
        "String defectPolicyJson", "String releaseReadinessPolicyJson",
        "Instant approvedAt", "UUID approvedBy", "Instant archivedAt", "UUID archivedBy",
        "String traceId", "int version", "Instant createdAt", "Instant updatedAt"
    ],
    jpa_fields_code="""    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="source_baseline_id") private UUID sourceBaselineId;
    private String code;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String status;
    @Column(name="current_flag", nullable=false) private boolean currentFlag;
    @Column(name="quality_objectives", columnDefinition="text") private String qualityObjectives;
    @Column(name="test_strategy", columnDefinition="text") private String testStrategy;
    @Column(name="entry_criteria", columnDefinition="text") private String entryCriteria;
    @Column(name="exit_criteria", columnDefinition="text") private String exitCriteria;
    @Column(name="defect_policy_json", columnDefinition="text") private String defectPolicyJson;
    @Column(name="release_readiness_policy_json", columnDefinition="text") private String releaseReadinessPolicyJson;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="approved_by") private UUID approvedBy;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId;""",
    enum_defs={"QualityPlanStatus": ["DRAFT", "READY", "APPROVED", "CURRENT", "ARCHIVED"]},
    create_factory="""    public static QualityPlan create(UUID projectId, UUID workspaceId, String code, String name, String description,
            String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria) {
        Instant now = Instant.now();
        return new QualityPlan(UUID.randomUUID(), projectId, workspaceId, null, code, name, description,
                QualityPlanStatus.DRAFT, false, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                null, null, null, null, null, null, null, 0, now, now);
    }""",
    extra_domain_methods="""    public QualityPlan approve(UUID actorId) {
        if (status != QualityPlanStatus.DRAFT && status != QualityPlanStatus.READY)
            throw new IllegalStateException("Cannot approve from " + status);
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                QualityPlanStatus.APPROVED, currentFlag, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, Instant.now(), actorId, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan markCurrent() {
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                QualityPlanStatus.CURRENT, true, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan clearCurrent() {
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                status == QualityPlanStatus.CURRENT ? QualityPlanStatus.APPROVED : status, false,
                qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan archive(UUID actorId) {
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                QualityPlanStatus.ARCHIVED, false, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, approvedAt, approvedBy, Instant.now(), actorId, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan update(String name, String description, String qualityObjectives, String testStrategy,
                              String entryCriteria, String exitCriteria) {
        if (status != QualityPlanStatus.DRAFT && status != QualityPlanStatus.READY)
            throw new IllegalStateException("Immutable");
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description, status, currentFlag,
                qualityObjectives, testStrategy, entryCriteria, exitCriteria, defectPolicyJson, releaseReadinessPolicyJson,
                approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }""",
    mapper_to_domain="""    public QualityPlan toDomain(QualityPlanJpaEntity e) {
        return new QualityPlan(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getSourceBaselineId(), e.getCode(),
                e.getName(), e.getDescription(), QualityPlanStatus.valueOf(e.getStatus()), e.isCurrentFlag(),
                e.getQualityObjectives(), e.getTestStrategy(), e.getEntryCriteria(), e.getExitCriteria(),
                e.getDefectPolicyJson(), e.getReleaseReadinessPolicyJson(), e.getApprovedAt(), e.getApprovedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(), e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }""",
    mapper_to_jpa="""    public QualityPlanJpaEntity toJpaEntity(QualityPlan d) {
        QualityPlanJpaEntity e = new QualityPlanJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setSourceBaselineId(d.sourceBaselineId()); e.setCode(d.code()); e.setName(d.name());
        e.setDescription(d.description()); e.setStatus(d.status().name()); e.setCurrentFlag(d.currentFlag());
        e.setQualityObjectives(d.qualityObjectives()); e.setTestStrategy(d.testStrategy());
        e.setEntryCriteria(d.entryCriteria()); e.setExitCriteria(d.exitCriteria());
        e.setDefectPolicyJson(d.defectPolicyJson()); e.setReleaseReadinessPolicyJson(d.releaseReadinessPolicyJson());
        e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy()); e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }""",
    response_fields="UUID id, UUID projectId, String code, String name, String status, boolean currentFlag, Instant createdAt",
    response_from="new QualityPlanResponse(e.id(), e.projectId(), e.code(), e.name(), e.status().name(), e.currentFlag(), e.createdAt())",
    create_cmd_fields="UUID projectId, String code, String name, String description, String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria",
    create_req_fields="@NotBlank String name, String code, String description, String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria",
    create_action_body="""        authorization.requireQualityCreate(command.projectId());
        Project project = projects.findById(command.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(command.projectId());
        if (command.name() == null || command.name().isBlank()) throw QualityExceptions.nameRequired();
        QualityPlan saved = repo.save(QualityPlan.create(project.id(), project.workspaceId(), command.code(),
                command.name().trim(), command.description(), command.qualityObjectives(), command.testStrategy(),
                command.entryCriteria(), command.exitCriteria()));
        activityLogger.logSuccess(QualityEntityTypes.QUALITY_PLAN, saved.id(), QualityActivityActions.QUALITY_PLAN_CREATED, "Quality plan created");
        return QualityPlanResponse.from(saved);""",
    auth_view="requireQualityView", auth_create="requireQualityCreate",
)

print("QualityPlan stack OK")
print("PHASE26_ENTITIES_PART1_DONE")
