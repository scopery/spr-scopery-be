package com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface SpringDataNfrSpecificationJpaRepository extends JpaRepository<NfrSpecificationJpaEntity, UUID> {}
