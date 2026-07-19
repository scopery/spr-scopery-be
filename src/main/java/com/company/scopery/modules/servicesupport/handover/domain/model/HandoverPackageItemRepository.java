package com.company.scopery.modules.servicesupport.handover.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface HandoverPackageItemRepository {
    HandoverPackageItem save(HandoverPackageItem item);
    Optional<HandoverPackageItem> findById(UUID id);
    List<HandoverPackageItem> findByHandoverPackageId(UUID handoverPackageId);
}
