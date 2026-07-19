package com.company.scopery.platform.security;

import com.company.scopery.common.constant.ApiPaths;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RateLimitFilter.class, WebMvcConfig.class, AiAgentSecurityInterceptor.class,
                com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter.class
        })
})
@Import({SecurityConfig.class, JwtAuthFilter.class, SecurityWebMvcTestConfig.class, CorsProperties.class})
class CorsSecurityIT {

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";
    private static final String DISALLOWED_ORIGIN = "https://evil.example";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    void preflight_fromAllowedOrigin_allowsCredentials() throws Exception {
        mockMvc.perform(options(ApiPaths.HEALTH)
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void getHealth_fromAllowedOrigin_echoesOriginAndCredentials() throws Exception {
        mockMvc.perform(get(ApiPaths.HEALTH)
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void getHealth_fromDisallowedOrigin_isRejectedWithoutAllowOrigin() throws Exception {
        mockMvc.perform(get(ApiPaths.HEALTH)
                        .header(HttpHeaders.ORIGIN, DISALLOWED_ORIGIN))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
