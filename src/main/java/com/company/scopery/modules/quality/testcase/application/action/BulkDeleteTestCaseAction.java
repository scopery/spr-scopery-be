package com.company.scopery.modules.quality.testcase.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.response.BulkDeleteResponse;
import com.company.scopery.modules.quality.testcase.application.command.BulkDeleteTestCaseCommand;
import com.company.scopery.modules.quality.testcase.application.command.DeleteTestCaseCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BulkDeleteTestCaseAction {

    private final DeleteTestCaseAction deleteAction;

    public BulkDeleteTestCaseAction(DeleteTestCaseAction deleteAction) {
        this.deleteAction = deleteAction;
    }

    public BulkDeleteResponse execute(BulkDeleteTestCaseCommand c) {
        List<UUID> succeeded = new ArrayList<>();
        List<BulkDeleteResponse.Failure> failures = new ArrayList<>();
        for (UUID id : c.ids()) {
            try {
                deleteAction.execute(new DeleteTestCaseCommand(id, c.projectId()));
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
