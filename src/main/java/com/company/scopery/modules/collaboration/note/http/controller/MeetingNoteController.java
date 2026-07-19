package com.company.scopery.modules.collaboration.note.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.note.application.action.*;
import com.company.scopery.modules.collaboration.note.application.command.*;
import com.company.scopery.modules.collaboration.note.application.response.MeetingNoteResponse;
import com.company.scopery.modules.collaboration.note.application.response.NoteConversionResponse;
import com.company.scopery.modules.collaboration.note.application.service.MeetingNoteQueryService;
import com.company.scopery.modules.collaboration.note.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CollaborationApiPaths.MEETINGS + "/{meetingId}/notes")
@Tag(name = "Collaboration - Notes")
public class MeetingNoteController {

    private final CreateNoteAction create;
    private final UpdateNoteAction update;
    private final ArchiveNoteAction archive;
    private final CreateDecisionFromNoteAction createDecision;
    private final CreateRaidItemFromNoteAction createRaid;
    private final CreateRequirementFromNoteAction createRequirement;
    private final CreateChangeRequestFromNoteAction createChangeRequest;
    private final MeetingNoteQueryService query;

    public MeetingNoteController(CreateNoteAction create, UpdateNoteAction update, ArchiveNoteAction archive,
                                 CreateDecisionFromNoteAction createDecision, CreateRaidItemFromNoteAction createRaid,
                                 CreateRequirementFromNoteAction createRequirement,
                                 CreateChangeRequestFromNoteAction createChangeRequest, MeetingNoteQueryService query) {
        this.create = create;
        this.update = update;
        this.archive = archive;
        this.createDecision = createDecision;
        this.createRaid = createRaid;
        this.createRequirement = createRequirement;
        this.createChangeRequest = createChangeRequest;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create note")
    public ApiResponse<MeetingNoteResponse> create(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                   @Valid @RequestBody CreateNoteRequest r) {
        return ApiResponse.success(create.execute(new CreateNoteCommand(
                projectId, meetingId, r.agendaItemId(), r.noteType(), r.body(), r.clientVisible())));
    }

    @GetMapping
    @Operation(summary = "List notes")
    public ApiResponse<List<MeetingNoteResponse>> list(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(query.list(projectId, meetingId));
    }

    @PutMapping("/{noteId}")
    @Operation(summary = "Update note")
    public ApiResponse<MeetingNoteResponse> update(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                   @PathVariable UUID noteId, @Valid @RequestBody UpdateNoteRequest r) {
        return ApiResponse.success(update.execute(new UpdateNoteCommand(
                projectId, meetingId, noteId, r.noteType(), r.body(), r.clientVisible())));
    }

    @PatchMapping("/{noteId}/archive")
    @Operation(summary = "Archive note")
    public ApiResponse<MeetingNoteResponse> archive(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                    @PathVariable UUID noteId) {
        return ApiResponse.success(archive.execute(new ArchiveNoteCommand(projectId, meetingId, noteId)));
    }

    @PostMapping("/{noteId}/create-decision")
    @Operation(summary = "Create DecisionRecord from note")
    public ApiResponse<NoteConversionResponse> createDecision(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                              @PathVariable UUID noteId,
                                                              @RequestBody(required = false) CreateDecisionFromNoteRequest r) {
        var req = r == null ? new CreateDecisionFromNoteRequest(null, null, null, null) : r;
        return ApiResponse.success(createDecision.execute(new CreateDecisionFromNoteCommand(
                projectId, meetingId, noteId, req.title(), req.rationale(), req.category(), req.code())));
    }

    @PostMapping("/{noteId}/create-raid-item")
    @Operation(summary = "Create RAID item from note")
    public ApiResponse<NoteConversionResponse> createRaid(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                          @PathVariable UUID noteId,
                                                          @RequestBody(required = false) CreateRaidItemFromNoteRequest r) {
        var req = r == null ? new CreateRaidItemFromNoteRequest(null, null, null, null, null) : r;
        return ApiResponse.success(createRaid.execute(new CreateRaidItemFromNoteCommand(
                projectId, meetingId, noteId, req.type(), req.title(), req.code(), req.description(), req.ownerUserId())));
    }

    @PostMapping("/{noteId}/create-requirement")
    @Operation(summary = "Create Requirement from note")
    public ApiResponse<NoteConversionResponse> createRequirement(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                                 @PathVariable UUID noteId,
                                                                 @RequestBody(required = false) CreateRequirementFromNoteRequest r) {
        var req = r == null ? new CreateRequirementFromNoteRequest(null, null, null, null, null, null) : r;
        return ApiResponse.success(createRequirement.execute(new CreateRequirementFromNoteCommand(
                projectId, meetingId, noteId, req.applicationId(), req.code(), req.title(),
                req.description(), req.requirementType(), req.priority())));
    }

    @PostMapping("/{noteId}/create-change-request-draft")
    @Operation(summary = "Create ChangeRequest draft from note")
    public ApiResponse<NoteConversionResponse> createChangeRequest(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                                   @PathVariable UUID noteId,
                                                                   @Valid @RequestBody CreateChangeRequestFromNoteRequest r) {
        return ApiResponse.success(createChangeRequest.execute(new CreateChangeRequestFromNoteCommand(
                projectId, meetingId, noteId, r.code(), r.title(), r.description(),
                r.changeType(), r.priority(), r.baselineId(), r.reason())));
    }
}
