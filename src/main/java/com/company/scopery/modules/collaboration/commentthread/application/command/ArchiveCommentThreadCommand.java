package com.company.scopery.modules.collaboration.commentthread.application.command;
import java.util.UUID;
public record ArchiveCommentThreadCommand(UUID projectId, UUID threadId) {}
