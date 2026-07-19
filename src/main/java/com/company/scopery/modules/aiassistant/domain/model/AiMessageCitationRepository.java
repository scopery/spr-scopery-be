package com.company.scopery.modules.aiassistant.domain.model;

import java.util.List;
import java.util.UUID;

public interface AiMessageCitationRepository {

    AiMessageCitation save(AiMessageCitation citation);

    List<AiMessageCitation> saveAll(List<AiMessageCitation> citations);

    List<AiMessageCitation> findByMessageIdOrderByOrdinal(UUID messageId);
}
