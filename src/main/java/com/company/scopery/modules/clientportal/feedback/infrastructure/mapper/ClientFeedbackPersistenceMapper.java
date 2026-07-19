package com.company.scopery.modules.clientportal.feedback.infrastructure.mapper;
import com.company.scopery.modules.clientportal.feedback.domain.enums.ClientFeedbackStatus;
import com.company.scopery.modules.clientportal.feedback.domain.model.ClientFeedback;
import com.company.scopery.modules.clientportal.feedback.infrastructure.persistence.ClientFeedbackJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ClientFeedbackPersistenceMapper {
    public ClientFeedback toDomain(ClientFeedbackJpaEntity e) {
        return new ClientFeedback(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getCategory(), e.getTitle(), e.getBody(),
                ClientFeedbackStatus.valueOf(e.getStatus()), e.getSubmittedByPortalAccountId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ClientFeedbackJpaEntity toJpaEntity(ClientFeedback d) {
        ClientFeedbackJpaEntity e = new ClientFeedbackJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setCategory(d.category());
        e.setTitle(d.title()); e.setBody(d.body()); e.setStatus(d.status().name()); e.setSubmittedByPortalAccountId(d.submittedByPortalAccountId());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
