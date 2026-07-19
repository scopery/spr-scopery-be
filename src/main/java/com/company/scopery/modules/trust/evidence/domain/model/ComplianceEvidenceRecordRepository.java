package com.company.scopery.modules.trust.evidence.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ComplianceEvidenceRecordRepository {
    ComplianceEvidenceRecord save(ComplianceEvidenceRecord r);
    Optional<ComplianceEvidenceRecord> findById(UUID id);
    List<ComplianceEvidenceRecord> findByWorkspaceId(UUID workspaceId);
}
