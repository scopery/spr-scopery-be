package com.company.scopery.modules.traceability.requirement.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.response.BulkDeleteResponse;
import com.company.scopery.modules.traceability.requirement.application.command.BulkDeleteRequirementCommand;
import com.company.scopery.modules.traceability.requirement.application.command.DeleteRequirementCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BulkDeleteRequirementAction {

    private final DeleteRequirementAction deleteAction;

    public BulkDeleteRequirementAction(DeleteRequirementAction deleteAction) {
        this.deleteAction = deleteAction;
    }

    public BulkDeleteResponse execute(BulkDeleteRequirementCommand c) {
        List<UUID> succeeded = new ArrayList<>();
        List<BulkDeleteResponse.Failure> failures = new ArrayList<>();
        for (UUID id : c.ids()) {
            try {
                deleteAction.execute(new DeleteRequirementCommand(c.projectId(), id));
                succeeded.add(id);
            } catch (AppException e) {
                failures.add(new BulkDeleteResponse.Failure(id, e.getErrorCode(), e.getMessage()));
            } catch (Exception e) {
                failures.add(new BulkDeleteResponse.Failure(id, "INTERNAL_ERROR", "Unexpected error"));
            }
        }
        return BulkDeleteResponse.of(c.ids().size(), succeeded, failures);
    }
}
