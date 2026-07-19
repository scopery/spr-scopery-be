package com.company.scopery.modules.eventregistry.eventdefinition.application;

import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.ActivateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.CreateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.DeactivateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.DeprecateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.UpsertEventVariablesAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.ActivateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.CreateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.DeactivateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.DeprecateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.UpsertEventVariablesCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.GetEventDefinitionDetailQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.service.EventDefinitionConsumerSafetyService;
import com.company.scopery.modules.eventregistry.eventdefinition.application.service.EventDefinitionQueryService;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryErrorCatalog;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventDefinitionActionTest {

    @Mock private EventDefinitionRepository repository;
    @Mock private EventRegistryActivityLogger activityLogger;
    @Mock private IamSystemAuthorizationService systemAuthorizationService;
    @Mock private EventDefinitionConsumerSafetyService consumerSafetyService;
    @Mock private ImmutableAuditEventService auditEventService;

    private ActivateEventDefinitionAction activateEventDefinitionAction;
    private CreateEventDefinitionAction createEventDefinitionAction;
    private DeactivateEventDefinitionAction deactivateEventDefinitionAction;
    private DeprecateEventDefinitionAction deprecateEventDefinitionAction;
    private UpsertEventVariablesAction upsertEventVariablesAction;
    private EventDefinitionQueryService eventDefinitionQueryService;

    @BeforeEach
    void setUp() {
        activateEventDefinitionAction = new ActivateEventDefinitionAction(repository, activityLogger, systemAuthorizationService);
        createEventDefinitionAction = new CreateEventDefinitionAction(repository, activityLogger, systemAuthorizationService);
        deactivateEventDefinitionAction = new DeactivateEventDefinitionAction(
                repository, activityLogger, systemAuthorizationService, consumerSafetyService);
        deprecateEventDefinitionAction = new DeprecateEventDefinitionAction(
                repository, activityLogger, systemAuthorizationService, auditEventService);
        upsertEventVariablesAction = new UpsertEventVariablesAction(
                repository, activityLogger, systemAuthorizationService, consumerSafetyService);
        eventDefinitionQueryService = new EventDefinitionQueryService(repository, systemAuthorizationService);
    }

    @Test
    void createEventDefinition_valid_success() {
        CreateEventDefinitionCommand command = new CreateEventDefinitionCommand(
                "HRM_CV_UPLOADED", "CV Uploaded", "HRM", "CV_UPLOADED", null, null, null);

        when(repository.existsByCode(any())).thenReturn(false);
        when(repository.existsBySourceSystemAndEventKey(any(), any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventDefinitionResponse response = createEventDefinitionAction.execute(command);

        assertThat(response.code()).isEqualTo("HRM_CV_UPLOADED");
        assertThat(response.sourceSystem()).isEqualTo("HRM");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.eventVersion()).isEqualTo(1);
    }

    @Test
    void createEventDefinition_duplicateCode_conflict() {
        CreateEventDefinitionCommand command = new CreateEventDefinitionCommand(
                "HRM_CV_UPLOADED", "CV Uploaded", "HRM", "CV_UPLOADED", null, null, null);

        when(repository.existsByCode(any())).thenReturn(true);

        assertThatThrownBy(() -> createEventDefinitionAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            EventRegistryErrorCatalog.EVENT_DEFINITION_CODE_ALREADY_EXISTS.code());
                });

        verify(repository, never()).save(any());
    }

    @Test
    void createEventDefinition_duplicateSourceSystemEventKey_conflict() {
        CreateEventDefinitionCommand command = new CreateEventDefinitionCommand(
                "ANOTHER_CODE", "Another", "HRM", "CV_UPLOADED", null, null, null);

        when(repository.existsByCode(any())).thenReturn(false);
        when(repository.existsBySourceSystemAndEventKey(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> createEventDefinitionAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            EventRegistryErrorCatalog.EVENT_DEFINITION_SOURCE_EVENT_ALREADY_EXISTS.code());
                });
    }

    @Test
    void createEventDefinition_invalidInputSchema_rejected() {
        CreateEventDefinitionCommand command = new CreateEventDefinitionCommand(
                "CODE", "Name", "SYS", "KEY", null, "not-valid-json{{{", null);

        when(repository.existsByCode(any())).thenReturn(false);
        when(repository.existsBySourceSystemAndEventKey(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> createEventDefinitionAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_INPUT_SCHEMA_JSON.code());
                });
    }

    @Test
    void getEventDefinitionDetail_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventDefinitionQueryService.getEventDefinitionDetail(
                new GetEventDefinitionDetailQuery(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void activateDeprecatedEvent_rejected() {
        UUID id = UUID.randomUUID();
        EventDefinition deprecated = reconstitute(id, EventDefinitionStatus.DEPRECATED);

        when(repository.findById(id)).thenReturn(Optional.of(deprecated));

        assertThatThrownBy(() -> activateEventDefinitionAction.execute(new ActivateEventDefinitionCommand(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            EventRegistryErrorCatalog.DEPRECATED_EVENT_DEFINITION_CANNOT_BE_ACTIVATED.code());
                });
    }

    @Test
    void deprecateActiveEvent_success() {
        UUID id = UUID.randomUUID();
        EventDefinition active = reconstitute(id, EventDefinitionStatus.ACTIVE);
        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.findVariablesByEventDefinitionId(id)).thenReturn(List.of());

        EventDefinitionDetailResponse response = deprecateEventDefinitionAction.execute(
                new DeprecateEventDefinitionCommand(id, null, "replaced"));

        assertThat(response.status()).isEqualTo("DEPRECATED");
        verify(auditEventService).record(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void deactivateEventWithActiveConsumers_blocked() {
        UUID id = UUID.randomUUID();
        EventDefinition active = reconstitute(id, EventDefinitionStatus.ACTIVE);
        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(consumerSafetyService.hasActiveConsumers(id)).thenReturn(true);

        assertThatThrownBy(() -> deactivateEventDefinitionAction.execute(new DeactivateEventDefinitionCommand(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(
                        EventRegistryErrorCatalog.EVENT_DEFINITION_HAS_ACTIVE_CONSUMERS.code()));
    }

    @Test
    void deactivateEventDefinition_setsInactive() {
        UUID id = UUID.randomUUID();
        EventDefinition active = reconstitute(id, EventDefinitionStatus.ACTIVE);

        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(consumerSafetyService.hasActiveConsumers(id)).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.findVariablesByEventDefinitionId(id)).thenReturn(List.of());

        EventDefinitionDetailResponse response = deactivateEventDefinitionAction.execute(
                new DeactivateEventDefinitionCommand(id));

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    void upsertVariables_valid_success() {
        UUID id = UUID.randomUUID();
        EventDefinition active = reconstitute(id, EventDefinitionStatus.ACTIVE);
        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(consumerSafetyService.hasActiveConsumers(id)).thenReturn(false);
        when(repository.findVariablesByEventDefinitionId(id)).thenReturn(List.of());
        when(repository.saveVariable(any())).thenAnswer(inv -> inv.getArgument(0));

        List<EventVariableResponse> saved = upsertEventVariablesAction.execute(
                new UpsertEventVariablesCommand(id, List.of(
                        new UpsertEventVariablesCommand.VariableEntry(
                                "actor.userId", "Actor", "UUID", false, false, null, null))));

        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().variablePath()).isEqualTo("actor.userId");
    }

    @Test
    void upsertVariables_duplicatePath_rejected() {
        UUID id = UUID.randomUUID();
        EventDefinition active = reconstitute(id, EventDefinitionStatus.ACTIVE);
        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(repository.findVariablesByEventDefinitionId(id)).thenReturn(List.of());

        assertThatThrownBy(() -> upsertEventVariablesAction.execute(
                new UpsertEventVariablesCommand(id, List.of(
                        new UpsertEventVariablesCommand.VariableEntry("a.b", "A", "STRING", false, null, null),
                        new UpsertEventVariablesCommand.VariableEntry("a.b", "B", "STRING", false, null, null)))))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(
                        EventRegistryErrorCatalog.EVENT_VARIABLE_DUPLICATE_PATH.code()));
    }

    @Test
    void upsertVariables_removingUsedRequiredVariable_rejected() {
        UUID id = UUID.randomUUID();
        EventDefinition active = reconstitute(id, EventDefinitionStatus.ACTIVE);
        EventVariable existing = EventVariable.create(id, "task.id", "Task", VariableType.UUID, true, null, null);
        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(repository.findVariablesByEventDefinitionId(id)).thenReturn(List.of(existing));
        when(consumerSafetyService.hasActiveConsumers(id)).thenReturn(true);

        assertThatThrownBy(() -> upsertEventVariablesAction.execute(
                new UpsertEventVariablesCommand(id, List.of())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(
                        EventRegistryErrorCatalog.EVENT_VARIABLE_REQUIRED_REMOVAL_BLOCKED.code()));
    }

    private static EventDefinition reconstitute(UUID id, EventDefinitionStatus status) {
        return EventDefinition.reconstitute(id,
                EventDefinitionCode.of("CODE"), "Name", SourceSystemCode.of("SYS"), EventKey.of("KEY"),
                null, null, null, status,
                EventDefinition.INITIAL_VERSION, null,
                null, null, true,
                null, null, null,
                Instant.now(), Instant.now());
    }
}
