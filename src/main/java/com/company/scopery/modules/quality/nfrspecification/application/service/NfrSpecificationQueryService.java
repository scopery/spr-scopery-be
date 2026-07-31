package com.company.scopery.modules.quality.nfrspecification.application.service;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.nfrspecification.application.response.*;
import com.company.scopery.modules.quality.nfrspecification.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class NfrSpecificationQueryService {
    private final NfrSpecificationRepository specRepo;
    private final NfrTargetRepository targetRepo;
    private final QualityAuthorizationService authorization;
    public NfrSpecificationQueryService(NfrSpecificationRepository specRepo,
            NfrTargetRepository targetRepo, QualityAuthorizationService authorization) {
        this.specRepo = specRepo; this.targetRepo = targetRepo; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public NfrSpecificationResponse getSpecification(UUID projectId, UUID requirementId) {
        authorization.requireQualityView(projectId);
        return specRepo.findByRequirementId(requirementId)
                .map(NfrSpecificationResponse::from)
                .orElseThrow(() -> QualityExceptions.nfrSpecificationNotFound(requirementId));
    }
    @Transactional(readOnly = true)
    public NfrTargetResponse.ListResponse getTargets(UUID projectId, UUID requirementId) {
        authorization.requireQualityView(projectId);
        var targets = targetRepo.findByRequirementId(requirementId)
                .stream().map(NfrTargetResponse::from).toList();
        return new NfrTargetResponse.ListResponse(requirementId, targets);
    }
}
