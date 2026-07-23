package com.company.scopery.modules.traceability.nfrscope.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.nfrscope.application.action.LinkNfrScopeTargetAction;
import com.company.scopery.modules.traceability.nfrscope.application.action.UnlinkNfrScopeTargetAction;
import com.company.scopery.modules.traceability.nfrscope.application.command.LinkNfrScopeTargetCommand;
import com.company.scopery.modules.traceability.nfrscope.application.response.NfrScopeTargetResponse;
import com.company.scopery.modules.traceability.nfrscope.application.service.NfrScopeTargetQueryService;
import com.company.scopery.modules.traceability.nfrscope.domain.enums.NfrTargetType;
import com.company.scopery.modules.traceability.nfrscope.http.request.LinkNfrScopeTargetRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.NFR_SCOPE_TARGETS)
@Tag(name = "Traceability - NFR Scope Targets")
public class NfrScopeTargetController {

    private final LinkNfrScopeTargetAction link;
    private final UnlinkNfrScopeTargetAction unlink;
    private final NfrScopeTargetQueryService query;

    public NfrScopeTargetController(LinkNfrScopeTargetAction link,
                                     UnlinkNfrScopeTargetAction unlink,
                                     NfrScopeTargetQueryService query) {
        this.link = link;
        this.unlink = unlink;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Link a scope target (module/function/screen) to an NFR")
    public ApiResponse<NfrScopeTargetResponse> link(@PathVariable UUID projectId,
                                                     @PathVariable UUID nfrId,
                                                     @Valid @RequestBody LinkNfrScopeTargetRequest r) {
        NfrTargetType targetType = TraceabilityEnumParser.parseRequired(NfrTargetType.class, r.targetType(), "targetType");
        return ApiResponse.success(link.execute(new LinkNfrScopeTargetCommand(projectId, nfrId, r.targetId(), targetType)));
    }

    @GetMapping
    @Operation(summary = "List all scope targets linked to an NFR")
    public ApiResponse<List<NfrScopeTargetResponse>> list(@PathVariable UUID projectId,
                                                           @PathVariable UUID nfrId) {
        return ApiResponse.success(query.listByNfr(nfrId));
    }

    @DeleteMapping("/{targetId}")
    @Operation(summary = "Unlink a scope target from an NFR")
    public ApiResponse<Void> unlink(@PathVariable UUID projectId,
                                     @PathVariable UUID nfrId,
                                     @PathVariable UUID targetId) {
        unlink.execute(projectId, nfrId, targetId);
        return ApiResponse.success(null);
    }
}
