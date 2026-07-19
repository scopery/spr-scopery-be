package com.company.scopery.modules.resourcecapacity.dayrule.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.dayrule.application.action.ReplaceDayRulesAction;
import com.company.scopery.modules.resourcecapacity.dayrule.application.command.DayRuleItem;
import com.company.scopery.modules.resourcecapacity.dayrule.application.command.ReplaceDayRulesCommand;
import com.company.scopery.modules.resourcecapacity.dayrule.application.response.CalendarDayRuleResponse;
import com.company.scopery.modules.resourcecapacity.dayrule.application.service.CalendarDayRuleQueryService;
import com.company.scopery.modules.resourcecapacity.dayrule.http.request.DayRuleItemRequest;
import com.company.scopery.modules.resourcecapacity.dayrule.http.request.ReplaceDayRulesRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CapacityApiPaths.CALENDAR_DAY_RULES)
@Tag(name = "Resource Capacity - Calendar Day Rules")
public class CalendarDayRuleController {

    private final CalendarDayRuleQueryService queryService;
    private final ReplaceDayRulesAction replaceDayRulesAction;

    public CalendarDayRuleController(CalendarDayRuleQueryService queryService,
                                     ReplaceDayRulesAction replaceDayRulesAction) {
        this.queryService = queryService;
        this.replaceDayRulesAction = replaceDayRulesAction;
    }

    @GetMapping
    @Operation(summary = "Get the 7 day rules for a working calendar")
    public ApiResponse<List<CalendarDayRuleResponse>> get(@PathVariable UUID calendarId) {
        return ApiResponse.success(queryService.getDayRules(calendarId));
    }

    @PutMapping
    @Operation(summary = "Atomically replace all 7 day rules for a working calendar")
    public ApiResponse<List<CalendarDayRuleResponse>> replace(
            @PathVariable UUID calendarId,
            @Valid @RequestBody ReplaceDayRulesRequest request) {
        List<DayRuleItem> items = request.dayRules().stream()
                .map(this::toItem)
                .toList();
        return ApiResponse.success(replaceDayRulesAction.execute(new ReplaceDayRulesCommand(calendarId, items)));
    }

    private DayRuleItem toItem(DayRuleItemRequest r) {
        return new DayRuleItem(r.dayOfWeek(), r.isWorkingDay(), r.startTime(), r.endTime(), r.workingHours());
    }
}
