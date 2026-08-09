package com.company.scopery.modules.elicitation.question.domain.model;

import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionSource;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ElicitationQuestion {

    private UUID id;
    private UUID sessionId;
    private UUID roundId;
    private int sequence;
    private String questionText;
    private String answerText;
    private ClarityLevel clarityLevel;
    private String aiFeedback;
    private QuestionStatus status;
    private QuestionSource source;
    private UUID parentQuestionId;
    private Instant answeredAt;
    private List<String> suggestedAnswers;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    private ElicitationQuestion() {}

    public static ElicitationQuestion createAiGenerated(UUID sessionId, UUID roundId, int sequence,
                                                         String questionText, List<String> suggestedAnswers) {
        ElicitationQuestion q = new ElicitationQuestion();
        q.id = UUID.randomUUID();
        q.sessionId = sessionId;
        q.roundId = roundId;
        q.sequence = sequence;
        q.questionText = questionText;
        q.suggestedAnswers = suggestedAnswers;
        q.status = QuestionStatus.PENDING;
        q.source = QuestionSource.AI_GENERATED;
        q.createdAt = Instant.now();
        q.updatedAt = q.createdAt;
        return q;
    }

    public static ElicitationQuestion createFeedback(UUID sessionId, int sequence, String questionText,
                                                       UUID parentQuestionId) {
        ElicitationQuestion q = new ElicitationQuestion();
        q.id = UUID.randomUUID();
        q.sessionId = sessionId;
        q.sequence = sequence;
        q.questionText = questionText;
        q.status = QuestionStatus.PENDING;
        q.source = QuestionSource.FEEDBACK;
        q.parentQuestionId = parentQuestionId;
        q.createdAt = Instant.now();
        q.updatedAt = q.createdAt;
        return q;
    }

    public static ElicitationQuestion reconstitute(UUID id, UUID sessionId, UUID roundId, int sequence,
                                                    String questionText, String answerText,
                                                    ClarityLevel clarityLevel, String aiFeedback,
                                                    QuestionStatus status, QuestionSource source,
                                                    UUID parentQuestionId, Instant answeredAt,
                                                    List<String> suggestedAnswers,
                                                    String createdBy, String updatedBy,
                                                    Instant createdAt, Instant updatedAt) {
        ElicitationQuestion q = new ElicitationQuestion();
        q.id = id;
        q.sessionId = sessionId;
        q.roundId = roundId;
        q.sequence = sequence;
        q.questionText = questionText;
        q.answerText = answerText;
        q.clarityLevel = clarityLevel;
        q.aiFeedback = aiFeedback;
        q.status = status;
        q.source = source;
        q.parentQuestionId = parentQuestionId;
        q.answeredAt = answeredAt;
        q.suggestedAnswers = suggestedAnswers;
        q.createdBy = createdBy;
        q.updatedBy = updatedBy;
        q.createdAt = createdAt;
        q.updatedAt = updatedAt;
        return q;
    }

    public void answer(String answerText) {
        if (status != QuestionStatus.PENDING) throw new IllegalStateException("Question is not pending");
        this.answerText = answerText;
        this.status = QuestionStatus.ANSWERED;
        this.answeredAt = Instant.now();
        this.updatedAt = this.answeredAt;
    }

    public void skip() {
        if (status != QuestionStatus.PENDING) throw new IllegalStateException("Question is not pending");
        this.status = QuestionStatus.SKIPPED;
        this.updatedAt = Instant.now();
    }

    public void evaluate(ClarityLevel clarityLevel, String aiFeedback) {
        this.clarityLevel = clarityLevel;
        this.aiFeedback = aiFeedback;
        this.updatedAt = Instant.now();
    }

    public UUID id()                     { return id; }
    public UUID sessionId()              { return sessionId; }
    public UUID roundId()                { return roundId; }
    public int sequence()                { return sequence; }
    public String questionText()         { return questionText; }
    public String answerText()           { return answerText; }
    public ClarityLevel clarityLevel()   { return clarityLevel; }
    public String aiFeedback()           { return aiFeedback; }
    public QuestionStatus status()       { return status; }
    public QuestionSource source()       { return source; }
    public UUID parentQuestionId()       { return parentQuestionId; }
    public Instant answeredAt()          { return answeredAt; }
    public List<String> suggestedAnswers() { return suggestedAnswers; }
    public String createdBy()            { return createdBy; }
    public String updatedBy()            { return updatedBy; }
    public Instant createdAt()           { return createdAt; }
    public Instant updatedAt()           { return updatedAt; }
}
