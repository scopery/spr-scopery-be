package com.company.scopery.modules.quality.qualityplan.domain.model;
import java.util.*;
public interface QualityPlanRepository {
    QualityPlan save(QualityPlan entity);
    Optional<QualityPlan> findByIdAndProjectId(UUID id, UUID projectId);
    List<QualityPlan> findByProjectId(UUID projectId);
}
