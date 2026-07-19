package com.company.scopery.modules.quality.qualityplan.application.service;
import com.company.scopery.modules.quality.qualityplan.application.response.QualityPlanResponse;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlanRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class QualityPlanQueryService {
    private final QualityPlanRepository repo;
    private final QualityAuthorizationService authorization;
    public QualityPlanQueryService(QualityPlanRepository repo, QualityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<QualityPlanResponse> list(UUID projectId) {
        authorization.requireQualityView(projectId);
        return repo.findByProjectId(projectId).stream().map(QualityPlanResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public QualityPlanResponse get(UUID projectId, UUID id) {
        authorization.requireQualityView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(QualityPlanResponse::from)
                .orElseThrow(() -> QualityExceptions.qualityPlanNotFound(id));
    }
}
