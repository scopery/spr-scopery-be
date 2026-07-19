package com.company.scopery.modules.aiassistant.application.port;

import com.company.scopery.modules.aiagent.tool.application.port.AiToolResultItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AiAssistantCitationValidationService {

    public record ValidatedCitation(
            UUID chunkId,
            String sourceType,
            UUID sourceRefId,
            UUID sourceVersionRefId,
            String title,
            List<String> headingPath,
            String quotedFragment,
            String accessValidationResult
    ) {}

    public List<ValidatedCitation> validate(List<AiToolResultItem> toolResultItems,
                                            List<String> citedChunkIds) {
        if (toolResultItems == null || toolResultItems.isEmpty()
                || citedChunkIds == null || citedChunkIds.isEmpty()) {
            return List.of();
        }

        Set<String> citedSet = Set.copyOf(citedChunkIds);

        return toolResultItems.stream()
                .filter(item -> item.chunkId() != null && citedSet.contains(item.chunkId().toString()))
                .map(item -> new ValidatedCitation(
                        item.chunkId(),
                        item.sourceType(),
                        item.sourceRefId(),
                        item.sourceVersionRefId(),
                        item.title(),
                        item.headingPath(),
                        item.safeSnippet(),
                        "ALLOWED"
                ))
                .collect(Collectors.toList());
    }
}
