package com.company.scopery.modules.scope.deliverable.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteria;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.application.command.AcceptDeliverableCommand;
import com.company.scopery.modules.scope.review.domain.model.DeliverableAcceptanceRepository;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableStatus;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableType;
import com.company.scopery.modules.scope.deliverable.domain.model.Deliverable;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class AcceptDeliverableActionTest {

    @Mock DeliverableRepository deliverables;
    @Mock AcceptanceCriteriaRepository criteria;
    @Mock ScopeAuthorizationService authorization;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock DeliverableAcceptanceRepository acceptances;
    @Mock ScopeActivityLogger activityLogger;
    @Mock IamUser iamUser;

    AcceptDeliverableAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID deliverableId = UUID.randomUUID();
    final UUID actorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new AcceptDeliverableAction(deliverables, criteria, authorization, currentUser, acceptances, activityLogger);
    }

    @Test
    void execute_blocksAcceptWhenMandatoryCriteriaUnmet() {
        Deliverable d = deliverable(true);
        when(deliverables.findByIdAndProjectId(deliverableId, projectId)).thenReturn(Optional.of(d));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(criteria.findByDeliverableId(deliverableId)).thenReturn(List.of(
                AcceptanceCriteria.create(deliverableId, projectId, "FUNCTIONAL", "Must ship", null, true)));

        assertThatThrownBy(() -> action.execute(new AcceptDeliverableCommand(projectId, deliverableId)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ScopeErrorCatalog.DELIVERABLE_CRITERIA_NOT_MET.code()));
        verify(deliverables, never()).save(any());
    }

    @Test
    void execute_acceptsWhenMandatoryCriteriaSatisfied() {
        Deliverable d = deliverable(true);
        when(deliverables.findByIdAndProjectId(deliverableId, projectId)).thenReturn(Optional.of(d));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(actorId);
        when(criteria.findByDeliverableId(deliverableId)).thenReturn(List.of(
                AcceptanceCriteria.create(deliverableId, projectId, "FUNCTIONAL", "Must ship", null, true).satisfy()));
        when(deliverables.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = action.execute(new AcceptDeliverableCommand(projectId, deliverableId));

        assertThat(response.status()).isEqualTo(DeliverableStatus.ACCEPTED.name());
        verify(deliverables).save(any());
    }

    @Test
    void execute_skipsCriteriaGateWhenAcceptanceNotRequired() {
        Deliverable d = deliverable(false);
        when(deliverables.findByIdAndProjectId(deliverableId, projectId)).thenReturn(Optional.of(d));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(actorId);
        when(deliverables.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = action.execute(new AcceptDeliverableCommand(projectId, deliverableId));

        assertThat(response.status()).isEqualTo(DeliverableStatus.ACCEPTED.name());
        verify(criteria, never()).findByDeliverableId(any());
    }

    private Deliverable deliverable(boolean acceptanceRequired) {
        Deliverable created = Deliverable.create(projectId, UUID.randomUUID(), DeliverableType.DOCUMENT,
                "D-1", "Spec", null, acceptanceRequired);
        return new Deliverable(
                deliverableId, created.projectId(), created.workspaceId(), null, null,
                created.type(), created.code(), created.title(), created.description(),
                created.acceptanceRequired(), created.status(), null, null, null, null, null,
                null, null, null, null, null, 0, created.createdAt(), created.updatedAt());
    }
}
