package com.company.scopery.modules.resourcecapacity.dayrule.application.command;

import java.util.List;
import java.util.UUID;

public record ReplaceDayRulesCommand(
        UUID calendarId,
        List<DayRuleItem> dayRules
) {}
