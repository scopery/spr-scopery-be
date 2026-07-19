package com.company.scopery.modules.externalparty.channel.application.action;

import com.company.scopery.modules.externalparty.channel.application.command.CreateExternalContactChannelCommand;
import com.company.scopery.modules.externalparty.channel.application.response.ExternalContactChannelResponse;
import com.company.scopery.modules.externalparty.channel.domain.enums.ChannelType;
import com.company.scopery.modules.externalparty.channel.domain.model.ExternalContactChannel;
import com.company.scopery.modules.externalparty.channel.domain.model.ExternalContactChannelRepository;
import com.company.scopery.modules.externalparty.shared.activity.ExternalPartyActivityLogger;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyActivityActions;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyEntityTypes;
import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import com.company.scopery.modules.externalparty.shared.util.ExternalPartyEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateExternalContactChannelAction {
    private final ExternalContactChannelRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    private final ExternalPartyActivityLogger activityLogger;

    public CreateExternalContactChannelAction(ExternalContactChannelRepository repo,
            ExternalPartyAuthorizationService authorization, ExternalPartyActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public ExternalContactChannelResponse execute(CreateExternalContactChannelCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var type = ExternalPartyEnumParser.parseRequired(ChannelType.class, c.channelType(), "channelType");
        if (c.primaryFlag() && repo.existsPrimaryByContactIdAndChannelType(c.workspaceId(), c.externalContactId(), type.name())) {
            throw ExternalPartyExceptions.channelDuplicatePrimary(type.name());
        }
        var saved = repo.save(ExternalContactChannel.create(c.workspaceId(), c.externalContactId(),
                type, c.channelValue(), c.primaryFlag()));
        activityLogger.logSuccess(ExternalPartyEntityTypes.CHANNEL, saved.id(), ExternalPartyActivityActions.CHANNEL_CREATED, "Channel created");
        return ExternalContactChannelResponse.from(saved);
    }
}
