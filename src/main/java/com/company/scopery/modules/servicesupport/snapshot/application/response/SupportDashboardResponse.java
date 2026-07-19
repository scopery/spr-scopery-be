package com.company.scopery.modules.servicesupport.snapshot.application.response;
public record SupportDashboardResponse(long openCases, long slaBreachedClocks, long openBreaches,
        long maintenancePlans, String note) {}
