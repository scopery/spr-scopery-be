package com.company.scopery.modules.raid.raidaction.http.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLinkedTaskFromRaidActionRequest(
        UUID phaseId,
        UUID wbsNodeId,
        BigDecimal estimateHours
) {}
