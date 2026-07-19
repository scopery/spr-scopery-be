package com.company.scopery.modules.servicesupport.handover.application.action;
import com.company.scopery.modules.servicesupport.handover.application.command.AddHandoverItemCommand;
import com.company.scopery.modules.servicesupport.handover.application.response.HandoverPackageItemResponse;
import com.company.scopery.modules.servicesupport.handover.domain.model.HandoverPackageItem;
import com.company.scopery.modules.servicesupport.handover.domain.model.HandoverPackageItemRepository;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackageRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class AddHandoverPackageItemAction {
    private final HandoverPackageItemRepository items; private final ServiceHandoverPackageRepository packages; private final SupportAuthorizationService auth;
    public AddHandoverPackageItemAction(HandoverPackageItemRepository items, ServiceHandoverPackageRepository packages, SupportAuthorizationService auth){ this.items=items; this.packages=packages; this.auth=auth; }
    @Transactional
    public HandoverPackageItemResponse execute(UUID workspaceId, UUID packageId, AddHandoverItemCommand cmd) {
        auth.requireManage(workspaceId);
        packages.findById(packageId).orElseThrow(() -> SupportExceptions.handoverPackageNotFound(packageId));
        var saved = items.save(HandoverPackageItem.create(workspaceId, packageId, cmd.itemType(), cmd.title()));
        return HandoverPackageItemResponse.from(saved);
    }
}
