package com.company.scopery.modules.knowledge.retrieval.application.service;

import com.company.scopery.modules.knowledge.source.domain.enums.ChunkType;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeChunkingService {

    private static final String STRATEGY_VERSION = "chunk-v1";

    private final int taskTargetTokens;
    private final int documentTargetTokens;
    private final int meetingTargetTokens;

    public KnowledgeChunkingService(
            @Value("${scopery.knowledge.chunking.task-target-tokens:600}") int taskTargetTokens,
            @Value("${scopery.knowledge.chunking.document-target-tokens:800}") int documentTargetTokens,
            @Value("${scopery.knowledge.chunking.meeting-target-tokens:600}") int meetingTargetTokens) {
        this.taskTargetTokens = taskTargetTokens;
        this.documentTargetTokens = documentTargetTokens;
        this.meetingTargetTokens = meetingTargetTokens;
    }

    public record ChunkResult(
            int ordinal,
            String strategyVersion,
            ChunkType chunkType,
            List<String> headingPath,
            String plainText,
            int tokenCount,
            int startCodePoint,
            int endCodePoint,
            String contentHash
    ) {}

    public List<ChunkResult> chunk(UUID sourceVersionRefId, KnowledgeSourceType sourceType, String normalizedText) {
        String text = normalize(normalizedText);
        int targetTokens = resolveTargetTokens(sourceType);
        int hardMax = resolveHardMax(sourceType);
        int overlap = resolveOverlap(sourceType);

        if (text.isBlank()) return List.of();

        List<ChunkResult> chunks = new ArrayList<>();
        String[] sections = text.split("\n\n+");

        StringBuilder buffer = new StringBuilder();
        List<String> currentHeading = new ArrayList<>();
        int bufferStart = 0;
        int codePointOffset = 0;
        int ordinal = 0;

        for (String section : sections) {
            String trimmed = section.strip();
            if (trimmed.isEmpty()) {
                codePointOffset += section.codePointCount(0, section.length()) + 2;
                continue;
            }

            boolean isHeading = isHeadingLine(trimmed);
            if (isHeading) {
                currentHeading = List.of(extractHeadingText(trimmed));
            }

            int sectionTokens = estimateTokens(trimmed);
            int bufferTokens = estimateTokens(buffer.toString());

            if (bufferTokens + sectionTokens > hardMax && buffer.length() > 0) {
                String chunkText = buffer.toString().strip();
                int start = bufferStart;
                int end = start + chunkText.codePointCount(0, chunkText.length());
                chunks.add(buildChunk(sourceVersionRefId, ordinal++, chunkText, currentHeading,
                        ChunkType.SECTION, start, end, estimateTokens(chunkText)));
                if (overlap > 0) {
                    buffer = new StringBuilder(tailOverlap(chunkText, overlap));
                } else {
                    buffer = new StringBuilder();
                }
                bufferStart = codePointOffset;
            }

            if (buffer.length() > 0) buffer.append("\n\n");
            buffer.append(trimmed);

            if (estimateTokens(buffer.toString()) >= targetTokens) {
                String chunkText = buffer.toString().strip();
                int start = bufferStart;
                int end = start + chunkText.codePointCount(0, chunkText.length());
                chunks.add(buildChunk(sourceVersionRefId, ordinal++, chunkText, currentHeading,
                        ChunkType.SECTION, start, end, estimateTokens(chunkText)));
                if (overlap > 0) {
                    buffer = new StringBuilder(tailOverlap(chunkText, overlap));
                } else {
                    buffer = new StringBuilder();
                }
                bufferStart = codePointOffset;
            }

            codePointOffset += section.codePointCount(0, section.length()) + 2;
        }

        if (buffer.length() > 0 && !buffer.toString().isBlank()) {
            String chunkText = buffer.toString().strip();
            int end = bufferStart + chunkText.codePointCount(0, chunkText.length());
            chunks.add(buildChunk(sourceVersionRefId, ordinal, chunkText, currentHeading,
                    ChunkType.SECTION, bufferStart, end, estimateTokens(chunkText)));
        }

        return List.copyOf(chunks);
    }

    public String normalize(String text) {
        if (text == null) return "";
        String nfkc = Normalizer.normalize(text, Normalizer.Form.NFKC);
        return nfkc.replace("\r\n", "\n")
                   .replace("\r", "\n")
                   .replaceAll("[ \t]+\n", "\n")
                   .replaceAll("\n{3,}", "\n\n")
                   .strip();
    }

    /** Rough approximation: 1 token ≈ 4 characters */
    public int estimateTokens(String text) {
        if (text == null || text.isEmpty()) return 0;
        return Math.max(1, text.length() / 4);
    }

    private ChunkResult buildChunk(UUID sourceVersionRefId, int ordinal, String text,
                                   List<String> headingPath, ChunkType chunkType,
                                   int start, int end, int tokens) {
        String hash = computeChunkHash(sourceVersionRefId, ordinal, text);
        return new ChunkResult(ordinal, STRATEGY_VERSION, chunkType,
                List.copyOf(headingPath), text, tokens, start, end, hash);
    }

    private String computeChunkHash(UUID sourceVersionRefId, int ordinal, String text) {
        try {
            String input = sourceVersionRefId + STRATEGY_VERSION + ordinal + text;
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String tailOverlap(String text, int overlapTokens) {
        int chars = overlapTokens * 4;
        if (text.length() <= chars) return text;
        return text.substring(text.length() - chars);
    }

    private boolean isHeadingLine(String line) {
        String first = line.lines().findFirst().orElse("");
        return first.startsWith("#") || (first.length() < 120 && first.equals(first.toUpperCase()) && first.length() > 3);
    }

    private String extractHeadingText(String section) {
        String first = section.lines().findFirst().orElse(section);
        return first.replaceAll("^#+\\s*", "").strip();
    }

    private int resolveTargetTokens(KnowledgeSourceType type) {
        return switch (type) {
            case TASK -> taskTargetTokens;
            case DOCUMENT_VERSION, NATIVE_DOCUMENT_CONTENT -> documentTargetTokens;
            case MEETING_MINUTE -> meetingTargetTokens;
            case FUNCTIONAL_ITEM, NON_FUNCTIONAL_ITEM, APP_MODULE, REQUIREMENT -> taskTargetTokens;
        };
    }

    private int resolveHardMax(KnowledgeSourceType type) {
        return switch (type) {
            case TASK -> 800;
            case DOCUMENT_VERSION, NATIVE_DOCUMENT_CONTENT -> 1000;
            case MEETING_MINUTE -> 800;
            case FUNCTIONAL_ITEM, NON_FUNCTIONAL_ITEM, APP_MODULE, REQUIREMENT -> 800;
        };
    }

    private int resolveOverlap(KnowledgeSourceType type) {
        return switch (type) {
            case TASK -> 0;
            case DOCUMENT_VERSION, NATIVE_DOCUMENT_CONTENT -> 120;
            case MEETING_MINUTE -> 80;
            case FUNCTIONAL_ITEM, NON_FUNCTIONAL_ITEM, APP_MODULE, REQUIREMENT -> 0;
        };
    }
}
