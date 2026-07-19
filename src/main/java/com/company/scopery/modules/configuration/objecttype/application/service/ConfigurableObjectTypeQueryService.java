package com.company.scopery.modules.configuration.objecttype.application.service;
import com.company.scopery.modules.configuration.objecttype.application.response.ConfigurableObjectTypeResponse;
import com.company.scopery.modules.configuration.objecttype.infrastructure.persistence.ConfigurableObjectTypeJpaEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class ConfigurableObjectTypeQueryService {
    private final EntityManager em;
    public ConfigurableObjectTypeQueryService(EntityManager em) { this.em = em; }
    @Transactional(readOnly = true)
    public List<ConfigurableObjectTypeResponse> listEnabled() {
        return em.createQuery("select e from ConfigurableObjectTypeJpaEntity e where e.enabled = true order by e.code", ConfigurableObjectTypeJpaEntity.class)
                .getResultList().stream().map(ConfigurableObjectTypeResponse::from).toList();
    }
}
