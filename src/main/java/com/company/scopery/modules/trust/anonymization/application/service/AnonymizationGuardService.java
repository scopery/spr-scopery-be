package com.company.scopery.modules.trust.anonymization.application.service;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHoldRepository;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import java.util.UUID;
@Service
public class AnonymizationGuardService {
    private final LegalHoldRepository legalHolds;
    public AnonymizationGuardService(LegalHoldRepository legalHolds){this.legalHolds=legalHolds;}
    public void requireNoActiveLegalHold(UUID workspaceId) {
        if (!legalHolds.findActiveByWorkspaceId(workspaceId).isEmpty()) throw TrustExceptions.anonymizationBlocked();
    }
}
