package com.company.scopery.modules.configuration.tag.application.service;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.tag.application.response.TagAssignmentResponse;
import com.company.scopery.modules.configuration.tag.domain.model.TagAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TagAssignmentQueryService {
    private final TagAssignmentRepository assignments; private final ConfigurationAuthorizationService authorization;
    public TagAssignmentQueryService(TagAssignmentRepository assignments, ConfigurationAuthorizationService authorization) {
        this.assignments=assignments; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<TagAssignmentResponse> list(UUID workspaceId) {
        authorization.requireFieldView(workspaceId);
        return assignments.findActiveByWorkspace(workspaceId).stream().map(TagAssignmentResponse::from).toList();
    }
}
