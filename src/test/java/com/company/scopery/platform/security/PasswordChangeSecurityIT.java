package com.company.scopery.platform.security;

import com.company.scopery.modules.iam.user.application.action.ChangePasswordAction;
import com.company.scopery.modules.iam.user.application.action.ConfirmPasswordResetAction;
import com.company.scopery.modules.iam.user.application.action.LoginAction;
import com.company.scopery.modules.iam.user.application.action.LogoutAction;
import com.company.scopery.modules.iam.user.application.action.RefreshTokenAction;
import com.company.scopery.modules.iam.user.application.action.RequestPasswordResetAction;
import com.company.scopery.modules.iam.user.application.action.RevokeAllSessionsAction;
import com.company.scopery.modules.iam.user.http.controller.IamAuthController;
import com.company.scopery.platform.config.SecurityConfig;
import com.company.scopery.platform.config.WebMvcConfig;
import com.company.scopery.platform.security.support.SecurityWebMvcTestConfig;
import com.company.scopery.platform.web.RateLimitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IamAuthController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RateLimitFilter.class, WebMvcConfig.class, AiAgentSecurityInterceptor.class, com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter.class
        })
})
@Import({SecurityConfig.class, JwtAuthFilter.class, SecurityWebMvcTestConfig.class, com.company.scopery.platform.security.CorsProperties.class})
class PasswordChangeSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private JwtProperties jwtProperties;
    @MockBean
    private LoginAction loginAction;
    @MockBean
    private RefreshTokenAction refreshTokenAction;
    @MockBean
    private LogoutAction logoutAction;
    @MockBean
    private ChangePasswordAction changePasswordAction;
    @MockBean
    private RequestPasswordResetAction requestPasswordResetAction;
    @MockBean
    private ConfirmPasswordResetAction confirmPasswordResetAction;
    @MockBean
    private RevokeAllSessionsAction revokeAllSessionsAction;

    @Test
    void passwordChange_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post(SecurityPathPolicy.IAM_AUTH_PASSWORD_CHANGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"old","newPassword":"new-password-1"}
                                """))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 401 && status != 403) {
                        throw new AssertionError("Expected 401 or 403 but was " + status);
                    }
                });
    }

    @Test
    void passwordChange_withAuthButNoCsrf_isBlocked() throws Exception {
        when(jwtService.isTokenValid("valid-access")).thenReturn(true);
        when(jwtService.extractUsername("valid-access")).thenReturn("admin");

        mockMvc.perform(post(SecurityPathPolicy.IAM_AUTH_PASSWORD_CHANGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"old","newPassword":"new-password-1"}
                                """)
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN_COOKIE, "valid-access")))
                .andExpect(status().isForbidden());
    }
}
