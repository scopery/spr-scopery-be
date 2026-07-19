package com.company.scopery.modules.quality.defect.application.service;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DefectQueryService {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    public DefectQueryService(DefectRepository repo, QualityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<DefectResponse> list(UUID projectId) {
        authorization.requireDefectView(projectId);
        return repo.findByProjectId(projectId).stream().map(DefectResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public DefectResponse get(UUID projectId, UUID id) {
        authorization.requireDefectView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(DefectResponse::from)
                .orElseThrow(() -> QualityExceptions.defectNotFound(id));
    }
}
