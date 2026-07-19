#!/usr/bin/env python3
"""Generate remaining Phase 31 collaboration layers (series, participant, agenda, minutes, note, action, link, comment, mention, report, events)."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/company/scopery/modules/collaboration"
TEST = ROOT / "src/test/java/com/company/scopery/modules/collaboration"
PKG = "com.company.scopery.modules.collaboration"

def w(rel, content, test=False):
    base = TEST if test else JAVA
    path = base / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.rstrip() + "\n")
    print(path.relative_to(ROOT))

# ========== MEETING SERIES ==========
w("meetingseries/infrastructure/persistence/MeetingSeriesJpaEntity.java", f"""package {PKG}.meetingseries.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import {PKG}.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.MEETING_SERIES) @Getter @Setter @NoArgsConstructor
public class MeetingSeriesJpaEntity extends AuditableJpaEntity {{
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    private String code;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    private String cadence;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(nullable=false) private String status;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}}
""")
w("meetingseries/infrastructure/persistence/SpringDataMeetingSeriesJpaRepository.java", f"""package {PKG}.meetingseries.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingSeriesJpaRepository extends JpaRepository<MeetingSeriesJpaEntity, UUID> {{
    Optional<MeetingSeriesJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingSeriesJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}}
""")
w("meetingseries/infrastructure/mapper/MeetingSeriesPersistenceMapper.java", f"""package {PKG}.meetingseries.infrastructure.mapper;
import {PKG}.meetingseries.domain.enums.*;
import {PKG}.meetingseries.domain.model.MeetingSeries;
import {PKG}.meetingseries.infrastructure.persistence.MeetingSeriesJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingSeriesPersistenceMapper {{
    public MeetingSeries toDomain(MeetingSeriesJpaEntity e) {{
        return new MeetingSeries(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getCode(), e.getTitle(), e.getDescription(),
                e.getCadence()==null?null:MeetingSeriesCadence.valueOf(e.getCadence()), e.getOwnerUserId(),
                MeetingSeriesStatus.valueOf(e.getStatus()), e.isClientVisible(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }}
    public MeetingSeriesJpaEntity toJpaEntity(MeetingSeries d) {{
        MeetingSeriesJpaEntity e = new MeetingSeriesJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setCode(d.code());
        e.setTitle(d.title()); e.setDescription(d.description());
        e.setCadence(d.cadence()==null?null:d.cadence().name()); e.setOwnerUserId(d.ownerUserId());
        e.setStatus(d.status().name()); e.setClientVisible(d.clientVisible());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }}
}}
""")
w("meetingseries/infrastructure/persistence/JpaMeetingSeriesRepository.java", f"""package {PKG}.meetingseries.infrastructure.persistence;
import {PKG}.meetingseries.domain.model.*;
import {PKG}.meetingseries.infrastructure.mapper.MeetingSeriesPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingSeriesRepository implements MeetingSeriesRepository {{
    private final SpringDataMeetingSeriesJpaRepository springData;
    private final MeetingSeriesPersistenceMapper mapper;
    public JpaMeetingSeriesRepository(SpringDataMeetingSeriesJpaRepository springData, MeetingSeriesPersistenceMapper mapper) {{
        this.springData=springData; this.mapper=mapper;
    }}
    @Override public MeetingSeries save(MeetingSeries s) {{ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s))); }}
    @Override public Optional<MeetingSeries> findByIdAndProjectId(UUID id, UUID projectId) {{
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }}
    @Override public List<MeetingSeries> findByProjectId(UUID projectId) {{
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }}
}}
""")
w("meetingseries/application/response/MeetingSeriesResponse.java", f"""package {PKG}.meetingseries.application.response;
import {PKG}.meetingseries.domain.model.MeetingSeries;
import java.time.Instant; import java.util.UUID;
public record MeetingSeriesResponse(UUID id, UUID workspaceId, UUID projectId, String code, String title, String description,
        String cadence, UUID ownerUserId, String status, boolean clientVisible, Instant archivedAt, Instant createdAt, Instant updatedAt, int version) {{
    public static MeetingSeriesResponse from(MeetingSeries s) {{
        return new MeetingSeriesResponse(s.id(), s.workspaceId(), s.projectId(), s.code(), s.title(), s.description(),
                s.cadence()==null?null:s.cadence().name(), s.ownerUserId(), s.status().name(), s.clientVisible(),
                s.archivedAt(), s.createdAt(), s.updatedAt(), s.version());
    }}
}}
""")
w("meetingseries/http/request/CreateMeetingSeriesRequest.java", f"""package {PKG}.meetingseries.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateMeetingSeriesRequest(String code, @NotBlank String title, String description, String cadence, UUID ownerUserId, Boolean clientVisible) {{}}
""")
w("meetingseries/http/request/UpdateMeetingSeriesRequest.java", f"""package {PKG}.meetingseries.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record UpdateMeetingSeriesRequest(@NotBlank String title, String description, String cadence, UUID ownerUserId, Boolean clientVisible) {{}}
""")
w("meetingseries/application/command/CreateMeetingSeriesCommand.java", f"""package {PKG}.meetingseries.application.command;
import java.util.UUID;
public record CreateMeetingSeriesCommand(UUID projectId, String code, String title, String description, String cadence, UUID ownerUserId, Boolean clientVisible) {{}}
""")
w("meetingseries/application/command/UpdateMeetingSeriesCommand.java", f"""package {PKG}.meetingseries.application.command;
import java.util.UUID;
public record UpdateMeetingSeriesCommand(UUID projectId, UUID seriesId, String title, String description, String cadence, UUID ownerUserId, Boolean clientVisible) {{}}
""")
w("meetingseries/application/action/CreateMeetingSeriesAction.java", f"""package {PKG}.meetingseries.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import {PKG}.meetingseries.application.command.CreateMeetingSeriesCommand;
import {PKG}.meetingseries.application.response.MeetingSeriesResponse;
import {PKG}.meetingseries.domain.enums.MeetingSeriesCadence;
import {PKG}.meetingseries.domain.model.*;
import {PKG}.shared.activity.CollaborationActivityLogger;
import {PKG}.shared.authorization.CollaborationAuthorizationService;
import {PKG}.shared.constant.*;
import {PKG}.shared.error.CollaborationExceptions;
import {PKG}.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateMeetingSeriesAction {{
    private final ProjectRepository projects; private final MeetingSeriesRepository series;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateMeetingSeriesAction(ProjectRepository projects, MeetingSeriesRepository series,
                                     CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {{
        this.projects=projects; this.series=series; this.authorization=authorization; this.activityLogger=activityLogger;
    }}
    @Transactional
    public MeetingSeriesResponse execute(CreateMeetingSeriesCommand command) {{
        authorization.requireSeriesManage(command.projectId());
        Project project = projects.findById(command.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw CollaborationExceptions.projectArchived(command.projectId());
        if (command.title() == null || command.title().isBlank()) throw CollaborationExceptions.titleRequired();
        MeetingSeriesCadence cadence = CollaborationEnumParser.parseOptional(MeetingSeriesCadence.class, command.cadence(), "cadence");
        MeetingSeries s = MeetingSeries.create(project.workspaceId(), project.id(), command.code(), command.title().trim(),
                command.description(), cadence, command.ownerUserId(), Boolean.TRUE.equals(command.clientVisible()));
        s = series.save(s);
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_CREATED, "Series created");
        return MeetingSeriesResponse.from(s);
    }}
}}
""")
w("meetingseries/application/action/UpdateMeetingSeriesAction.java", f"""package {PKG}.meetingseries.application.action;
import {PKG}.meetingseries.application.command.UpdateMeetingSeriesCommand;
import {PKG}.meetingseries.application.response.MeetingSeriesResponse;
import {PKG}.meetingseries.domain.enums.MeetingSeriesCadence;
import {PKG}.meetingseries.domain.model.*;
import {PKG}.shared.activity.CollaborationActivityLogger;
import {PKG}.shared.authorization.CollaborationAuthorizationService;
import {PKG}.shared.constant.*;
import {PKG}.shared.error.CollaborationExceptions;
import {PKG}.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateMeetingSeriesAction {{
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;
    public UpdateMeetingSeriesAction(MeetingSeriesRepository series, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {{
        this.series=series; this.authorization=authorization; this.activityLogger=activityLogger;
    }}
    @Transactional
    public MeetingSeriesResponse execute(UpdateMeetingSeriesCommand command) {{
        authorization.requireSeriesManage(command.projectId());
        MeetingSeries s = series.findByIdAndProjectId(command.seriesId(), command.projectId())
                .orElseThrow(() -> CollaborationExceptions.seriesNotFound(command.seriesId()));
        if (command.title() == null || command.title().isBlank()) throw CollaborationExceptions.titleRequired();
        MeetingSeriesCadence cadence = CollaborationEnumParser.parseOptional(MeetingSeriesCadence.class, command.cadence(), "cadence");
        s = series.save(s.update(command.title().trim(), command.description(), cadence, command.ownerUserId(), Boolean.TRUE.equals(command.clientVisible())));
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_UPDATED, "Series updated");
        return MeetingSeriesResponse.from(s);
    }}
}}
""")
w("meetingseries/application/action/PauseMeetingSeriesAction.java", f"""package {PKG}.meetingseries.application.action;
import {PKG}.meetingseries.application.response.MeetingSeriesResponse;
import {PKG}.meetingseries.domain.model.*;
import {PKG}.shared.activity.CollaborationActivityLogger;
import {PKG}.shared.authorization.CollaborationAuthorizationService;
import {PKG}.shared.constant.*;
import {PKG}.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class PauseMeetingSeriesAction {{
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;
    public PauseMeetingSeriesAction(MeetingSeriesRepository series, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {{
        this.series=series; this.authorization=authorization; this.activityLogger=activityLogger;
    }}
    @Transactional
    public MeetingSeriesResponse execute(UUID projectId, UUID seriesId) {{
        authorization.requireSeriesManage(projectId);
        MeetingSeries s = series.findByIdAndProjectId(seriesId, projectId).orElseThrow(() -> CollaborationExceptions.seriesNotFound(seriesId));
        s = series.save(s.pause());
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_PAUSED, "Series paused");
        return MeetingSeriesResponse.from(s);
    }}
}}
""")
w("meetingseries/application/action/ArchiveMeetingSeriesAction.java", f"""package {PKG}.meetingseries.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import {PKG}.meetingseries.application.response.MeetingSeriesResponse;
import {PKG}.meetingseries.domain.model.*;
import {PKG}.shared.activity.CollaborationActivityLogger;
import {PKG}.shared.authorization.CollaborationAuthorizationService;
import {PKG}.shared.constant.*;
import {PKG}.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveMeetingSeriesAction {{
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public ArchiveMeetingSeriesAction(MeetingSeriesRepository series, CollaborationAuthorizationService authorization,
                                      CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {{
        this.series=series; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }}
    @Transactional
    public MeetingSeriesResponse execute(UUID projectId, UUID seriesId) {{
        authorization.requireSeriesManage(projectId);
        var actor = currentUser.resolveCurrentUser();
        MeetingSeries s = series.findByIdAndProjectId(seriesId, projectId).orElseThrow(() -> CollaborationExceptions.seriesNotFound(seriesId));
        s = series.save(s.archive(actor.id()));
        activityLogger.logSuccess(CollaborationEntityTypes.MEETING_SERIES, s.id(), CollaborationActivityActions.MEETING_SERIES_ARCHIVED, "Series archived");
        return MeetingSeriesResponse.from(s);
    }}
}}
""")
w("meetingseries/application/service/MeetingSeriesQueryService.java", f"""package {PKG}.meetingseries.application.service;
import {PKG}.meetingseries.application.response.MeetingSeriesResponse;
import {PKG}.meetingseries.domain.model.MeetingSeriesRepository;
import {PKG}.shared.authorization.CollaborationAuthorizationService;
import {PKG}.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingSeriesQueryService {{
    private final MeetingSeriesRepository series; private final CollaborationAuthorizationService authorization;
    public MeetingSeriesQueryService(MeetingSeriesRepository series, CollaborationAuthorizationService authorization) {{
        this.series=series; this.authorization=authorization;
    }}
    @Transactional(readOnly=true)
    public MeetingSeriesResponse get(UUID projectId, UUID seriesId) {{
        authorization.requireSeriesManage(projectId);
        return MeetingSeriesResponse.from(series.findByIdAndProjectId(seriesId, projectId)
                .orElseThrow(() -> CollaborationExceptions.seriesNotFound(seriesId)));
    }}
    @Transactional(readOnly=true)
    public List<MeetingSeriesResponse> list(UUID projectId) {{
        authorization.requireSeriesManage(projectId);
        return series.findByProjectId(projectId).stream().map(MeetingSeriesResponse::from).toList();
    }}
}}
""")
w("meetingseries/http/controller/MeetingSeriesController.java", f"""package {PKG}.meetingseries.http.controller;
import com.company.scopery.common.response.ApiResponse;
import {PKG}.meetingseries.application.action.*;
import {PKG}.meetingseries.application.command.*;
import {PKG}.meetingseries.application.response.MeetingSeriesResponse;
import {PKG}.meetingseries.application.service.MeetingSeriesQueryService;
import {PKG}.meetingseries.http.request.*;
import {PKG}.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.MEETING_SERIES) @Tag(name = "Collaboration - Meeting Series")
public class MeetingSeriesController {{
    private final CreateMeetingSeriesAction create; private final UpdateMeetingSeriesAction update;
    private final PauseMeetingSeriesAction pause; private final ArchiveMeetingSeriesAction archive;
    private final MeetingSeriesQueryService query;
    public MeetingSeriesController(CreateMeetingSeriesAction create, UpdateMeetingSeriesAction update,
                                   PauseMeetingSeriesAction pause, ArchiveMeetingSeriesAction archive,
                                   MeetingSeriesQueryService query) {{
        this.create=create; this.update=update; this.pause=pause; this.archive=archive; this.query=query;
    }}
    @PostMapping @Operation(summary="Create meeting series")
    public ApiResponse<MeetingSeriesResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateMeetingSeriesRequest r) {{
        return ApiResponse.success(create.execute(new CreateMeetingSeriesCommand(projectId, r.code(), r.title(), r.description(), r.cadence(), r.ownerUserId(), r.clientVisible())));
    }}
    @GetMapping @Operation(summary="List meeting series")
    public ApiResponse<List<MeetingSeriesResponse>> list(@PathVariable UUID projectId) {{ return ApiResponse.success(query.list(projectId)); }}
    @GetMapping("/{{seriesId}}") @Operation(summary="Get meeting series")
    public ApiResponse<MeetingSeriesResponse> get(@PathVariable UUID projectId, @PathVariable UUID seriesId) {{ return ApiResponse.success(query.get(projectId, seriesId)); }}
    @PutMapping("/{{seriesId}}") @Operation(summary="Update meeting series")
    public ApiResponse<MeetingSeriesResponse> update(@PathVariable UUID projectId, @PathVariable UUID seriesId, @Valid @RequestBody UpdateMeetingSeriesRequest r) {{
        return ApiResponse.success(update.execute(new UpdateMeetingSeriesCommand(projectId, seriesId, r.title(), r.description(), r.cadence(), r.ownerUserId(), r.clientVisible())));
    }}
    @PatchMapping("/{{seriesId}}/pause") @Operation(summary="Pause meeting series")
    public ApiResponse<MeetingSeriesResponse> pause(@PathVariable UUID projectId, @PathVariable UUID seriesId) {{ return ApiResponse.success(pause.execute(projectId, seriesId)); }}
    @PatchMapping("/{{seriesId}}/archive") @Operation(summary="Archive meeting series")
    public ApiResponse<MeetingSeriesResponse> archive(@PathVariable UUID projectId, @PathVariable UUID seriesId) {{ return ApiResponse.success(archive.execute(projectId, seriesId)); }}
}}
""")

print("Meeting series complete")
print("OK so far")
