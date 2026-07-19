package com.company.scopery.platform.security;

import com.company.scopery.modules.iam.me.application.query.GetMeQuery;
import com.company.scopery.modules.iam.me.application.response.MeResponse;
import com.company.scopery.modules.iam.me.application.service.MeQueryService;
import com.company.scopery.modules.iam.me.http.controller.MeController;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MeController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RateLimitFilter.class, WebMvcConfig.class, AiAgentSecurityInterceptor.class, com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter.class
        })
})
@Import({SecurityConfig.class, JwtAuthFilter.class, SecurityWebMvcTestConfig.class, com.company.scopery.platform.security.CorsProperties.class})
class ProtectedEndpointSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private MeQueryService meQueryService;

    @Test
    void getMe_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get(IamApiPaths.IAM_ME))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }

    @Test
    void getMe_afterValidAccessCookie_returns200() throws Exception {
        when(jwtService.isTokenValid("valid-access")).thenReturn(true);
        when(jwtService.extractUsername("valid-access")).thenReturn("admin");
        when(meQueryService.getMe(any(GetMeQuery.class))).thenReturn(new MeResponse(
                UUID.randomUUID(), "admin", "admin@example.com", "Admin", "ACTIVE",
                List.of(), new MeResponse.SecurityState(false, false), Instant.now()));

        mockMvc.perform(get(IamApiPaths.IAM_ME)
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN_COOKIE, "valid-access")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }
}
