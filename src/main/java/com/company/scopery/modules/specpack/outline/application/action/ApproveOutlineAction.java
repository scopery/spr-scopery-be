package com.company.scopery.modules.specpack.outline.application.action;

import com.company.scopery.modules.specpack.outline.application.command.ApproveOutlineCommand;
import com.company.scopery.modules.specpack.outline.application.response.OutlineResponse;
import com.company.scopery.modules.specpack.outline.domain.enums.OutlineStatus;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutline;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutlineRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApproveOutlineAction {

    private final SpecPackOutlineRepository outlineRepository;
    private final SpecPackActivityLogger activityLogger;

    public ApproveOutlineAction(SpecPackOutlineRepository outlineRepository,
                                 SpecPackActivityLogger activityLogger) {
        this.outlineRepository = outlineRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OutlineResponse execute(ApproveOutlineCommand command) {
        SpecPackOutline outline = outlineRepository.findById(command.outlineId())
                .filter(o -> o.sessionId().equals(command.sessionId()))
                .orElseThrow(() -> SpecPackExceptions.outlineNotFound(command.outlineId()));

        if (outline.status() == OutlineStatus.APPROVED) {
            throw SpecPackExceptions.outlineAlreadyApproved(command.outlineId());
        }
        if (outline.status() != OutlineStatus.DRAFT) {
            throw SpecPackExceptions.outlineNotDraft(command.outlineId());
        }

        outline.approve();
        SpecPackOutline saved = outlineRepository.save(outline);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_OUTLINE,
                saved.id(),
                SpecPackActivityActions.OUTLINE_APPROVED,
                "Outline v" + saved.versionNumber() + " approved for session: " + saved.sessionId()
        );

        return OutlineResponse.from(saved);
    }
}
