package com.company.scopery.modules.collaboration.note.http.request;
import java.util.UUID;
public record CreateRaidItemFromNoteRequest(String type, String title, String code, String description, UUID ownerUserId) {}
