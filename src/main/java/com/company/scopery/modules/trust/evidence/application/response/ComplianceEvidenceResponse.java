package com.company.scopery.modules.trust.evidence.application.response;
import com.company.scopery.modules.trust.evidence.domain.model.ComplianceEvidenceRecord;
import java.util.UUID;
public record ComplianceEvidenceResponse(UUID id, String evidenceType, String title, String status) {
    public static ComplianceEvidenceResponse from(ComplianceEvidenceRecord r){ return new ComplianceEvidenceResponse(r.id(), r.evidenceType(), r.title(), r.status()); }
}
