package com.company.scopery.modules.aiassistant.infrastructure.sse;

import com.company.scopery.modules.aiassistant.domain.model.AiStreamEvent;
import com.company.scopery.modules.aiassistant.domain.model.AiStreamEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AiAssistantSseStreamService {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantSseStreamService.class);

    private final ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private final AiStreamEventRepository streamEventRepository;
    private final ObjectMapper objectMapper;

    public AiAssistantSseStreamService(AiStreamEventRepository streamEventRepository,
                                       ObjectMapper objectMapper) {
        this.streamEventRepository = streamEventRepository;
        this.objectMapper = objectMapper;
    }

    public SseEmitter create(UUID messageId, long timeoutMs) {
        SseEmitter emitter = new SseEmitter(timeoutMs);
        emitters.computeIfAbsent(messageId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(messageId, emitter));
        emitter.onError(ex -> removeEmitter(messageId, emitter));
        emitter.onTimeout(() -> removeEmitter(messageId, emitter));

        return emitter;
    }

    public void persistAndEmit(UUID messageId, long sequence, String eventType, Object payload, Instant expiresAt) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize SSE payload for messageId={} sequence={}", messageId, sequence, e);
            return;
        }

        String payloadHash = sha256Hex(payloadJson);

        AiStreamEvent event = AiStreamEvent.create(messageId, sequence, eventType, payloadJson, payloadHash, expiresAt);
        streamEventRepository.save(event);

        List<SseEmitter> messageEmitters = emitters.get(messageId);
        if (messageEmitters == null || messageEmitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : messageEmitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(String.valueOf(sequence))
                                .name(eventType)
                                .data(payloadJson)
                );
            } catch (IOException e) {
                log.debug("SSE emitter error for messageId={}, removing emitter", messageId);
                removeEmitter(messageId, emitter);
            }
        }
    }

    public void replay(SseEmitter emitter, UUID messageId, long afterSequence) {
        List<AiStreamEvent> events = streamEventRepository
                .findByMessageIdAndSequenceGreaterThanOrderBySequence(messageId, afterSequence);

        for (AiStreamEvent event : events) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(String.valueOf(event.sequence()))
                                .name(event.eventType())
                                .data(event.payloadJson())
                );
            } catch (IOException e) {
                log.debug("SSE replay error for messageId={}, stopping replay", messageId);
                removeEmitter(messageId, emitter);
                return;
            }
        }
    }

    public void removeEmitter(UUID messageId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> messageEmitters = emitters.get(messageId);
        if (messageEmitters != null) {
            messageEmitters.remove(emitter);
            if (messageEmitters.isEmpty()) {
                emitters.remove(messageId, messageEmitters);
            }
        }
    }

    public void sendHeartbeat() {
        for (Map.Entry<UUID, CopyOnWriteArrayList<SseEmitter>> entry : emitters.entrySet()) {
            UUID messageId = entry.getKey();
            for (SseEmitter emitter : entry.getValue()) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    log.debug("SSE heartbeat failed for messageId={}, removing emitter", messageId);
                    removeEmitter(messageId, emitter);
                }
            }
        }
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
