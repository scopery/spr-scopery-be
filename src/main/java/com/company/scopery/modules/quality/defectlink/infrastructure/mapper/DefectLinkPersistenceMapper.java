package com.company.scopery.modules.quality.defectlink.infrastructure.mapper;
import com.company.scopery.modules.quality.defectlink.domain.enums.DefectLinkType;
import com.company.scopery.modules.quality.defectlink.domain.model.DefectLink;
import com.company.scopery.modules.quality.defectlink.infrastructure.persistence.DefectLinkJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DefectLinkPersistenceMapper {
    public DefectLink toDomain(DefectLinkJpaEntity e) {
        return new DefectLink(e.getId(), e.getProjectId(), e.getDefectId(), e.getTargetType(), e.getTargetId(),
                DefectLinkType.valueOf(e.getLinkType()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
    public DefectLinkJpaEntity toJpaEntity(DefectLink d) {
        DefectLinkJpaEntity e = new DefectLinkJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setDefectId(d.defectId());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId()); e.setLinkType(d.linkType().name());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
