package com.company.scopery.modules.servicesupport.requesttype.application.action;
import com.company.scopery.modules.servicesupport.requesttype.application.response.SupportRequestTypeResponse;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestTypeRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DisableSupportRequestTypeAction {
    private final SupportRequestTypeRepository repo; private final SupportAuthorizationService auth;
    public DisableSupportRequestTypeAction(SupportRequestTypeRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SupportRequestTypeResponse execute(UUID workspaceId, UUID requestTypeId) {
        auth.requireManage(workspaceId);
        var rt = repo.findById(requestTypeId).orElseThrow(() -> SupportExceptions.requestTypeNotFound(requestTypeId));
        return SupportRequestTypeResponse.from(repo.save(rt.disable()));
    }
}
