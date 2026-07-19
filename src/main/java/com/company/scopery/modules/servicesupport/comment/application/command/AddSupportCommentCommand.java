package com.company.scopery.modules.servicesupport.comment.application.command;
import java.util.UUID;
public record AddSupportCommentCommand(String body, String visibility, UUID authorUserId) {}
