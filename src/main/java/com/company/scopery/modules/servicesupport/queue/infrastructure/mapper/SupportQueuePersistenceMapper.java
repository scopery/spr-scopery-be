package com.company.scopery.modules.servicesupport.queue.infrastructure.mapper;

import com.company.scopery.modules.servicesupport.queue.domain.model.SupportQueue;
import com.company.scopery.modules.servicesupport.queue.infrastructure.persistence.SupportQueueJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SupportQueuePersistenceMapper {
    public SupportQueueJpaEntity toJpa(SupportQueue d) {
        SupportQueueJpaEntity e = new SupportQueueJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setQueueCode(d.queueCode());
        e.setName(d.name()); e.setStatus(d.status()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportQueue toDomain(SupportQueueJpaEntity e) {
        return new SupportQueue(e.getId(), e.getWorkspaceId(), e.getQueueCode(), e.getName(), e.getStatus(), e.getCreatedAt());
    }
}
