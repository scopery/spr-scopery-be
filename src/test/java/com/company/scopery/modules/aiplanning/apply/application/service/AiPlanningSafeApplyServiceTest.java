package com.company.scopery.modules.aiplanning.apply.application.service;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiplanning.apply.domain.enums.ApplyMode;
import com.company.scopery.modules.aiplanning.applyresult.domain.enums.ApplyResultStatus;
import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResult;
import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResultRepository;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionType;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemOperation;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemType;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItem;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItemRepository;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.changerequest.application.action.CreateChangeRequestAction;
import com.company.scopery.modules.projectbaseline.changerequest.application.command.CreateChangeRequestCommand;
import com.company.scopery.modules.projectbaseline.changerequest.application.response.ChangeRequestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiPlanningSafeApplyServiceTest {

    @Mock private ProjectRepository projects;
    @Mock private ProjectBaselineRepository baselines;
    @Mock private AiPlanningSuggestionRepository suggestions;
    @Mock private AiPlanningSuggestionItemRepository items;
    @Mock private AiPlanningApplyResultRepository applyResults;
    @Mock private CreateChangeRequestAction createChangeRequestAction;

    private AiPlanningSafeApplyService service;

    private final UUID projectId = UUID.randomUUID();
    private final UUID actorId = UUID.randomUUID();
    private final UUID baselineId = UUID.randomUUID();
    private final UUID crId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AiPlanningSafeApplyService(
                projects, baselines, suggestions, items, applyResults, createChangeRequestAction);
        org.mockito.Mockito.lenient().when(applyResults.save(any())).thenAnswer(inv -> inv.getArgument(0));
        org.mockito.Mockito.lenient().when(items.save(any())).thenAnswer(inv -> inv.getArgument(0));
        org.mockito.Mockito.lenient().when(suggestions.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private Project project(UUID currentBaselineId) {
        return new Project(
                projectId, UUID.randomUUID(), UUID.randomUUID(), "P-1", "Demo", null,
                actorId, "USD", null, null, ProjectStatus.ACTIVE,
                null, null, null, null, null, null, null, null, null, null, null, null, currentBaselineId,
                0, Instant.now(), Instant.now());
    }

    private AiPlanningSuggestion acceptedSuggestion() {
        return AiPlanningSuggestion.create(
                        UUID.randomUUID(), projectId, UUID.randomUUID(),
                        SuggestionType.MIXED_PLAN, "Plan", "sum", "why", "HIGH", "{}")
                .accept(actorId);
    }

    private AiPlanningSuggestionItem acceptedItem(UUID suggestionId, SuggestionItemType type) {
        return AiPlanningSuggestionItem.create(
                        suggestionId, projectId, type, SuggestionItemOperation.CREATE,
                        "item", "d", "{}", "r", "MED", type.name(), null)
                .accept(actorId);
    }

    @Test
    void applyBaselinedProject_createsChangeRequestInsteadOfDirectMutation() {
        AiPlanningSuggestion suggestion = acceptedSuggestion();
        AiPlanningSuggestionItem item = acceptedItem(suggestion.id(), SuggestionItemType.TASK);
        when(projects.findById(projectId)).thenReturn(Optional.of(project(baselineId)));
        when(suggestions.findByIdAndProjectId(suggestion.id(), projectId)).thenReturn(Optional.of(suggestion));
        when(items.findBySuggestionId(suggestion.id())).thenReturn(List.of(item));
        when(baselines.findCurrentByProjectId(projectId)).thenReturn(Optional.empty());
        when(createChangeRequestAction.execute(any())).thenReturn(crResponse());

        AiPlanningSafeApplyService.ApplyOutcome outcome = service.applySuggestion(
                projectId, suggestion.id(), actorId, ApplyMode.ALL_ACCEPTED_ITEMS, true);

        assertThat(outcome.changeRequestCreated()).isTrue();
        assertThat(outcome.changeRequestId()).isEqualTo(crId);
        assertThat(outcome.results()).isNotEmpty();
        assertThat(outcome.results().getFirst().status()).isEqualTo(ApplyResultStatus.CHANGE_REQUEST_REQUIRED);
        ArgumentCaptor<CreateChangeRequestCommand> captor = ArgumentCaptor.forClass(CreateChangeRequestCommand.class);
        verify(createChangeRequestAction).execute(captor.capture());
        assertThat(captor.getValue().projectId()).isEqualTo(projectId);
        assertThat(captor.getValue().changeType()).isEqualTo("SCOPE_CHANGE");
    }

    @Test
    void draftChangeRequest_applyCreatesChangeRequestDraft() {
        AiPlanningSuggestion suggestion = acceptedSuggestion();
        AiPlanningSuggestionItem item = acceptedItem(suggestion.id(), SuggestionItemType.WBS_NODE);
        when(projects.findById(projectId)).thenReturn(Optional.of(project(null)));
        when(suggestions.findByIdAndProjectId(suggestion.id(), projectId)).thenReturn(Optional.of(suggestion));
        when(items.findBySuggestionId(suggestion.id())).thenReturn(List.of(item));
        when(baselines.findCurrentByProjectId(projectId)).thenReturn(Optional.empty());
        when(createChangeRequestAction.execute(any())).thenReturn(crResponse());

        AiPlanningSafeApplyService.ApplyOutcome outcome = service.applySuggestion(
                projectId, suggestion.id(), actorId, ApplyMode.CREATE_CHANGE_REQUEST_ONLY, false);

        assertThat(outcome.changeRequestCreated()).isTrue();
        verify(createChangeRequestAction).execute(any());
    }

    @Test
    void applyAcceptedCreateTask_usesSkipPendingDomainForm_whenNotBaselined() {
        AiPlanningSuggestion suggestion = acceptedSuggestion();
        AiPlanningSuggestionItem item = acceptedItem(suggestion.id(), SuggestionItemType.TASK);
        when(projects.findById(projectId)).thenReturn(Optional.of(project(null)));
        when(suggestions.findByIdAndProjectId(suggestion.id(), projectId)).thenReturn(Optional.of(suggestion));
        when(items.findBySuggestionId(suggestion.id())).thenReturn(List.of(item));
        when(baselines.findCurrentByProjectId(projectId)).thenReturn(Optional.empty());

        AiPlanningSafeApplyService.ApplyOutcome outcome = service.applySuggestion(
                projectId, suggestion.id(), actorId, ApplyMode.ALL_ACCEPTED_ITEMS, true);

        assertThat(outcome.changeRequestCreated()).isFalse();
        assertThat(outcome.results()).hasSize(1);
        assertThat(outcome.results().getFirst().status()).isEqualTo(ApplyResultStatus.SKIPPED);
        verify(createChangeRequestAction, never()).execute(any());
    }

    @Test
    void financeInsight_doesNotChangeFinanceScenario_onApplyNoteItem() {
        AiPlanningSuggestion suggestion = acceptedSuggestion();
        AiPlanningSuggestionItem item = acceptedItem(suggestion.id(), SuggestionItemType.FINANCE_NOTE);
        when(projects.findById(projectId)).thenReturn(Optional.of(project(null)));
        when(suggestions.findByIdAndProjectId(suggestion.id(), projectId)).thenReturn(Optional.of(suggestion));
        when(items.findBySuggestionId(suggestion.id())).thenReturn(List.of(item));
        when(baselines.findCurrentByProjectId(projectId)).thenReturn(Optional.empty());

        AiPlanningSafeApplyService.ApplyOutcome outcome = service.applySuggestion(
                projectId, suggestion.id(), actorId, ApplyMode.ALL_ACCEPTED_ITEMS, true);

        assertThat(outcome.results().getFirst().status()).isEqualTo(ApplyResultStatus.SUCCESS);
        assertThat(outcome.results().getFirst().domainAction()).isEqualTo("RECORD_PROPOSAL");
        verify(createChangeRequestAction, never()).execute(any());
    }

    @Test
    void rejectedSuggestion_cannotApply() {
        AiPlanningSuggestion rejected = AiPlanningSuggestion.create(
                        UUID.randomUUID(), projectId, UUID.randomUUID(),
                        SuggestionType.MIXED_PLAN, "Plan", "sum", "why", "HIGH", "{}")
                .reject(actorId, "no");
        when(projects.findById(projectId)).thenReturn(Optional.of(project(null)));
        when(suggestions.findByIdAndProjectId(rejected.id(), projectId)).thenReturn(Optional.of(rejected));

        assertThatThrownBy(() -> service.applySuggestion(
                projectId, rejected.id(), actorId, ApplyMode.ALL_ACCEPTED_ITEMS, true))
                .isInstanceOf(AppException.class);
        verify(createChangeRequestAction, never()).execute(any());
    }

    @Test
    void applyWithoutAcceptedItems_rejected() {
        AiPlanningSuggestion suggestion = acceptedSuggestion();
        AiPlanningSuggestionItem proposed = AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.TASK, SuggestionItemOperation.CREATE,
                "item", "d", "{}", "r", "MED", "TASK", null);
        when(projects.findById(projectId)).thenReturn(Optional.of(project(null)));
        when(suggestions.findByIdAndProjectId(suggestion.id(), projectId)).thenReturn(Optional.of(suggestion));
        when(items.findBySuggestionId(suggestion.id())).thenReturn(List.of(proposed));
        when(baselines.findCurrentByProjectId(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.applySuggestion(
                projectId, suggestion.id(), actorId, ApplyMode.ALL_ACCEPTED_ITEMS, true))
                .isInstanceOf(AppException.class);
    }

    @Test
    void draftChangeRequest_doesNotSubmitApproveApply() {
        AiPlanningSuggestion suggestion = acceptedSuggestion();
        AiPlanningSuggestionItem item = acceptedItem(suggestion.id(), SuggestionItemType.CHANGE_REQUEST);
        when(projects.findById(projectId)).thenReturn(Optional.of(project(baselineId)));
        when(suggestions.findByIdAndProjectId(suggestion.id(), projectId)).thenReturn(Optional.of(suggestion));
        when(items.findBySuggestionId(suggestion.id())).thenReturn(List.of(item));
        when(baselines.findCurrentByProjectId(projectId)).thenReturn(Optional.empty());
        when(createChangeRequestAction.execute(any())).thenReturn(crResponse());

        service.applySuggestion(projectId, suggestion.id(), actorId, ApplyMode.ALL_ACCEPTED_ITEMS, true);

        verify(createChangeRequestAction).execute(any());
        // Only CreateChangeRequestAction is invoked — no submit/approve/apply collaborators exist on this service.
    }

    private ChangeRequestResponse crResponse() {
        return new ChangeRequestResponse(
                crId, projectId, UUID.randomUUID(), baselineId, "AI-CR-1", "title", "desc",
                "SCOPE_CHANGE", "MEDIUM", "DRAFT", "reason",
                null, null, null, null, null, null, null, null, null,
                Instant.now(), Instant.now());
    }
}
