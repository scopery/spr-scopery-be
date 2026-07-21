package com.company.scopery.platform.config;

import com.company.scopery.common.constant.ApiPaths;
import com.company.scopery.common.response.ErrorResponse;
import com.company.scopery.platform.security.CorsProperties;
import com.company.scopery.platform.security.JwtAuthFilter;
import com.company.scopery.platform.security.SecurityPathPolicy;
import com.company.scopery.platform.security.SwaggerAccessPolicy;
import com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;
    private final IdempotencyKeyFilter idempotencyKeyFilter;
    private final CorsProperties corsProperties;
    private final SwaggerAccessPolicy swaggerAccessPolicy;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, ObjectMapper objectMapper,
                          IdempotencyKeyFilter idempotencyKeyFilter,
                          CorsProperties corsProperties,
                          SwaggerAccessPolicy swaggerAccessPolicy) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.objectMapper = objectMapper;
        this.idempotencyKeyFilter = idempotencyKeyFilter;
        this.corsProperties = corsProperties;
        this.swaggerAccessPolicy = swaggerAccessPolicy;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        corsProperties.validateForCredentialedRequests();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowCredentials(corsProperties.isAllowCredentials());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setExposedHeaders(corsProperties.getExposedHeaders());
        config.setMaxAge(corsProperties.getMaxAgeSeconds());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                // CSRF — Double Submit Cookie pattern (JS reads XSRF-TOKEN cookie, sends as X-XSRF-TOKEN header)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers(SecurityPathPolicy.csrfIgnoredMatchers()))
                // Force deferred CSRF token to materialise on every response so the cookie is always set
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        .requestMatchers(HttpMethod.GET, SecurityPathPolicy.healthPath()).permitAll()
                        .requestMatchers(swaggerAccessPolicy.infraPublicPaths().toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                SecurityPathPolicy.publicAuthPostPaths().toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.IAM_USERS).permitAll()
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
