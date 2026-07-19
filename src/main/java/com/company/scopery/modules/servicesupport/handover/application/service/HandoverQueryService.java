package com.company.scopery.modules.servicesupport.handover.application.service;
import com.company.scopery.modules.servicesupport.handover.application.response.HandoverPackageItemResponse;
import com.company.scopery.modules.servicesupport.handover.application.response.ServiceHandoverPackageResponse;
import com.company.scopery.modules.servicesupport.handover.domain.model.HandoverPackageItemRepository;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackageRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class HandoverQueryService {
    private final ServiceHandoverPackageRepository packages; private final HandoverPackageItemRepository items;
    private final SupportAuthorizationService auth;
    public HandoverQueryService(ServiceHandoverPackageRepository packages, HandoverPackageItemRepository items, SupportAuthorizationService auth){
        this.packages=packages; this.items=items; this.auth=auth;
    }
    public List<ServiceHandoverPackageResponse> listPackages(UUID workspaceId) {
        auth.requireView(workspaceId);
        return packages.findByWorkspaceId(workspaceId).stream().map(ServiceHandoverPackageResponse::from).toList();
    }
    public List<HandoverPackageItemResponse> listItems(UUID workspaceId, UUID packageId) {
        auth.requireView(workspaceId);
        return items.findByHandoverPackageId(packageId).stream().map(HandoverPackageItemResponse::from).toList();
    }
}
