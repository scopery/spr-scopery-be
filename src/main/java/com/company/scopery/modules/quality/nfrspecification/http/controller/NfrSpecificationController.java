package com.company.scopery.modules.quality.nfrspecification.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.nfrspecification.application.action.*;
import com.company.scopery.modules.quality.nfrspecification.application.command.*;
import com.company.scopery.modules.quality.nfrspecification.application.response.*;
import com.company.scopery.modules.quality.nfrspecification.application.service.NfrSpecificationQueryService;
import com.company.scopery.modules.quality.nfrspecification.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Quality - NFR Specification")
public class NfrSpecificationController {
    private final SaveNfrSpecificationAction saveSpec;
    private final ManageNfrTargetsAction manageTargets;
    private final NfrSpecificationQueryService query;
    public NfrSpecificationController(SaveNfrSpecificationAction saveSpec,
            ManageNfrTargetsAction manageTargets, NfrSpecificationQueryService query) {
        this.saveSpec = saveSpec; this.manageTargets = manageTargets; this.query = query;
    }
    @GetMapping(QualityApiPaths.NFR_SPECIFICATION) @Operation(summary = "Get NFR specification")
    public ApiResponse<NfrSpecificationResponse> getSpec(@PathVariable UUID projectId,
            @PathVariable UUID requirementId) {
        return ApiResponse.success(query.getSpecification(projectId, requirementId));
    }
    @PutMapping(QualityApiPaths.NFR_SPECIFICATION) @Operation(summary = "Save NFR specification")
    public ApiResponse<NfrSpecificationResponse> saveSpec(@PathVariable UUID projectId,
            @PathVariable UUID requirementId, @Valid @RequestBody SaveNfrSpecificationRequest r) {
        return ApiResponse.success(saveSpec.execute(new SaveNfrSpecificationCommand(
                projectId, requirementId, r.qualityAttribute(), r.metricName(),
                r.comparisonOperator(), r.targetValue(), r.secondaryTargetValue(),
                r.unit(), r.measurementWindow(), r.environment(),
                r.verificationFrequency(), r.configurationJson())));
    }
    @GetMapping(QualityApiPaths.NFR_TARGETS) @Operation(summary = "Get NFR targets")
    public ApiResponse<NfrTargetResponse.ListResponse> getTargets(@PathVariable UUID projectId,
            @PathVariable UUID requirementId) {
        return ApiResponse.success(query.getTargets(projectId, requirementId));
    }
    @PutMapping(QualityApiPaths.NFR_TARGETS) @Operation(summary = "Replace NFR targets")
    public ApiResponse<NfrTargetResponse.ListResponse> manageTargets(@PathVariable UUID projectId,
            @PathVariable UUID requirementId, @RequestBody ManageNfrTargetsRequest r) {
        List<ManageNfrTargetsCommand.TargetItem> items = r.targets() == null ? List.of() :
                r.targets().stream()
                .map(t -> new ManageNfrTargetsCommand.TargetItem(t.targetType(), t.targetId(), t.targetLabel(), t.displayOrder()))
                .toList();
        return ApiResponse.success(manageTargets.execute(new ManageNfrTargetsCommand(projectId, requirementId, items)));
    }
}
