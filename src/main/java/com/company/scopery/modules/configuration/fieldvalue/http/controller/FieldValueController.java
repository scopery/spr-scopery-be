package com.company.scopery.modules.configuration.fieldvalue.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.fieldvalue.application.action.UpsertFieldValuesAction;
import com.company.scopery.modules.configuration.fieldvalue.application.command.UpsertFieldValuesCommand;
import com.company.scopery.modules.configuration.fieldvalue.application.response.CustomFieldValueResponse;
import com.company.scopery.modules.configuration.fieldvalue.application.service.FieldValueQueryService;
import com.company.scopery.modules.configuration.fieldvalue.http.request.UpsertFieldValuesRequest;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.FIELD_VALUES) @Tag(name = "Configuration - Field Values")
public class FieldValueController {
    private final UpsertFieldValuesAction upsert; private final FieldValueQueryService query;
    public FieldValueController(UpsertFieldValuesAction upsert, FieldValueQueryService query) { this.upsert=upsert; this.query=query; }
    @PutMapping @Operation(summary="Upsert custom field values")
    public ApiResponse<List<CustomFieldValueResponse>> upsert(@PathVariable UUID workspaceId, @Valid @RequestBody UpsertFieldValuesRequest r) {
        return ApiResponse.success(upsert.execute(new UpsertFieldValuesCommand(workspaceId, r.objectType(), r.targetId(), r.values().stream().map(i -> new UpsertFieldValuesCommand.FieldValueEntry(i.fieldId(), i.valueText(), i.valueLongText(), i.valueNumber(), i.valueDecimal(), i.valueBoolean(), i.valueDate(), i.valueDatetime(), i.valueJson(), i.valueOptionIds())).toList())));
    }
    @GetMapping @Operation(summary="List custom field values")
    public ApiResponse<List<CustomFieldValueResponse>> list(@PathVariable UUID workspaceId, @RequestParam String objectType, @RequestParam UUID targetId) {
        return ApiResponse.success(query.list(workspaceId, objectType, targetId));
    }
}
