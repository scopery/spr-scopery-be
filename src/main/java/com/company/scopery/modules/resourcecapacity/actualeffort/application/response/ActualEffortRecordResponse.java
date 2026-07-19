package com.company.scopery.modules.resourcecapacity.actualeffort.application.response;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.model.ActualEffortRecord;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record ActualEffortRecordResponse(UUID id, UUID projectId, String targetType, UUID targetId, LocalDate effortDate,
        BigDecimal effortHours, String inputMode, String status) {
    public static ActualEffortRecordResponse from(ActualEffortRecord r) {
        return new ActualEffortRecordResponse(r.id(), r.projectId(), r.targetType(), r.targetId(), r.effortDate(),
                r.effortHours(), r.inputMode().name(), r.status().name());
    }
}
