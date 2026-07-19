package com.company.scopery.modules.profitability.adjustment.application.service;
import com.company.scopery.modules.profitability.adjustment.application.response.ProfitAdjustmentResponse;
import com.company.scopery.modules.profitability.adjustment.domain.model.ProfitAdjustmentRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ProfitAdjustmentQueryService {
    private final ProfitAdjustmentRepository adjustments; private final ProfitabilityAuthorizationService authorization;
    public ProfitAdjustmentQueryService(ProfitAdjustmentRepository adjustments, ProfitabilityAuthorizationService authorization) { this.adjustments=adjustments; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<ProfitAdjustmentResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return adjustments.findByProjectId(projectId).stream().map(ProfitAdjustmentResponse::from).toList();
    }
}
