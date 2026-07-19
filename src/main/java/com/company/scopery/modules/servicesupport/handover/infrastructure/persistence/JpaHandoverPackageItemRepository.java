package com.company.scopery.modules.servicesupport.handover.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.handover.domain.model.HandoverPackageItem;
import com.company.scopery.modules.servicesupport.handover.domain.model.HandoverPackageItemRepository;
import com.company.scopery.modules.servicesupport.handover.infrastructure.mapper.HandoverPackageItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaHandoverPackageItemRepository implements HandoverPackageItemRepository {
    private final SpringDataHandoverPackageItemJpaRepository spring;
    private final HandoverPackageItemPersistenceMapper mapper;
    public JpaHandoverPackageItemRepository(SpringDataHandoverPackageItemJpaRepository spring, HandoverPackageItemPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public HandoverPackageItem save(HandoverPackageItem d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<HandoverPackageItem> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<HandoverPackageItem> findByHandoverPackageId(UUID p){ return spring.findByHandoverPackageId(p).stream().map(mapper::toDomain).toList(); }
}
