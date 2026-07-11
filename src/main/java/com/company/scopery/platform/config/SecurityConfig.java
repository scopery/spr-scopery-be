package com.company.scopery.platform.config;

import com.company.scopery.common.constant.ApiPaths;
import com.company.scopery.common.response.ErrorResponse;
import com.company.scopery.platform.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.io.IOException;
import java.time.Instant;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            ApiPaths.IAM_AUTH + "/**",
            ApiPaths.HEALTH,
            "/actuator/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper  objectMapper;
    private final IdempotencyKeyFilter idempotencyKeyFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, ObjectMapper objectMapper,
                          IdempotencyKeyFilter idempotencyKeyFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.objectMapper  = objectMapper;
        this.idempotencyKeyFilter = idempotencyKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF — Double Submit Cookie pattern (JS reads XSRF-TOKEN cookie, sends as X-XSRF-TOKEN header)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers(ApiPaths.IAM_AUTH + "/**")
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher(ApiPaths.IAM_AUTH_PASSWORD_RESET_REQUEST, HttpMethod.POST.name()),
                                new AntPathRequestMatcher(ApiPaths.IAM_AUTH_PASSWORD_RESET_CONFIRM, HttpMethod.POST.name()))
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher(ApiPaths.IAM_USERS, HttpMethod.POST.name())))
                // Force deferred CSRF token to materialise on every response so the cookie is always set
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.IAM_USERS).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                ApiPaths.IAM_AUTH_PASSWORD_RESET_REQUEST,
                                ApiPaths.IAM_AUTH_PASSWORD_RESET_CONFIRM).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                writeUnauthorized(response, request.getRequestURI())))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(idempotencyKeyFilter, JwtAuthFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<IdempotencyKeyFilter> disableIdempotencyServletRegistration(
            IdempotencyKeyFilter filter) {
        FilterRegistrationBean<IdempotencyKeyFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    private void writeUnauthorized(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "UNAUTHORIZED",
                "Authentication required",
                null,
                path,
                null,
                Instant.now().toString());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    static final class CsrfCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
            if (csrf != null) csrf.getToken(); // triggers deferred token → writes Set-Cookie: XSRF-TOKEN
            filterChain.doFilter(request, response);
        }
    }
}
