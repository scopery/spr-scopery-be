package com.company.scopery.platform.security.support;

import com.company.scopery.common.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Minimal protected write endpoint for CSRF / auth security contract tests.
 */
@RestController
@RequestMapping("/api/projects")
public class SecurityProbeProjectController {

    @PostMapping
    public ApiResponse<String> create() {
        return ApiResponse.success("reached");
    }
}
