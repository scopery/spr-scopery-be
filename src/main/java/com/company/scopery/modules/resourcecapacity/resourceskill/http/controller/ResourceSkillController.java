package com.company.scopery.modules.resourcecapacity.resourceskill.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.resourceskill.application.action.CreateResourceSkillAction;
import com.company.scopery.modules.resourcecapacity.resourceskill.application.response.ResourceSkillResponse;
import com.company.scopery.modules.resourcecapacity.resourceskill.application.service.ResourceSkillQueryService;
import com.company.scopery.modules.resourcecapacity.resourceskill.http.request.CreateResourceSkillRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Resource Capacity - ResourceSkills")
public class ResourceSkillController {
    private final CreateResourceSkillAction create; private final ResourceSkillQueryService query;
    public ResourceSkillController(CreateResourceSkillAction create, ResourceSkillQueryService query) { this.create=create; this.query=query; }
    @PostMapping(CapacityApiPaths.SKILLS) @Operation(summary="Create ResourceSkill")
    public ApiResponse<ResourceSkillResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateResourceSkillRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r));
    }
    @GetMapping(CapacityApiPaths.SKILLS) @Operation(summary="List ResourceSkills")
    public ApiResponse<List<ResourceSkillResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
