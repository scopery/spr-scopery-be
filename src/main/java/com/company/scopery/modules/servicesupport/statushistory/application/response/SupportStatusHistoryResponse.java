package com.company.scopery.modules.servicesupport.statushistory.application.response;
import com.company.scopery.modules.servicesupport.statushistory.domain.model.SupportStatusHistory;
import java.time.Instant; import java.util.UUID;
public record SupportStatusHistoryResponse(UUID id, UUID supportCaseId, String fromStatus, String toStatus,
        String visibility, Instant changedAt) {
    public static SupportStatusHistoryResponse from(SupportStatusHistory d) {
        return new SupportStatusHistoryResponse(d.id(), d.supportCaseId(), d.fromStatus(), d.toStatus(),
                d.visibility(), d.changedAt());
    }
}
