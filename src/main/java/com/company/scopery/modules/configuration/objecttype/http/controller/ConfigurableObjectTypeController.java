package com.company.scopery.modules.configuration.objecttype.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.objecttype.application.response.ConfigurableObjectTypeResponse;
import com.company.scopery.modules.configuration.objecttype.application.service.ConfigurableObjectTypeQueryService;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping(ConfigurationApiPaths.OBJECT_TYPES) @Tag(name = "Configuration - Object Types")
public class ConfigurableObjectTypeController {
    private final ConfigurableObjectTypeQueryService query;
    public ConfigurableObjectTypeController(ConfigurableObjectTypeQueryService query) { this.query = query; }
    @GetMapping @Operation(summary="List configurable object types")
    public ApiResponse<List<ConfigurableObjectTypeResponse>> list() {
        return ApiResponse.success(query.listEnabled());
    }
}
