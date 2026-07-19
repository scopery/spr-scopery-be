package com.company.scopery.modules.resourcereference.batchresolve.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcereference.batchresolve.application.action.BatchResolveAction;
import com.company.scopery.modules.resourcereference.batchresolve.application.command.BatchResolveCommand;
import com.company.scopery.modules.resourcereference.batchresolve.application.response.ResolvedResourceResponse;
import com.company.scopery.modules.resourcereference.batchresolve.http.request.BatchResolveRequest;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Resource Reference - Batch Resolve")
public class BatchResolveController {

    private final BatchResolveAction batchResolve;

    public BatchResolveController(BatchResolveAction batchResolve) {
        this.batchResolve = batchResolve;
    }

    @PostMapping(ResourceReferenceApiPaths.BATCH_RESOLVE)
    @Operation(summary = "Batch resolve up to 200 resource references")
    public ApiResponse<List<ResolvedResourceResponse>> resolve(@Valid @RequestBody BatchResolveRequest r) {
        List<BatchResolveCommand.ResourceRef> refs = r.refs().stream()
                .map(item -> new BatchResolveCommand.ResourceRef(item.resourceType(), item.resourceId()))
                .toList();
        return ApiResponse.success(batchResolve.execute(new BatchResolveCommand(refs)));
    }
}
