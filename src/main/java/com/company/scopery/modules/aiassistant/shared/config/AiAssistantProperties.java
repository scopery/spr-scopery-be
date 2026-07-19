package com.company.scopery.modules.aiassistant.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "scopery.ai-assistant")
public class AiAssistantProperties {

    private String promptProfileCode = "SCOPERY_CONTEXTUAL_ASSISTANT_V1";
    private Duration emitterTimeout = Duration.ofSeconds(180);
    private Duration heartbeatInterval = Duration.ofSeconds(15);
    private Duration streamEventRetention = Duration.ofHours(24);
    private Duration conversationRetention = Duration.ofDays(180);
    private Duration deletedPurgeDelay = Duration.ofDays(30);
    private Duration toolTraceRetention = Duration.ofDays(30);
    private int maxUserMessageChars = 8000;
    private int maxMessagesPerConversation = 500;
    private int maxInputTokens = 24000;
    private int maxOutputTokens = 2000;
    private int maxEvidenceChunks = 8;
    private int maxActiveStreamsPerUser = 2;
    private int maxTurnsPerUserPerDay = 200;
    private int maxTokensPerUserPerDay = 500000;
    private Memory memory = new Memory();

    public static class Memory {
        private int triggerTurns = 20;
        private int triggerUnsummarizedTokens = 12000;
        private int keepLatestMessages = 8;
        private int maxSummaryTokens = 2000;

        public int getTriggerTurns() { return triggerTurns; }
        public void setTriggerTurns(int triggerTurns) { this.triggerTurns = triggerTurns; }
        public int getTriggerUnsummarizedTokens() { return triggerUnsummarizedTokens; }
        public void setTriggerUnsummarizedTokens(int triggerUnsummarizedTokens) { this.triggerUnsummarizedTokens = triggerUnsummarizedTokens; }
        public int getKeepLatestMessages() { return keepLatestMessages; }
        public void setKeepLatestMessages(int keepLatestMessages) { this.keepLatestMessages = keepLatestMessages; }
        public int getMaxSummaryTokens() { return maxSummaryTokens; }
        public void setMaxSummaryTokens(int maxSummaryTokens) { this.maxSummaryTokens = maxSummaryTokens; }
    }

    public String getPromptProfileCode() { return promptProfileCode; }
    public void setPromptProfileCode(String promptProfileCode) { this.promptProfileCode = promptProfileCode; }
    public Duration getEmitterTimeout() { return emitterTimeout; }
    public void setEmitterTimeout(Duration emitterTimeout) { this.emitterTimeout = emitterTimeout; }
    public Duration getHeartbeatInterval() { return heartbeatInterval; }
    public void setHeartbeatInterval(Duration heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
    public Duration getStreamEventRetention() { return streamEventRetention; }
    public void setStreamEventRetention(Duration streamEventRetention) { this.streamEventRetention = streamEventRetention; }
    public Duration getConversationRetention() { return conversationRetention; }
    public void setConversationRetention(Duration conversationRetention) { this.conversationRetention = conversationRetention; }
    public Duration getDeletedPurgeDelay() { return deletedPurgeDelay; }
    public void setDeletedPurgeDelay(Duration deletedPurgeDelay) { this.deletedPurgeDelay = deletedPurgeDelay; }
    public Duration getToolTraceRetention() { return toolTraceRetention; }
    public void setToolTraceRetention(Duration toolTraceRetention) { this.toolTraceRetention = toolTraceRetention; }
    public int getMaxUserMessageChars() { return maxUserMessageChars; }
    public void setMaxUserMessageChars(int maxUserMessageChars) { this.maxUserMessageChars = maxUserMessageChars; }
    public int getMaxMessagesPerConversation() { return maxMessagesPerConversation; }
    public void setMaxMessagesPerConversation(int maxMessagesPerConversation) { this.maxMessagesPerConversation = maxMessagesPerConversation; }
    public int getMaxInputTokens() { return maxInputTokens; }
    public void setMaxInputTokens(int maxInputTokens) { this.maxInputTokens = maxInputTokens; }
    public int getMaxOutputTokens() { return maxOutputTokens; }
    public void setMaxOutputTokens(int maxOutputTokens) { this.maxOutputTokens = maxOutputTokens; }
    public int getMaxEvidenceChunks() { return maxEvidenceChunks; }
    public void setMaxEvidenceChunks(int maxEvidenceChunks) { this.maxEvidenceChunks = maxEvidenceChunks; }
    public int getMaxActiveStreamsPerUser() { return maxActiveStreamsPerUser; }
    public void setMaxActiveStreamsPerUser(int maxActiveStreamsPerUser) { this.maxActiveStreamsPerUser = maxActiveStreamsPerUser; }
    public int getMaxTurnsPerUserPerDay() { return maxTurnsPerUserPerDay; }
    public void setMaxTurnsPerUserPerDay(int maxTurnsPerUserPerDay) { this.maxTurnsPerUserPerDay = maxTurnsPerUserPerDay; }
    public int getMaxTokensPerUserPerDay() { return maxTokensPerUserPerDay; }
    public void setMaxTokensPerUserPerDay(int maxTokensPerUserPerDay) { this.maxTokensPerUserPerDay = maxTokensPerUserPerDay; }
    public Memory getMemory() { return memory; }
    public void setMemory(Memory memory) { this.memory = memory; }
}
