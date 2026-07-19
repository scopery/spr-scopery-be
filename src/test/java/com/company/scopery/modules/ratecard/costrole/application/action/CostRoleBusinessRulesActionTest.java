package com.company.scopery.modules.ratecard.costrole.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.ratecard.costrole.application.command.ArchiveCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.command.CreateCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardErrorCatalog;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostRoleBusinessRulesActionTest {

    @Mock CostRoleRepository costRoleRepository;
    @Mock WorkspaceRepository workspaceRepository;
    @Mock OrganizationRepository organizationRepository;
    @Mock RateCardAuthorizationService authorizationService;
    @Mock RateCardActivityLogger activityLogger;
    @Mock RateCardPlatformPublisher platformPublisher;

    CreateCostRoleAction createAction;
    ArchiveCostRoleAction archiveAction;

    UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createAction = new CreateCostRoleAction(costRoleRepository, workspaceRepository, organizationRepository,
                authorizationService, activityLogger, platformPublisher);
        archiveAction = new ArchiveCostRoleAction(costRoleRepository, authorizationService, activityLogger, platformPublisher);
    }

    @Test
    void create_normalizesCodeUppercase() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(activeWorkspace()));
        when(costRoleRepository.existsByScopeAndCode(any(), any(), any(), any())).thenReturn(false);
        when(costRoleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        createAction.execute(new CreateCostRoleCommand("backend_developer", "Backend", null,
                "WORKSPACE", null, workspaceId, "ENGINEERING"));

        ArgumentCaptor<CostRole> captor = ArgumentCaptor.forClass(CostRole.class);
        verify(costRoleRepository).save(captor.capture());
        assertThat(captor.getValue().code()).isEqualTo("BACKEND_DEVELOPER");
        assertThat(captor.getValue().scope()).isEqualTo(CostRoleScope.WORKSPACE);
    }

    @Test
    void create_duplicateCode_conflict() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(activeWorkspace()));
        when(costRoleRepository.existsByScopeAndCode(any(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> createAction.execute(new CreateCostRoleCommand("BACKEND_DEVELOPER", "Backend", null,
                "WORKSPACE", null, workspaceId, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(RateCardErrorCatalog.COST_ROLE_CODE_ALREADY_EXISTS.code());
                });
    }

    @Test
    void archive_inUse_rejected() {
        CostRole role = CostRole.create("QA_ENGINEER", "QA", null, CostRoleScope.SYSTEM, null, null, "QUALITY", true);
        when(costRoleRepository.findById(role.id())).thenReturn(Optional.of(role));
        when(costRoleRepository.isReferencedByRateLines(role.id())).thenReturn(true);

        assertThatThrownBy(() -> archiveAction.execute(new ArchiveCostRoleCommand(role.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.COST_ROLE_IN_USE.code()));
    }

    @Test
    void domain_activateDeactivateArchive() {
        CostRole role = CostRole.createSystemBuiltIn("TECH_LEAD", "Tech Lead", "ENGINEERING");
        assertThat(role.status()).isEqualTo(CostRoleStatus.ACTIVE);
        assertThat(role.deactivate().status()).isEqualTo(CostRoleStatus.INACTIVE);
        assertThat(role.archive(UUID.randomUUID()).status()).isEqualTo(CostRoleStatus.ARCHIVED);
        assertThat(role.builtIn()).isTrue();
    }

    private Workspace activeWorkspace() {
        return Workspace.create(UUID.randomUUID(), "WS", WorkspaceCode.of("WS01"), null, null,
                WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY);
    }
}
