package com.company.scopery.modules.clientportal.review.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.review.application.action.*; import com.company.scopery.modules.clientportal.review.application.command.CreateClientReviewRequestCommand;
import com.company.scopery.modules.clientportal.review.application.command.DecideClientReviewCommand;
import com.company.scopery.modules.clientportal.review.application.response.ClientReviewRequestResponse; import com.company.scopery.modules.clientportal.review.application.service.ClientReviewRequestQueryService;
import com.company.scopery.modules.clientportal.review.http.request.*; import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ClientPortalApiPaths.REVIEW_REQUESTS) @Tag(name="Client Portal - Reviews")
public class ClientReviewRequestController {
    private final CreateClientReviewRequestAction create; private final DecideClientReviewAction decide; private final ClientReviewRequestQueryService query;
    public ClientReviewRequestController(CreateClientReviewRequestAction create, DecideClientReviewAction decide, ClientReviewRequestQueryService query){this.create=create;this.decide=decide;this.query=query;}
    @PostMapping @Operation(summary="Create client review request")
    public ApiResponse<ClientReviewRequestResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateClientReviewRequestRequest r) {
        return ApiResponse.success(create.execute(new CreateClientReviewRequestCommand(projectId, r.targetType(), r.targetId(), r.title(), r.assignedPortalAccountId())));
    }
    @GetMapping @Operation(summary="List client reviews")
    public ApiResponse<List<ClientReviewRequestResponse>> list(@PathVariable UUID projectId){return ApiResponse.success(query.list(projectId));}
    @GetMapping("/{reviewId}") @Operation(summary="Get client review")
    public ApiResponse<ClientReviewRequestResponse> get(@PathVariable UUID projectId, @PathVariable UUID reviewId){return ApiResponse.success(query.get(projectId, reviewId));}
    @PostMapping("/{reviewId}/decide") @Operation(summary="Decide client review")
    public ApiResponse<ClientReviewRequestResponse> decide(@PathVariable UUID projectId, @PathVariable UUID reviewId, @Valid @RequestBody DecideClientReviewRequest r) {
        return ApiResponse.success(decide.execute(new DecideClientReviewCommand(projectId, reviewId, r.decision(), r.comment())));
    }
}
