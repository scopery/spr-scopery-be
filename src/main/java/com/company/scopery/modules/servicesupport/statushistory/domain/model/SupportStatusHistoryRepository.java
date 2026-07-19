package com.company.scopery.modules.servicesupport.statushistory.domain.model;
import java.util.List; import java.util.UUID;
public interface SupportStatusHistoryRepository {
    SupportStatusHistory save(SupportStatusHistory history);
    List<SupportStatusHistory> findBySupportCaseId(UUID caseId);
}
