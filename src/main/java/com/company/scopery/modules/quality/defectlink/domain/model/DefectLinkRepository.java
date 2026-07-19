package com.company.scopery.modules.quality.defectlink.domain.model;
import java.util.*;
public interface DefectLinkRepository {
    DefectLink save(DefectLink entity);
    Optional<DefectLink> findByIdAndProjectId(UUID id, UUID projectId);
    List<DefectLink> findByProjectIdAndDefectId(UUID projectId, UUID defectId);
}
