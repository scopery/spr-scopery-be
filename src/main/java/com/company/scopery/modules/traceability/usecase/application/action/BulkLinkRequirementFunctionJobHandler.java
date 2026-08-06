package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.usecase.application.command.BulkLinkRequirementFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToFunctionCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkLinkRequirementFunctionJobHandler
        extends AbstractBulkCreateJobHandler<LinkRequirementToFunctionCommand> {

    public static final String JOB_TYPE = "BULK_LINK_REQUIREMENT_FUNCTION";

    private final RequirementFunctionRepository requirementFunctionRepo;

    public BulkLinkRequirementFunctionJobHandler(RequirementFunctionRepository requirementFunctionRepo,
                                                  BulkJobService bulkJobService,
                                                  ObjectMapper objectMapper) {
        super(bulkJobService, objectMapper);
        this.requirementFunctionRepo = requirementFunctionRepo;
    }

    @Override
    public String supportsJobType() {
        return JOB_TYPE;
    }

    @Override
    protected List<LinkRequirementToFunctionCommand> parseItems(String payloadJson) throws Exception {
        BulkLinkRequirementFunctionCommand cmd =
                objectMapper.readValue(payloadJson, BulkLinkRequirementFunctionCommand.class);
        return cmd.requirementIds().stream()
                .map(reqId -> new LinkRequirementToFunctionCommand(cmd.projectId(), cmd.functionId(), reqId))
                .toList();
    }

    @Override
    protected void createOne(LinkRequirementToFunctionCommand item) {
        if (requirementFunctionRepo.exists(item.requirementId(), item.functionId())) {
            return;
        }
        requirementFunctionRepo.link(item.requirementId(), item.functionId());
    }

    @Override
    protected String extractIdentity(LinkRequirementToFunctionCommand item) {
        return item.requirementId().toString();
    }
}
