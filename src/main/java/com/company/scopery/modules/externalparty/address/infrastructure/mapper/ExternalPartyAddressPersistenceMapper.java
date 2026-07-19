package com.company.scopery.modules.externalparty.address.infrastructure.mapper;

import com.company.scopery.modules.externalparty.address.domain.enums.AddressType;
import com.company.scopery.modules.externalparty.address.domain.model.ExternalPartyAddress;
import com.company.scopery.modules.externalparty.address.infrastructure.persistence.ExternalPartyAddressJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalPartyAddressPersistenceMapper {

    public ExternalPartyAddress toDomain(ExternalPartyAddressJpaEntity e) {
        return new ExternalPartyAddress(e.getId(), e.getWorkspaceId(),
                e.getExternalOrganizationId(), e.getExternalContactId(),
                e.getAddressType() != null ? AddressType.valueOf(e.getAddressType()) : null,
                e.getLine1(), e.getLine2(), e.getCity(), e.getStateRegion(),
                e.getPostalCode(), e.getCountryCode(), e.isPrimaryFlag(),
                e.getVersion() != null ? e.getVersion() : 0,
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ExternalPartyAddressJpaEntity toJpaEntity(ExternalPartyAddress d) {
        var e = new ExternalPartyAddressJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setExternalOrganizationId(d.externalOrganizationId());
        e.setExternalContactId(d.externalContactId());
        e.setAddressType(d.addressType() != null ? d.addressType().name() : null);
        e.setLine1(d.line1());
        e.setLine2(d.line2());
        e.setCity(d.city());
        e.setStateRegion(d.stateRegion());
        e.setPostalCode(d.postalCode());
        e.setCountryCode(d.countryCode());
        e.setPrimaryFlag(d.primaryFlag());
        e.setCreatedAt(d.createdAt());
        e.setVersion(d.version());
        return e;
    }
}
