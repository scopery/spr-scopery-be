package com.company.scopery.modules.externalparty.channel.application.command;

import java.util.UUID;

public record CreateExternalContactChannelCommand(
        UUID workspaceId,
        UUID externalContactId,
        String channelType,
        String channelValue,
        boolean primaryFlag) {}
