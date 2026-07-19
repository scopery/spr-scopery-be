package com.company.scopery.modules.clientportal.auth.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.auth.application.action.*;
import com.company.scopery.modules.clientportal.auth.application.response.*;
import com.company.scopery.modules.clientportal.auth.application.service.PortalAuthQueryService;
import com.company.scopery.modules.clientportal.auth.http.request.*;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_AUTH)
@Tag(name = "Client Portal - Auth")
public class PortalAuthController {
    private final AcceptPortalInviteAction acceptInvite;
    private final PortalLoginAction login;
    private final RefreshPortalTokenAction refresh;
    private final ChangePortalPasswordAction changePassword;
    private final PortalAuthQueryService query;
    public PortalAuthController(AcceptPortalInviteAction acceptInvite, PortalLoginAction login, RefreshPortalTokenAction refresh,
                                ChangePortalPasswordAction changePassword, PortalAuthQueryService query) {
        this.acceptInvite=acceptInvite; this.login=login; this.refresh=refresh; this.changePassword=changePassword; this.query=query;
    }
    @PostMapping("/accept-invite") @Operation(summary = "Accept portal invite and activate account")
    public ApiResponse<PortalAuthResult> acceptInvite(@Valid @RequestBody AcceptPortalInviteRequest r) {
        return ApiResponse.success(acceptInvite.execute(r.inviteToken(), r.password(), r.displayName()));
    }
    @PostMapping("/login") @Operation(summary = "Portal login")
    public ApiResponse<PortalAuthResult> login(@Valid @RequestBody PortalLoginRequest r) {
        return ApiResponse.success(login.execute(r.workspaceId(), r.email(), r.password()));
    }
    @PostMapping("/logout") @Operation(summary = "Portal logout (client discards token)")
    public ApiResponse<Void> logout() { return ApiResponse.success(null); }
    @PostMapping("/refresh") @Operation(summary = "Portal token refresh")
    public ApiResponse<PortalAuthResult> refresh() { return ApiResponse.success(refresh.execute()); }
    @GetMapping("/me") @Operation(summary = "Current portal account")
    public ApiResponse<PortalMeResponse> me() { return ApiResponse.success(query.me()); }
    @PostMapping("/password") @Operation(summary = "Change portal password")
    public ApiResponse<PortalMeResponse> changePassword(@Valid @RequestBody ChangePortalPasswordRequest r) {
        return ApiResponse.success(changePassword.execute(r.currentPassword(), r.newPassword()));
    }
}
