package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.response.BulkDeleteResponse;
import com.company.scopery.modules.traceability.functionalitem.application.command.BulkDeleteFunctionalItemCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BulkDeleteFunctionalItemAction {

    private final DeleteFunctionalItemAction deleteAction;

    public BulkDeleteFunctionalItemAction(DeleteFunctionalItemAction deleteAction) {
        this.deleteAction = deleteAction;
    }

    public BulkDeleteResponse execute(BulkDeleteFunctionalItemCommand c) {
        List<UUID> succeeded = new ArrayList<>();
        List<BulkDeleteResponse.Failure> failures = new ArrayList<>();
        for (UUID id : c.ids()) {
            try {
                deleteAction.execute(id, c.projectId());
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
