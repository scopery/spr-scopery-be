package com.company.scopery.modules.iam.resource.application.listeners;

import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@Order(90)
public class IamSystemAuthResourceInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String GLOBAL_SYSTEM_RESOURCE_CODE = "GLOBAL_SYSTEM";

    private static final Logger log = LoggerFactory.getLogger(IamSystemAuthResourceInitializer.class);

    private final IamAuthResourceRepository resourceRepository;

    public IamSystemAuthResourceInitializer(IamAuthResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        IamResourceCode code = IamResourceCode.of(GLOBAL_SYSTEM_RESOURCE_CODE);
        if (resourceRepository.findByCodeAndResourceType(code, IamResourceType.GLOBAL).isPresent()) {
            return;
        }
        try {
            IamAuthResource saved = resourceRepository.save(IamAuthResource.create(
                    code,
                    IamResourceType.GLOBAL,
                    "Global System",
                    "Global IAM resource for system-scoped permissions"));
            log.info("IAM auth resource: seeded global system resource {}", saved.id());
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            log.info("IAM auth resource: global system resource already present");
        }
    }
}
