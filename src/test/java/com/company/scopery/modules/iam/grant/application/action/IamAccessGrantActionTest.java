package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.command.RemoveIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.command.RevokeIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantRightResponse;
import com.company.scopery.modules.iam.grant.application.service.IamAccessGrantQueryService;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamAccessGrantActionTest {

    @Mock private IamAccessGrantRepository grantRepository;
    @Mock private IamAccessGrantRightRepository grantRightRepository;
    @Mock private IamAuthResourceRepository resourceRepository;
    @Mock private IamRightRepository rightRepository;
    @Mock private IamActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private AuthorizationDecisionService authorizationDecisionService;
    @Mock private ImmutableAuditEventService auditEventService;
    @Mock private IamAccessGrantPermissionActionRepository grantActionRepository;
    @Mock private IamPermissionRepository permissionRepository;
    @Mock private IamPermissionActionDefinitionRepository actionRepository;

    private CreateIamAccessGrantAction createAction;
    private RevokeIamAccessGrantAction revokeAction;
    private AddIamGrantRightAction addRightAction;
    private RemoveIamGrantRightAction removeRightAction;
    private IamAccessGrantQueryService queryService;

    private UUID subjectId;
    private UUID grantedBy;
    private IamAuthResource activeResource;
    private IamAuthResource inactiveResource;
    private IamAccessGrant activeGrant;
    private IamAccessGrant revokedGrant;
    private IamRight activeRight;
    private IamRight inactiveRight;
    private IamUser actor;

    @BeforeEach
    void setUp() {
        createAction = new CreateIamAccessGrantAction(grantRepository, resourceRepository,
                currentUserService, activityLogger, authorizationDecisionService, auditEventService);
        revokeAction = new RevokeIamAccessGrantAction(grantRepository, activityLogger,
                currentUserService, authorizationDecisionService, resourceRepository, auditEventService);
        addRightAction = new AddIamGrantRightAction(grantRepository, grantRightRepository,
                rightRepository, resourceRepository, currentUserService, authorizationDecisionService, activityLogger);
        removeRightAction = new RemoveIamGrantRightAction(grantRepository, grantRightRepository,
                resourceRepository, currentUserService, authorizationDecisionService, activityLogger);
        queryService = new IamAccessGrantQueryService(grantRepository, grantRightRepository,
                grantActionRepository, permissionRepository, actionRepository, rightRepository);

        Instant now = Instant.now();
        subjectId = UUID.randomUUID();
        grantedBy = UUID.randomUUID();
        actor = new IamUser(grantedBy, Username.of("actor"), EmailAddress.of("actor@example.com"),
                "Actor", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(actor);

        UUID wsId = UUID.randomUUID();
        activeResource = IamAuthResource.createWithOwnership(
                IamResourceCode.of("WORKSPACE_A"), IamResourceType.WORKSPACE, "Workspace A", null,
                wsId, null, null, wsId, IamResourceVisibility.PRIVATE, null);
        inactiveResource = activeResource.deactivate();

        activeGrant = IamAccessGrant.create(IamSubjectType.USER, subjectId, activeResource.id(),
                null, IamGrantEffect.ALLOW, null, null, wsId, grantedBy);
        revokedGrant = activeGrant.revoke();

        activeRight = IamRight.create(IamRightCode.of("VIEW_WORKSPACE"), "View Workspace", null, "WORKSPACE");
        inactiveRight = activeRight.deactivate();

        lenient().when(resourceRepository.findById(activeGrant.resourceId())).thenReturn(Optional.of(activeResource));
    }

    @Test
    void createGrant_validRequest_succeeds() {
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(grantRepository.existsBySubjectIdAndResourceId(subjectId, activeResource.id())).thenReturn(false);
        when(grantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAccessGrantResponse response = createAction.execute(
                new CreateIamAccessGrantCommand("USER", subjectId, activeResource.id(),
                        null, null, null, null, null));

        assertThat(response.subjectType()).isEqualTo("USER");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void createGrant_inactiveResource_throws422() {
        when(resourceRepository.findById(inactiveResource.id())).thenReturn(Optional.of(inactiveResource));

        assertThatThrownBy(() -> createAction.execute(
                new CreateIamAccessGrantCommand("USER", subjectId, inactiveResource.id(),
                        null, null, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_AUTH_RESOURCE_INACTIVE_CANNOT_BE_USED.code()));
    }

    @Test
    void createGrant_invalidSubjectType_throws400() {
        assertThatThrownBy(() -> createAction.execute(
                new CreateIamAccessGrantCommand("INVALID", subjectId, activeResource.id(),
                        null, null, null, null, null)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void revokeGrant_activeGrant_succeeds() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(grantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAccessGrantResponse response = revokeAction.execute(new RevokeIamAccessGrantCommand(activeGrant.id()));

        assertThat(response.status()).isEqualTo("REVOKED");
    }

    @Test
    void addRight_revokedGrant_throws422() {
        when(grantRepository.findById(revokedGrant.id())).thenReturn(Optional.of(revokedGrant));

        assertThatThrownBy(() -> addRightAction.execute(
                new AddIamGrantRightCommand(revokedGrant.id(), activeRight.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_GRANT_REVOKED_CANNOT_BE_MODIFIED.code()));
    }

    @Test
    void addRight_validRequest_succeeds() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(rightRepository.findById(activeRight.id())).thenReturn(Optional.of(activeRight));
        when(grantRightRepository.existsByGrantIdAndRightId(activeGrant.id(), activeRight.id())).thenReturn(false);
        when(grantRightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAccessGrantRightResponse response = addRightAction.execute(
                new AddIamGrantRightCommand(activeGrant.id(), activeRight.id()));

        assertThat(response.grantId()).isEqualTo(activeGrant.id());
        assertThat(response.rightId()).isEqualTo(activeRight.id());
    }

    @Test
    void removeRight_notAttached_throws404() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(grantRightRepository.existsByGrantIdAndRightId(activeGrant.id(), activeRight.id())).thenReturn(false);

        assertThatThrownBy(() -> removeRightAction.execute(
                new RemoveIamGrantRightCommand(activeGrant.id(), activeRight.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_GRANT_RIGHT_NOT_FOUND.code()));
    }

    @Test
    void getGrantRights_existingGrant_returnsList() {
        IamAccessGrantRight grantRight = IamAccessGrantRight.create(activeGrant.id(), activeRight.id());
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(grantRightRepository.findByGrantId(activeGrant.id())).thenReturn(List.of(grantRight));

        List<IamAccessGrantRightResponse> rights = queryService.getGrantRights(activeGrant.id());

        assertThat(rights).hasSize(1);
        assertThat(rights.getFirst().rightId()).isEqualTo(activeRight.id());
    }
}
