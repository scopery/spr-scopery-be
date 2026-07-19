package com.company.scopery.modules.configuration.fieldvisibility.application.service;
import com.company.scopery.modules.configuration.fieldvisibility.application.response.FieldVisibilityPolicyResponse;
import com.company.scopery.modules.configuration.fieldvisibility.domain.model.FieldVisibilityPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class FieldVisibilityPolicyQueryService {
    private final FieldVisibilityPolicyRepository policies;
    public FieldVisibilityPolicyQueryService(FieldVisibilityPolicyRepository policies) { this.policies=policies; }
    @Transactional(readOnly=true)
    public List<FieldVisibilityPolicyResponse> listByField(UUID workspaceId, UUID fieldId) {
        return policies.findByFieldId(workspaceId, fieldId).stream().map(FieldVisibilityPolicyResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<FieldVisibilityPolicyResponse> listByWorkspace(UUID workspaceId) {
        return policies.findByWorkspaceId(workspaceId).stream().map(FieldVisibilityPolicyResponse::from).toList();
    }
}
