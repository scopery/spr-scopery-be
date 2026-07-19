package com.company.scopery.modules.configuration.tag.application.action;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.tag.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.tag.application.command.RemoveTagAssignmentCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RemoveTagAssignmentAction {
    private final TagAssignmentRepository assignments; private final ConfigurationAuthorizationService authorization;
    public RemoveTagAssignmentAction(TagAssignmentRepository assignments, ConfigurationAuthorizationService authorization) {
        this.assignments=assignments; this.authorization=authorization;
    }
    @Transactional
    public void execute(RemoveTagAssignmentCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        var a = assignments.findById(c.assignmentId()).orElseThrow(() -> ConfigurationExceptions.tagNotFound(c.assignmentId()));
        if (!a.workspaceId().equals(c.workspaceId())) throw ConfigurationExceptions.accessDenied();
        assignments.save(a.archive());
    }
}
