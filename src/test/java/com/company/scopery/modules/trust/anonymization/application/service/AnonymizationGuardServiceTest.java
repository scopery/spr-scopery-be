package com.company.scopery.modules.trust.anonymization.application.service;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHold;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHoldRepository;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class AnonymizationGuardServiceTest {
    @Mock LegalHoldRepository repo;
    @Test void legalHoldBlocksAnonymization() {
        UUID ws = UUID.randomUUID();
        when(repo.findActiveByWorkspaceId(ws)).thenReturn(List.of(LegalHold.create(ws, "LEGAL", "WORKSPACE", null, "hold")));
        assertThatThrownBy(() -> new AnonymizationGuardService(repo).requireNoActiveLegalHold(ws)).isInstanceOf(AppException.class);
    }
}
