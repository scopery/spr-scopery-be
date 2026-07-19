package com.company.scopery.modules.collaboration.mention.domain.model;
import com.company.scopery.modules.collaboration.mention.domain.enums.MentionSourceType;
import java.util.List; import java.util.UUID;
public interface MentionRepository {
    Mention save(Mention mention);
    List<Mention> findBySource(MentionSourceType sourceType, UUID sourceId);
}
