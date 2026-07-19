package com.company.scopery.modules.externalparty.channel.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name = ExternalPartyTableNames.CHANNEL) @Getter @Setter @NoArgsConstructor
public class ExternalContactChannelJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "external_contact_id", nullable = false) private UUID externalContactId;
    @Column(name = "channel_type", nullable = false) private String channelType;
    @Column(name = "channel_value", nullable = false) private String channelValue;
    @Column(name = "primary_flag", nullable = false) private boolean primaryFlag;
    @Version private Integer version;
}
