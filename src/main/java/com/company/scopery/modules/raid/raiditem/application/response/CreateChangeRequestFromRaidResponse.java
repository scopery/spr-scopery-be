package com.company.scopery.modules.raid.raiditem.application.response;

import java.util.UUID;

public record CreateChangeRequestFromRaidResponse(RaidItemResponse raidItem, UUID changeRequestId) {}
