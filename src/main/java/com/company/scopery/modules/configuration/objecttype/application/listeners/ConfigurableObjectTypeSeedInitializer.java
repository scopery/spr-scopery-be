package com.company.scopery.modules.configuration.objecttype.application.listeners;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.EntityManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Component @Order(39)
public class ConfigurableObjectTypeSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EntityManager em;
    public ConfigurableObjectTypeSeedInitializer(EntityManager em) { this.em = em; }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (String code : List.of("PROJECT","TASK","DELIVERABLE","REQUIREMENT","DEFECT","DOCUMENT","RAID_ITEM","DECISION","MEETING","EXTERNAL_CONTACT")) {
            Long count = em.createQuery("select count(e) from ConfigurableObjectTypeJpaEntity e where e.code = :code", Long.class)
                    .setParameter("code", code).getSingleResult();
            if (count == 0) {
                var e = new com.company.scopery.modules.configuration.objecttype.infrastructure.persistence.ConfigurableObjectTypeJpaEntity();
                e.setId(UUID.randomUUID()); e.setCode(code); e.setName(code.replace('_',' ')); e.setCustomFieldsEnabled(true);
                e.setFormsEnabled(true); e.setTagsEnabled(true); e.setCustomStatusEnabled(false); e.setClientVisibleFieldsEnabled(false);
                e.setReportableFieldsEnabled(true); e.setSearchableFieldsEnabled(true); e.setEnabled(true);
                em.persist(e);
            }
        }
    }
}
