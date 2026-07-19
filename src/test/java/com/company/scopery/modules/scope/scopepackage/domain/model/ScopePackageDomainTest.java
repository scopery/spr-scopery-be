package com.company.scopery.modules.scope.scopepackage.domain.model;

import com.company.scopery.modules.scope.scopepackage.domain.enums.ScopePackageStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScopePackageDomainTest {

    @Test
    void approve_setsApprovedStatusAndCurrentFlag() {
        UUID actor = UUID.randomUUID();
        ScopePackage pkg = ScopePackage.create(UUID.randomUUID(), UUID.randomUUID(), "SP-1", "Package", null, "trace");
        ScopePackage approved = pkg.approve(actor);

        assertThat(approved.status()).isEqualTo(ScopePackageStatus.APPROVED);
        assertThat(approved.currentFlag()).isTrue();
        assertThat(approved.approvedBy()).isEqualTo(actor);
        assertThat(approved.approvedAt()).isNotNull();
        assertThat(approved.isEditable()).isFalse();
    }

    @Test
    void approve_rejectsArchivedPackage() {
        ScopePackage pkg = ScopePackage.create(UUID.randomUUID(), UUID.randomUUID(), "SP-1", "Package", null, "trace");
        ScopePackage archived = new ScopePackage(
                pkg.id(), pkg.projectId(), pkg.workspaceId(), null, null, pkg.code(), pkg.name(), pkg.description(),
                ScopePackageStatus.ARCHIVED, false, null, null, pkg.createdAt(), UUID.randomUUID(), pkg.traceId(),
                0, pkg.createdAt(), pkg.updatedAt());

        assertThatThrownBy(() -> archived.approve(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("archived");
    }
}
