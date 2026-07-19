package com.company.scopery.modules.documenthub.generatedjob.infrastructure.mapper;
import com.company.scopery.modules.documenthub.generatedjob.domain.enums.GeneratedDocumentJobStatus;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.GeneratedDocumentJob;
import com.company.scopery.modules.documenthub.generatedjob.infrastructure.persistence.GeneratedDocumentJobJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class GeneratedDocumentJobPersistenceMapper {
    public GeneratedDocumentJob toDomain(GeneratedDocumentJobJpaEntity e) {
        return new GeneratedDocumentJob(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getTemplateId(), e.getTemplateVersionId(), e.getJobType(),
                GeneratedDocumentJobStatus.valueOf(e.getStatus()), e.getSourceType(), e.getSourceId(), e.getOutputDocumentId(), e.getErrorMessage(),
                e.getRequestedBy(), e.getStartedAt(), e.getCompletedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public GeneratedDocumentJobJpaEntity toJpaEntity(GeneratedDocumentJob d) {
        GeneratedDocumentJobJpaEntity e = new GeneratedDocumentJobJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setTemplateId(d.templateId());
        e.setTemplateVersionId(d.templateVersionId()); e.setJobType(d.jobType()); e.setStatus(d.status().name());
        e.setSourceType(d.sourceType()); e.setSourceId(d.sourceId()); e.setOutputDocumentId(d.outputDocumentId());
        e.setErrorMessage(d.errorMessage()); e.setRequestedBy(d.requestedBy()); e.setStartedAt(d.startedAt()); e.setCompletedAt(d.completedAt());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
