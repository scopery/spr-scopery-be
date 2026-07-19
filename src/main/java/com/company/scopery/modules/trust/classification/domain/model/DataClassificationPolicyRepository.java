package com.company.scopery.modules.trust.classification.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DataClassificationPolicyRepository {
    DataClassificationPolicy save(DataClassificationPolicy p);
    Optional<DataClassificationPolicy> findByWorkspaceId(UUID workspaceId);
    List<DataClassificationPolicy> findAll();
}
