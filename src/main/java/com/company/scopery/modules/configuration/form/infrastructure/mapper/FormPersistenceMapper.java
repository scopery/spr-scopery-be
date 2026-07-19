package com.company.scopery.modules.configuration.form.infrastructure.mapper;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.form.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class FormPersistenceMapper {
    public CustomFormDefinition toDomain(CustomFormDefinitionJpaEntity e) {
        return new CustomFormDefinition(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getObjectTypeCode(), e.getFormCode(), e.getName(),
                e.getFormType(), e.getStatus(), e.getCurrentVersionId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFormDefinitionJpaEntity toJpa(CustomFormDefinition d) {
        CustomFormDefinitionJpaEntity e = new CustomFormDefinitionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setObjectTypeCode(d.objectTypeCode());
        e.setFormCode(d.formCode()); e.setName(d.name()); e.setFormType(d.formType()); e.setStatus(d.status());
        e.setCurrentVersionId(d.currentVersionId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public CustomFormVersion toDomain(CustomFormVersionJpaEntity e) {
        return new CustomFormVersion(e.getId(), e.getFormDefinitionId(), e.getWorkspaceId(), e.getVersionNumber(), e.getStatus(),
                e.getSchemaJson(), e.getPublishedAt(), e.isCurrentFlag(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFormVersionJpaEntity toJpa(CustomFormVersion d) {
        CustomFormVersionJpaEntity e = new CustomFormVersionJpaEntity();
        e.setId(d.id()); e.setFormDefinitionId(d.formDefinitionId()); e.setWorkspaceId(d.workspaceId()); e.setVersionNumber(d.versionNumber());
        e.setStatus(d.status()); e.setSchemaJson(d.schemaJson()); e.setPublishedAt(d.publishedAt()); e.setCurrentFlag(d.currentFlag());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public CustomFormSection toDomain(CustomFormSectionJpaEntity e) {
        return new CustomFormSection(e.getId(), e.getFormVersionId(), e.getTitle(), e.getSortOrder(), e.getVisibilityJson(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFormSectionJpaEntity toJpa(CustomFormSection d) {
        CustomFormSectionJpaEntity e = new CustomFormSectionJpaEntity();
        e.setId(d.id()); e.setFormVersionId(d.formVersionId()); e.setTitle(d.title()); e.setSortOrder(d.sortOrder());
        e.setVisibilityJson(d.visibilityJson()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public CustomFormField toDomain(CustomFormFieldJpaEntity e) {
        return new CustomFormField(e.getId(), e.getFormVersionId(), e.getSectionId(), e.getFieldSource(), e.getCustomFieldDefinitionId(),
                e.getCoreFieldKey(), e.getLabelOverride(), e.isRequiredOnForm(), e.isReadonlyFlag(), e.getSortOrder(), e.getMetadataJson(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFormFieldJpaEntity toJpa(CustomFormField d) {
        CustomFormFieldJpaEntity e = new CustomFormFieldJpaEntity();
        e.setId(d.id()); e.setFormVersionId(d.formVersionId()); e.setSectionId(d.sectionId()); e.setFieldSource(d.fieldSource());
        e.setCustomFieldDefinitionId(d.customFieldDefinitionId()); e.setCoreFieldKey(d.coreFieldKey()); e.setLabelOverride(d.labelOverride());
        e.setRequiredOnForm(d.requiredOnForm()); e.setReadonlyFlag(d.readonlyFlag()); e.setSortOrder(d.sortOrder()); e.setMetadataJson(d.metadataJson());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public FormSubmission toDomain(FormSubmissionJpaEntity e) {
        return new FormSubmission(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getFormDefinitionId(), e.getFormVersionId(),
                e.getObjectTypeCode(), e.getTargetId(), e.getPrincipalType(), e.getSubmittedBy(), e.getPayloadJson(),
                e.getValidationStatus(), e.getValidationErrorsJson(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public FormSubmissionJpaEntity toJpa(FormSubmission d) {
        FormSubmissionJpaEntity e = new FormSubmissionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setFormDefinitionId(d.formDefinitionId());
        e.setFormVersionId(d.formVersionId()); e.setObjectTypeCode(d.objectTypeCode()); e.setTargetId(d.targetId());
        e.setPrincipalType(d.principalType()); e.setSubmittedBy(d.submittedBy()); e.setPayloadJson(d.payloadJson());
        e.setValidationStatus(d.validationStatus()); e.setValidationErrorsJson(d.validationErrorsJson()); e.setStatus(d.status());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
