package com.company.scopery.modules.servicesupport.queue.application.action;
import com.company.scopery.modules.servicesupport.queue.application.command.CreateSupportQueueCommand;
import com.company.scopery.modules.servicesupport.queue.application.response.SupportQueueResponse;
import com.company.scopery.modules.servicesupport.queue.domain.model.SupportQueue;
import com.company.scopery.modules.servicesupport.queue.domain.model.SupportQueueRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSupportQueueAction {
    private final SupportQueueRepository repo; private final SupportAuthorizationService auth;
    public CreateSupportQueueAction(SupportQueueRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SupportQueueResponse execute(UUID workspaceId, CreateSupportQueueCommand cmd) {
        auth.requireManage(workspaceId);
        return SupportQueueResponse.from(repo.save(SupportQueue.create(workspaceId, cmd.queueCode(), cmd.name())));
    }
}
