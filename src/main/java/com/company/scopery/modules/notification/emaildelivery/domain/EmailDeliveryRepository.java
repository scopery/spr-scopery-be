package com.company.scopery.modules.notification.emaildelivery.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailDeliveryRepository {

    EmailDelivery save(EmailDelivery delivery);

    Optional<EmailDelivery> findById(UUID id);

    List<EmailDelivery> findAll(EmailDeliverySearchCriteria criteria);

    long countAll(EmailDeliverySearchCriteria criteria);
}
