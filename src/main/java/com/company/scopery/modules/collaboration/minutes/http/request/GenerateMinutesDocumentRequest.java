package com.company.scopery.modules.collaboration.minutes.http.request;
import java.util.UUID;
public record GenerateMinutesDocumentRequest(UUID folderId, String code, String title) {}
