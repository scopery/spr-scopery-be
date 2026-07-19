package com.company.scopery.platform.security;

import com.company.scopery.common.constant.ApiPaths;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.action.ChangePasswordAction;
import com.company.scopery.modules.iam.user.application.action.ConfirmPasswordResetAction;
import com.company.scopery.modules.iam.user.application.action.LoginAction;
import com.company.scopery.modules.iam.user.application.action.LogoutAction;
import com.company.scopery.modules.iam.user.application.action.RefreshTokenAction;
import com.company.scopery.modules.iam.user.application.action.RequestPasswordResetAction;
import com.company.scopery.modules.iam.user.application.action.RevokeAllSessionsAction;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.iam.user.http.controller.IamAuthController;
import com.company.scopery.platform.config.SecurityConfig;
import com.company.scopery.platform.config.WebMvcConfig;
import com.company.scopery.platform.security.support.SecurityWebMvcTestConfig;
import com.company.scopery.platform.web.GlobalExceptionHandler;
import com.company.scopery.platform.web.RateLimitFilter;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IamAuthController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RateLimitFilter.class, WebMvcConfig.class, AiAgentSecurityInterceptor.class, com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter.class
        })
})
@Import({SecurityConfig.class, JwtAuthFilter.class, SecurityWebMvcTestConfig.class, com.company.scopery.platform.security.CorsProperties.class, GlobalExceptionHandler.class})
class AuthPathIT {

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

    @BeforeEach
    void setUpJwtProperties() {
        when(jwtProperties.getExpirationMs()).thenReturn(900_000L);
        when(jwtProperties.getRefreshExpirationMs()).thenReturn(604_800_000L);
        when(jwtProperties.isCookieSecure()).thenReturn(false);
    }

    @Test
    void login_validCredentials_setsAccessAndRefreshCookies() throws Exception {
        IamUser user = sampleUser();
        when(loginAction.execute(any())).thenReturn(new AuthResult(user, "access-token", "refresh-token"));

        MvcResult result = mockMvc.perform(post(ApiPaths.IAM_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"secret"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String setCookies = String.join(",", result.getResponse().getHeaders("Set-Cookie"));
        assertThat(setCookies).contains(CookieUtil.ACCESS_TOKEN_COOKIE + "=access-token");
        assertThat(setCookies).contains("Path=/");
        assertThat(setCookies).contains(CookieUtil.REFRESH_TOKEN_COOKIE + "=refresh-token");
        assertThat(setCookies).contains("Path=" + ApiPaths.IAM_AUTH);
        assertThat(setCookies.toLowerCase()).contains("httponly");
    }

    @Test
    void login_invalidCredentials_returnsGenericFailure() throws Exception {
        when(loginAction.execute(any())).thenThrow(IamExceptions.invalidCredentials());

        mockMvc.perform(post(ApiPaths.IAM_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_withValidRefreshCookie_rotatesCookies() throws Exception {
        IamUser user = sampleUser();
        when(refreshTokenAction.execute(any())).thenReturn(new AuthResult(user, "new-access", "new-refresh"));

        MvcResult result = mockMvc.perform(post(ApiPaths.IAM_AUTH + "/refresh")
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.REFRESH_TOKEN_COOKIE, "old-refresh")))
                .andExpect(status().isOk())
                .andReturn();

        String setCookies = String.join(",", result.getResponse().getHeaders("Set-Cookie"));
        assertThat(setCookies).contains(CookieUtil.ACCESS_TOKEN_COOKIE + "=new-access");
        assertThat(setCookies).contains(CookieUtil.REFRESH_TOKEN_COOKIE + "=new-refresh");
        assertThat(setCookies).contains("Path=" + ApiPaths.IAM_AUTH);
    }

    @Test
    void logout_clearsCookiesWithCorrectPaths() throws Exception {
        doNothing().when(logoutAction).execute(any());

        MvcResult result = mockMvc.perform(post(ApiPaths.IAM_AUTH + "/logout")
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.REFRESH_TOKEN_COOKIE, "refresh")))
                .andExpect(status().isOk())
                .andReturn();

        String setCookies = String.join(",", result.getResponse().getHeaders("Set-Cookie"));
        assertThat(setCookies).contains(CookieUtil.ACCESS_TOKEN_COOKIE + "=");
        assertThat(setCookies).contains(CookieUtil.REFRESH_TOKEN_COOKIE + "=");
        assertThat(setCookies).contains("Max-Age=0");
        assertThat(setCookies).contains("Path=" + ApiPaths.IAM_AUTH);
        assertThat(setCookies).contains("Path=/");
    }

    private static IamUser sampleUser() {
        Instant now = Instant.now();
        return IamUser.of(
                UUID.randomUUID(),
                Username.of("admin"),
                EmailAddress.of("admin@example.com"),
                "Admin",
                "hash",
                IamUserStatus.ACTIVE,
                now,
                now);
    }
}
