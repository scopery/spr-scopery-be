package com.company.scopery.modules.specpack.clarification.domain.model;

import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationPriority;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationSource;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationStatus;

import java.time.Instant;
import java.util.UUID;

public class SpecPackClarification {

    private UUID id;
    private UUID sessionId;
    private String code;
    private String question;
    private String answer;
    private ClarificationPriority priority;
    private ClarificationStatus status;
    private ClarificationSource source;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    private SpecPackClarification() {}

    public static SpecPackClarification create(UUID sessionId, String code, String question,
                                                ClarificationPriority priority, ClarificationSource source) {
        SpecPackClarification c = new SpecPackClarification();
        c.id = UUID.randomUUID();
        c.sessionId = sessionId;
        c.code = code;
        c.question = question;
        c.priority = priority;
        c.status = ClarificationStatus.OPEN;
        c.source = source;
        c.createdAt = Instant.now();
        c.updatedAt = c.createdAt;
        return c;
    }

    public static SpecPackClarification reconstitute(UUID id, UUID sessionId, String code, String question,
                                                      String answer, ClarificationPriority priority,
                                                      ClarificationStatus status, ClarificationSource source,
                                                      String createdBy, Instant createdAt, Instant updatedAt) {
        SpecPackClarification c = new SpecPackClarification();
        c.id = id;
        c.sessionId = sessionId;
        c.code = code;
        c.question = question;
        c.answer = answer;
        c.priority = priority;
        c.status = status;
        c.source = source;
        c.createdBy = createdBy;
        c.createdAt = createdAt;
        c.updatedAt = updatedAt;
        return c;
    }

    public void answer(String answer) {
        this.answer = answer;
        this.status = ClarificationStatus.ANSWERED;
        this.updatedAt = Instant.now();
    }

    public void defer() {
        this.status = ClarificationStatus.DEFERRED;
        this.updatedAt = Instant.now();
    }

    public void markNotApplicable() {
        this.status = ClarificationStatus.NOT_APPLICABLE;
        this.updatedAt = Instant.now();
    }

    public UUID id()                            { return id; }
    public UUID sessionId()                     { return sessionId; }
    public String code()                        { return code; }
    public String question()                    { return question; }
    public String answer()                      { return answer; }
    public ClarificationPriority priority()     { return priority; }
    public ClarificationStatus status()         { return status; }
    public ClarificationSource source()         { return source; }
    public String createdBy()                   { return createdBy; }
    public Instant createdAt()                  { return createdAt; }
    public Instant updatedAt()                  { return updatedAt; }
}
