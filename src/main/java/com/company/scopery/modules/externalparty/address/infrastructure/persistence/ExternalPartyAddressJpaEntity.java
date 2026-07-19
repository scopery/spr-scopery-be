package com.company.scopery.modules.externalparty.address.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name = ExternalPartyTableNames.ADDRESS) @Getter @Setter @NoArgsConstructor
public class ExternalPartyAddressJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "external_organization_id") private UUID externalOrganizationId;
    @Column(name = "external_contact_id") private UUID externalContactId;
    @Column(name = "address_type", nullable = false) private String addressType;
    private String line1;
    private String line2;
    private String city;
    @Column(name = "state_region") private String stateRegion;
    @Column(name = "postal_code") private String postalCode;
    @Column(name = "country_code") private String countryCode;
    @Column(name = "primary_flag", nullable = false) private boolean primaryFlag;
    @Version private Integer version;
}
