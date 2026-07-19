package com.company.scopery.modules.quality.defect.domain.model;
import java.util.*;
public interface DefectRepository {
    Defect save(Defect entity);
    Optional<Defect> findByIdAndProjectId(UUID id, UUID projectId);
    List<Defect> findByProjectId(UUID projectId);
    List<Defect> findOpenBlockers(UUID projectId);
}
