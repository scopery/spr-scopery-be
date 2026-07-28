package com.company.scopery.modules.iam.authorization.application.service;

import com.company.scopery.modules.iam.authorization.application.response.CapabilitiesResponse;
import com.company.scopery.modules.iam.authorization.domain.enums.NavCapabilityPack;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.authorization.domain.nav.NavCapabilityDefinitions;
import com.company.scopery.modules.iam.authorization.domain.nav.NavCapabilityDefinitions.Gate;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NavCapabilitiesQueryService {

    private final CurrentUserAuthorizationService currentUserService;
    private final AuthorizationDecisionService decisionService;
    private final IamAuthResourceRepository authResourceRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;

    public NavCapabilitiesQueryService(
            CurrentUserAuthorizationService currentUserService,
            AuthorizationDecisionService decisionService,
            IamAuthResourceRepository authResourceRepository,
            WorkspaceRepository workspaceRepository,
            ProjectRepository projectRepository) {
        this.currentUserService = currentUserService;
        this.decisionService = decisionService;
        this.authResourceRepository = authResourceRepository;
        this.workspaceRepository = workspaceRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public CapabilitiesResponse forWorkspace(UUID workspaceId, NavCapabilityPack pack) {
        return forWorkspace(workspaceId, pack, null);
    }

    @Transactional(readOnly = true)
    public CapabilitiesResponse forWorkspace(UUID workspaceId, NavCapabilityPack pack, UUID projectId) {
        if (pack == NavCapabilityPack.NAV_ORG) {
            throw new IllegalArgumentException("NAV_ORG must be requested on an organization");
        }
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamAuthResource workspaceResource = authResourceRepository
                .findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(workspaceId));

        if (pack == NavCapabilityPack.NAV_PROJECT && projectId != null) {
            return forNavProject(actorId, workspaceId, workspaceResource, projectId);
        }

        Map<String, Boolean> caps = evaluateGates(actorId, workspaceResource.id(), pack);

        if (pack == NavCapabilityPack.NAV_SETTINGS) {
            caps.put(NavCapabilityDefinitions.SETTINGS_PERSONAL, true);
            UUID orgId = workspaceRepository.findById(workspaceId)
                    .map(Workspace::organizationId)
                    .orElse(null);
            if (orgId != null) {
                caps.put(
                        "settings.organization_entry",
                        allowedOnRef(actorId, IamResourceType.ORGANIZATION, orgId, IamAuthorities.ORGANIZATION_MANAGE));
            } else {
                caps.put("settings.organization_entry", false);
            }
        }

        applyDerived(caps, pack);
        return new CapabilitiesResponse(IamResourceType.WORKSPACE.name(), workspaceId, pack.name(), caps);
    }

    private CapabilitiesResponse forNavProject(
            UUID actorId, UUID workspaceId, IamAuthResource workspaceResource, UUID projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null || !workspaceId.equals(project.workspaceId())) {
            throw IamExceptions.iamAuthResourceNotFound(projectId);
        }

        UUID projectResourceId = authResourceRepository
                .findByRefIdAndResourceType(projectId, IamResourceType.PROJECT)
                .map(IamAuthResource::id)
                .orElse(null);

        Map<String, Boolean> caps = new LinkedHashMap<>();
        for (Gate gate : NavCapabilityDefinitions.gatesFor(NavCapabilityPack.NAV_PROJECT)) {
            boolean allowed;
            if (projectResourceId != null) {
                // PROJECT resource present → authorize on project only (no workspace fallback)
                allowed = decisionService
                        .canAccess(new AuthorizationRequest(actorId, projectResourceId, gate.authority()))
                        .allowed();
            } else {
                allowed = decisionService
                        .canAccess(new AuthorizationRequest(actorId, workspaceResource.id(), gate.authority()))
                        .allowed();
            }
            caps.put(gate.key(), allowed);
        }
        applyDerived(caps, NavCapabilityPack.NAV_PROJECT);
        return new CapabilitiesResponse(
                projectResourceId != null ? IamResourceType.PROJECT.name() : IamResourceType.WORKSPACE.name(),
                projectResourceId != null ? projectId : workspaceId,
                NavCapabilityPack.NAV_PROJECT.name(),
                caps);
    }

    @Transactional(readOnly = true)
    public CapabilitiesResponse forOrganization(UUID organizationId, NavCapabilityPack pack) {
        if (pack != NavCapabilityPack.NAV_ORG && pack != NavCapabilityPack.NAV_SETTINGS) {
            throw new IllegalArgumentException("Organization capabilities only support NAV_ORG or NAV_SETTINGS");
        }
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamAuthResource resource = authResourceRepository
                .findByRefIdAndResourceType(organizationId, IamResourceType.ORGANIZATION)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(organizationId));

        Map<String, Boolean> caps;
        if (pack == NavCapabilityPack.NAV_ORG) {
            caps = evaluateGates(actorId, resource.id(), pack);
            applyDerived(caps, pack);
        } else {
            caps = NavCapabilityDefinitions.emptyFalseMap(pack);
            caps.put(NavCapabilityDefinitions.SETTINGS_PERSONAL, true);
            caps.put(
                    "settings.organization_entry",
                    decisionService
                            .canAccess(new AuthorizationRequest(actorId, resource.id(), IamAuthorities.ORGANIZATION_MANAGE))
                            .allowed());
        }
        return new CapabilitiesResponse(IamResourceType.ORGANIZATION.name(), organizationId, pack.name(), caps);
    }

    private Map<String, Boolean> evaluateGates(UUID actorId, UUID resourceId, NavCapabilityPack pack) {
        Map<String, Boolean> caps = new LinkedHashMap<>();
        for (Gate gate : NavCapabilityDefinitions.gatesFor(pack)) {
            boolean allowed = decisionService
                    .canAccess(new AuthorizationRequest(actorId, resourceId, gate.authority()))
                    .allowed();
            caps.put(gate.key(), allowed);
        }
        return caps;
    }

    private boolean allowedOnRef(
            UUID actorId, IamResourceType type, UUID refId, IamPermissionAction authority) {
        return authResourceRepository
                .findByRefIdAndResourceType(refId, type)
                .map(r -> decisionService.canAccess(new AuthorizationRequest(actorId, r.id(), authority)).allowed())
                .orElse(false);
    }

    private static void applyDerived(Map<String, Boolean> caps, NavCapabilityPack pack) {
        for (Map.Entry<String, List<String>> e : NavCapabilityDefinitions.derivedOrKeys(pack).entrySet()) {
            boolean any = false;
            for (String child : e.getValue()) {
                if (Boolean.TRUE.equals(caps.get(child))) {
                    any = true;
                    break;
                }
            }
            caps.put(e.getKey(), any);
        }
    }
}
