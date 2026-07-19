package com.company.scopery.modules.resourcecapacity.calendarexception.domain.enums;

public enum CalendarExceptionType {
    HOLIDAY,
    NON_WORKING_DAY,
    SPECIAL_WORKING_DAY,
    HALF_DAY,
    ADJUSTED_HOURS,
    COMPANY_EVENT;

    public boolean isNonWorking() {
        return this == HOLIDAY || this == NON_WORKING_DAY;
    }
}
