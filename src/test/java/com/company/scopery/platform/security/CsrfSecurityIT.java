package com.company.scopery.platform.security;

import com.company.scopery.platform.config.SecurityConfig;
import com.company.scopery.platform.config.WebMvcConfig;
import com.company.scopery.platform.security.support.SecurityProbeProjectController;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityProbeProjectController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RateLimitFilter.class, WebMvcConfig.class, AiAgentSecurityInterceptor.class, com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter.class
        })
})
@Import({SecurityConfig.class, JwtAuthFilter.class, SecurityWebMvcTestConfig.class, com.company.scopery.platform.security.CorsProperties.class})
class CsrfSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    void postProjects_withoutAuth_isBlocked() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 401 && status != 403) {
                        throw new AssertionError("Expected 401 or 403 but was " + status);
                    }
                });
    }

    @Test
    void postProjects_withAuthButNoCsrf_isBlocked() throws Exception {
        when(jwtService.isTokenValid("valid-access")).thenReturn(true);
        when(jwtService.extractUsername("valid-access")).thenReturn("admin");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN_COOKIE, "valid-access")))
                .andExpect(status().isForbidden());
    }

    @Test
    void postProjects_withAuthAndCsrf_reachesController() throws Exception {
        when(jwtService.isTokenValid("valid-access")).thenReturn(true);
        when(jwtService.extractUsername("valid-access")).thenReturn("admin");

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN_COOKIE, "valid-access")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("reached"));
    }
}
