package com.company.scopery.modules.clientportal.portalsupport.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_PROJECTS + "/{projectId}/support/cases")
@Tag(name = "Client Portal - Support")
public class PortalSupportCaseController {
    private final SupportCaseRepository cases; private final ProjectRepository projects;
    public PortalSupportCaseController(SupportCaseRepository cases, ProjectRepository projects) {
        this.cases=cases; this.projects=projects;
    }
    @PostMapping @Operation(summary="Submit support case from portal")
    public ApiResponse<Map<String,Object>> create(@PathVariable UUID projectId, @RequestBody Map<String,Object> body) {
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        var saved = cases.save(SupportCase.create(project.workspaceId(), projectId,
                Objects.toString(body.get("requestTypeCode"), "QUESTION"),
                Objects.toString(body.get("priority"), "NORMAL"),
                Objects.toString(body.get("title"), "Portal request"),
                "PORTAL_SUBMISSION", true));
        return ApiResponse.success(Map.of("id", saved.id(), "caseNumber", saved.caseNumber(), "status", saved.status(),
                "portalVisible", saved.portalVisible(), "source", saved.source()));
    }
    @GetMapping @Operation(summary="List portal-visible support cases for project")
    public ApiResponse<List<Map<String,Object>>> list(@PathVariable UUID projectId) {
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return ApiResponse.success(cases.findByWorkspaceId(project.workspaceId()).stream()
                .filter(c -> projectId.equals(c.projectId()) && c.portalVisible())
                .map(c -> Map.<String,Object>of("id", c.id(), "caseNumber", c.caseNumber(), "status", c.status(),
                        "title", c.title(), "portalVisible", c.portalVisible())).toList());
    }
}
