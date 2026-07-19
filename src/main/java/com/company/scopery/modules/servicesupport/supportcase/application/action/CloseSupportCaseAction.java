package com.company.scopery.modules.servicesupport.supportcase.application.action;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import com.company.scopery.modules.servicesupport.supportcase.application.response.SupportCaseResponse;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CloseSupportCaseAction {
    private final SupportCaseRepository cases; private final SupportAuthorizationService auth;
    public CloseSupportCaseAction(SupportCaseRepository cases, SupportAuthorizationService auth){
        this.cases=cases; this.auth=auth;
    }
    @Transactional
    public SupportCaseResponse execute(UUID workspaceId, UUID caseId) {
        auth.requireManage(workspaceId);
        SupportCase c = cases.findById(caseId).orElseThrow(() -> SupportExceptions.caseNotFound(caseId));
        try { return SupportCaseResponse.from(cases.save(c.close())); }
        catch (IllegalStateException ex) { throw SupportExceptions.invalidStatus(); }
    }
}
