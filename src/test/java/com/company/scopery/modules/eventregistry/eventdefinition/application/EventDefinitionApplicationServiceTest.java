package com.company.scopery.modules.eventregistry.eventdefinition.application;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.ActivateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.CreateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.DeactivateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.service.EventDefinitionQueryService;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.*;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.*;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
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


    private ActivateEventDefinitionAction activateEventDefinitionAction;
    private CreateEventDefinitionAction createEventDefinitionAction;
    private DeactivateEventDefinitionAction deactivateEventDefinitionAction;
    private EventDefinitionQueryService eventDefinitionQueryService;

    @BeforeEach
    void setUp() {
        activateEventDefinitionAction = new ActivateEventDefinitionAction(repository, activityLogger, systemAuthorizationService);
        createEventDefinitionAction = new CreateEventDefinitionAction(repository, activityLogger, systemAuthorizationService);
        deactivateEventDefinitionAction = new DeactivateEventDefinitionAction(repository, activityLogger, systemAuthorizationService);
        eventDefinitionQueryService = new EventDefinitionQueryService(repository);
    }

    @Test
    void createEventDefinition_success() {
        CreateEventDefinitionCommand command = new CreateEventDefinitionCommand(
                "HRM_CV_UPLOADED", "CV Uploaded", "HRM", "CV_UPLOADED", null, null, null);

        when(repository.existsByCode(any())).thenReturn(false);
        when(repository.existsBySourceSystemAndEventKey(any(), any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventDefinitionResponse response = createEventDefinitionAction.execute(command);

        assertThat(response.code()).isEqualTo("HRM_CV_UPLOADED");
        assertThat(response.sourceSystem()).isEqualTo("HRM");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void createEventDefinition_duplicateCode_throwsConflict() {
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
    void createEventDefinition_duplicateSourceEventPair_throwsConflict() {
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
    void createEventDefinition_invalidInputSchemaJson_throwsBadRequest() {
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
                new com.company.scopery.modules.eventregistry.eventdefinition.application.query.GetEventDefinitionDetailQuery(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void activateEventDefinition_whenDeprecated_throwsUnprocessable() {
        UUID id = UUID.randomUUID();
        EventDefinition deprecated = EventDefinition.reconstitute(id,
                EventDefinitionCode.of("OLD"), "Old", SourceSystemCode.of("SYS"), EventKey.of("OLD"),
                null, null, null, EventDefinitionStatus.DEPRECATED,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());

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
    void deactivateEventDefinition_setsInactive() {
        UUID id = UUID.randomUUID();
        EventDefinition active = EventDefinition.reconstitute(id,
                EventDefinitionCode.of("CODE"), "Name", SourceSystemCode.of("SYS"), EventKey.of("KEY"),
                null, null, null, EventDefinitionStatus.ACTIVE,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventDefinitionDetailResponse response = deactivateEventDefinitionAction.execute(
                new DeactivateEventDefinitionCommand(id));

        assertThat(response.status()).isEqualTo("INACTIVE");
    }
}