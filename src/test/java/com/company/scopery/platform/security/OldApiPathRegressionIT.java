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
import com.company.scopery.platform.web.HealthController;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {IamAuthController.class, HealthController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RateLimitFilter.class, WebMvcConfig.class, AiAgentSecurityInterceptor.class, com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter.class
        })
})
@Import({SecurityConfig.class, JwtAuthFilter.class, SecurityWebMvcTestConfig.class, com.company.scopery.platform.security.CorsProperties.class})
class OldApiPathRegressionIT {

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
    void postApiV1Login_isNotSuccessfulLogin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/iam/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"secret"}
                                """))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(200);
        String setCookies = String.join(",", result.getResponse().getHeaders("Set-Cookie"));
        assertThat(setCookies).doesNotContain(CookieUtil.ACCESS_TOKEN_COOKIE + "=");
    }

    @Test
    void postApiV2Login_isNotSuccessfulLogin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v2/iam/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"secret"}
                                """))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(200);
        String setCookies = String.join(",", result.getResponse().getHeaders("Set-Cookie"));
        assertThat(setCookies).doesNotContain(CookieUtil.ACCESS_TOKEN_COOKIE + "=");
    }

    @Test
    void getApiV1Health_isNotSuccessfulHealth() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getApiV2Health_isNotSuccessfulHealth() throws Exception {
        mockMvc.perform(get("/api/v2/health"))
                .andExpect(status().isUnauthorized());
    }
}
