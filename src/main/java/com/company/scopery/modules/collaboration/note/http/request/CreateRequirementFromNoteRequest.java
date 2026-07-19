package com.company.scopery.modules.collaboration.note.http.request;
import java.util.UUID;
public record CreateRequirementFromNoteRequest(UUID applicationId, String code, String title, String description,
                                               String requirementType, String priority) {}
