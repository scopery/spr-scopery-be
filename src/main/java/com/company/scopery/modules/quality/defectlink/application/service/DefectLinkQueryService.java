package com.company.scopery.modules.quality.defectlink.application.service;
import com.company.scopery.modules.quality.defectlink.application.response.DefectLinkResponse;
import com.company.scopery.modules.quality.defectlink.domain.model.DefectLinkRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DefectLinkQueryService {
    private final DefectLinkRepository repo;
    private final QualityAuthorizationService authorization;
    public DefectLinkQueryService(DefectLinkRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DefectLinkResponse> listByDefect(UUID projectId, UUID defectId) {
        authorization.requireDefectView(projectId);
        return repo.findByProjectIdAndDefectId(projectId, defectId).stream().map(DefectLinkResponse::from).toList();
    }
}
