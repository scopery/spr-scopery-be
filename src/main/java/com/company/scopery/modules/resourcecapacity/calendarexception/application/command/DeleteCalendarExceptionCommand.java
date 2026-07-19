package com.company.scopery.modules.resourcecapacity.calendarexception.application.command;

import java.util.UUID;

public record DeleteCalendarExceptionCommand(UUID id, UUID calendarId) {}
