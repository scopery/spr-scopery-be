package com.company.scopery.modules.integrationhub.provider.application.listeners;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.EntityManager; import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener; import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Component @Order(43)
public class IntegrationProviderSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EntityManager em;
    public IntegrationProviderSeedInitializer(EntityManager em){this.em=em;}
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var p : List.of(
                new P("CSV","CSV Import/Export","CSV_IMPORT", true, false),
                new P("JSON","JSON Import/Export","CSV_EXPORT", true, false),
                new P("GENERIC_WEBHOOK","Generic Webhook","WEBHOOK", true, false),
                new P("SLACK","Slack","CHAT", true, true),
                new P("GOOGLE_DRIVE","Google Drive","STORAGE", true, true),
                new P("JIRA","Jira","ISSUE_TRACKER", true, true))) {
            Number count = (Number) em.createNativeQuery("SELECT COUNT(*) FROM " + IntegrationTableNames.PROVIDER + " WHERE provider_code = :c")
                    .setParameter("c", p.code).getSingleResult();
            if (count.longValue() > 0) continue;
            em.createNativeQuery("INSERT INTO " + IntegrationTableNames.PROVIDER +
                    " (id, provider_code, name, category, enabled, seed_only, version, created_at) VALUES (:id,:code,:name,:cat,true,:seed,0,NOW())")
                    .setParameter("id", UUID.randomUUID()).setParameter("code", p.code).setParameter("name", p.name)
                    .setParameter("cat", p.cat).setParameter("seed", p.seedOnly).executeUpdate();
        }
    }
    private record P(String code, String name, String cat, boolean enabled, boolean seedOnly){}
}
