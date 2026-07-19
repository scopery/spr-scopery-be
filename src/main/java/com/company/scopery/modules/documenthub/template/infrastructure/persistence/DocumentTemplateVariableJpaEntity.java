package com.company.scopery.modules.documenthub.template.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.NATIVE_TEMPLATE_VARIABLE,
        uniqueConstraints = @UniqueConstraint(name = "uq_documenthub_native_template_variable_key",
                columnNames = {"template_version_id", "variable_key"}))
@Getter
@Setter
@NoArgsConstructor
public class DocumentTemplateVariableJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_version_id", nullable = false, updatable = false)
    private UUID templateVersionId;

    @Column(name = "variable_key", nullable = false)
    private String variableKey;

    @Column(name = "label")
    private String label;

    @Column(name = "variable_type", nullable = false)
    private String variableType;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "sensitive", nullable = false)
    private boolean sensitive;

    @Column(name = "ordinal", nullable = false)
    private int ordinal;
}
