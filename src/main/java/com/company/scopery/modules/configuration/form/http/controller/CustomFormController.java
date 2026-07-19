package com.company.scopery.modules.configuration.form.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.form.application.action.*;
import com.company.scopery.modules.configuration.form.application.command.*;
import com.company.scopery.modules.configuration.form.application.response.*;
import com.company.scopery.modules.configuration.form.application.service.FormQueryService;
import com.company.scopery.modules.configuration.form.http.request.*;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.FORMS) @Tag(name = "Configuration - Forms")
public class CustomFormController {
    private final CreateFormAction createForm; private final CreateFormVersionAction createVersion; private final PublishFormVersionAction publish;
    private final AddFormSectionAction addSection; private final AddFormFieldAction addField; private final SubmitFormAction submit;
    private final FormQueryService query;
    public CustomFormController(CreateFormAction createForm, CreateFormVersionAction createVersion, PublishFormVersionAction publish,
                                AddFormSectionAction addSection, AddFormFieldAction addField, SubmitFormAction submit, FormQueryService query) {
        this.createForm=createForm; this.createVersion=createVersion; this.publish=publish; this.addSection=addSection; this.addField=addField;
        this.submit=submit; this.query=query;
    }
    @PostMapping @Operation(summary="Create form")
    public ApiResponse<CustomFormDefinitionResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateFormRequest r) {
        return ApiResponse.success(createForm.execute(new CreateFormCommand(workspaceId, r.formCode(), r.name(), r.objectTypeCode(), r.formType(), r.projectId())));
    }
    @GetMapping @Operation(summary="List forms")
    public ApiResponse<List<CustomFormDefinitionResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.listForms(workspaceId)); }
    @GetMapping("/{formId}") @Operation(summary="Get form")
    public ApiResponse<CustomFormDefinitionResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID formId) { return ApiResponse.success(query.getForm(workspaceId, formId)); }
    @PostMapping("/{formId}/versions") @Operation(summary="Create form version")
    public ApiResponse<CustomFormVersionResponse> createVersion(@PathVariable UUID workspaceId, @PathVariable UUID formId) {
        return ApiResponse.success(createVersion.execute(new CreateFormVersionCommand(workspaceId, formId)));
    }
    @GetMapping("/{formId}/versions") @Operation(summary="List form versions")
    public ApiResponse<List<CustomFormVersionResponse>> listVersions(@PathVariable UUID workspaceId, @PathVariable UUID formId) {
        return ApiResponse.success(query.listVersions(workspaceId, formId));
    }
    @PostMapping("/{formId}/versions/{versionId}/publish") @Operation(summary="Publish form version")
    public ApiResponse<CustomFormVersionResponse> publish(@PathVariable UUID workspaceId, @PathVariable UUID formId, @PathVariable UUID versionId) {
        return ApiResponse.success(publish.execute(new PublishFormVersionCommand(workspaceId, formId, versionId)));
    }
    @PostMapping("/{formId}/versions/{versionId}/sections") @Operation(summary="Add form section")
    public ApiResponse<CustomFormSectionResponse> addSection(@PathVariable UUID workspaceId, @PathVariable UUID formId, @PathVariable UUID versionId, @Valid @RequestBody CreateFormSectionRequest r) {
        return ApiResponse.success(addSection.execute(new AddFormSectionCommand(workspaceId, formId, versionId, r.title(), r.sortOrder())));
    }
    @GetMapping("/{formId}/versions/{versionId}/sections") @Operation(summary="List form sections")
    public ApiResponse<List<CustomFormSectionResponse>> listSections(@PathVariable UUID workspaceId, @PathVariable UUID formId, @PathVariable UUID versionId) {
        return ApiResponse.success(query.listSections(workspaceId, formId, versionId));
    }
    @PostMapping("/{formId}/versions/{versionId}/fields") @Operation(summary="Add form field")
    public ApiResponse<CustomFormFieldResponse> addField(@PathVariable UUID workspaceId, @PathVariable UUID formId, @PathVariable UUID versionId, @Valid @RequestBody CreateFormFieldRequest r) {
        return ApiResponse.success(addField.execute(new AddFormFieldCommand(workspaceId, formId, versionId, r.fieldSource(), r.sectionId(), r.customFieldDefinitionId(), r.coreFieldKey(), r.requiredOnForm(), r.readonlyFlag(), r.sortOrder())));
    }
    @GetMapping("/{formId}/versions/{versionId}/fields") @Operation(summary="List form fields")
    public ApiResponse<List<CustomFormFieldResponse>> listFields(@PathVariable UUID workspaceId, @PathVariable UUID formId, @PathVariable UUID versionId) {
        return ApiResponse.success(query.listFields(workspaceId, formId, versionId));
    }
    @PostMapping("/{formId}/submit") @Operation(summary="Submit form")
    public ApiResponse<FormSubmissionResponse> submit(@PathVariable UUID workspaceId, @PathVariable UUID formId, @Valid @RequestBody SubmitFormRequest r) {
        return ApiResponse.success(submit.execute(new SubmitFormCommand(workspaceId, formId, r.formVersionId(), r.objectTypeCode(), r.targetId(), r.projectId(), r.payloadJson())));
    }
}
