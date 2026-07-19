-- Phase 31: Meetings / Collaboration

CREATE TABLE IF NOT EXISTS collab_meeting_series (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    code VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    cadence VARCHAR(50),
    owner_user_id UUID,
    status VARCHAR(50) NOT NULL,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting_series PRIMARY KEY (id),
    CONSTRAINT ck_collab_meeting_series_status CHECK (status IN ('ACTIVE','PAUSED','ARCHIVED')),
    CONSTRAINT ck_collab_meeting_series_cadence CHECK (cadence IS NULL OR cadence IN ('DAILY','WEEKLY','BIWEEKLY','MONTHLY','CUSTOM'))
);
CREATE INDEX IF NOT EXISTS idx_collab_meeting_series_workspace ON collab_meeting_series(workspace_id);
CREATE INDEX IF NOT EXISTS idx_collab_meeting_series_project ON collab_meeting_series(project_id);

CREATE TABLE IF NOT EXISTS collab_meeting (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    meeting_series_id UUID,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    meeting_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ,
    timezone VARCHAR(100),
    location VARCHAR(500),
    meeting_url VARCHAR(1000),
    organizer_user_id UUID,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    internal_only BOOLEAN NOT NULL DEFAULT TRUE,
    cancelled_at TIMESTAMPTZ,
    cancelled_by UUID,
    cancel_reason TEXT,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting PRIMARY KEY (id),
    CONSTRAINT fk_collab_meeting_series FOREIGN KEY (meeting_series_id) REFERENCES collab_meeting_series(id),
    CONSTRAINT ck_collab_meeting_status CHECK (status IN ('DRAFT','SCHEDULED','IN_PROGRESS','COMPLETED','CANCELLED','ARCHIVED')),
    CONSTRAINT ck_collab_meeting_type CHECK (meeting_type IN (
        'PROJECT_STATUS','CLIENT_STATUS','SPRINT_PLANNING','SPRINT_REVIEW','RETROSPECTIVE',
        'REQUIREMENT_REVIEW','DESIGN_REVIEW','TECHNICAL_REVIEW','RAID_REVIEW','CHANGE_CONTROL',
        'QUALITY_REVIEW','RELEASE_READINESS','UAT_REVIEW','DELIVERABLE_ACCEPTANCE','DECISION_MEETING','GENERAL','OTHER'))
);
CREATE INDEX IF NOT EXISTS idx_collab_meeting_workspace ON collab_meeting(workspace_id);
CREATE INDEX IF NOT EXISTS idx_collab_meeting_project ON collab_meeting(project_id);
CREATE INDEX IF NOT EXISTS idx_collab_meeting_status ON collab_meeting(status);
CREATE INDEX IF NOT EXISTS idx_collab_meeting_start ON collab_meeting(start_at);

CREATE TABLE IF NOT EXISTS collab_meeting_participant (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    meeting_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id UUID,
    display_name_snapshot VARCHAR(255),
    email_snapshot VARCHAR(255),
    participant_role VARCHAR(50) NOT NULL,
    attendance_status VARCHAR(50) NOT NULL,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting_participant PRIMARY KEY (id),
    CONSTRAINT fk_collab_meeting_participant_meeting FOREIGN KEY (meeting_id) REFERENCES collab_meeting(id),
    CONSTRAINT ck_collab_participant_target CHECK (target_type IN ('INTERNAL_USER','WORKSPACE_MEMBER','TEAM','EXTERNAL_CONTACT','EXTERNAL_ORGANIZATION','ROLE_PLACEHOLDER')),
    CONSTRAINT ck_collab_participant_role CHECK (participant_role IN ('ORGANIZER','FACILITATOR','ATTENDEE','OPTIONAL','PRESENTER','REVIEWER','APPROVER','CLIENT_REPRESENTATIVE','OBSERVER')),
    CONSTRAINT ck_collab_participant_attendance CHECK (attendance_status IN ('INVITED','ACCEPTED','DECLINED','TENTATIVE','ATTENDED','NO_SHOW','EXCUSED'))
);
CREATE INDEX IF NOT EXISTS idx_collab_meeting_participant_meeting ON collab_meeting_participant(meeting_id);

CREATE TABLE IF NOT EXISTS collab_meeting_agenda_item (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    meeting_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    owner_user_id UUID,
    status VARCHAR(50) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    timebox_minutes INT,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting_agenda_item PRIMARY KEY (id),
    CONSTRAINT fk_collab_agenda_meeting FOREIGN KEY (meeting_id) REFERENCES collab_meeting(id),
    CONSTRAINT ck_collab_agenda_status CHECK (status IN ('OPEN','DISCUSSED','DEFERRED','CANCELLED'))
);
CREATE INDEX IF NOT EXISTS idx_collab_agenda_meeting ON collab_meeting_agenda_item(meeting_id);

CREATE TABLE IF NOT EXISTS collab_meeting_minutes (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    meeting_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    summary TEXT,
    decisions_summary TEXT,
    actions_summary TEXT,
    client_visible_summary TEXT,
    document_id UUID,
    document_version_id UUID,
    submitted_at TIMESTAMPTZ,
    submitted_by UUID,
    approved_at TIMESTAMPTZ,
    approved_by UUID,
    rejected_at TIMESTAMPTZ,
    rejected_by UUID,
    rejection_reason TEXT,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting_minutes PRIMARY KEY (id),
    CONSTRAINT fk_collab_minutes_meeting FOREIGN KEY (meeting_id) REFERENCES collab_meeting(id),
    CONSTRAINT ck_collab_minutes_status CHECK (status IN ('DRAFT','IN_REVIEW','APPROVED','REJECTED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_collab_minutes_meeting ON collab_meeting_minutes(meeting_id);

CREATE TABLE IF NOT EXISTS collab_meeting_note (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    meeting_id UUID NOT NULL,
    agenda_item_id UUID,
    note_type VARCHAR(50) NOT NULL,
    body TEXT NOT NULL,
    internal_only BOOLEAN NOT NULL DEFAULT TRUE,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    source_ai_suggestion_id UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting_note PRIMARY KEY (id),
    CONSTRAINT fk_collab_note_meeting FOREIGN KEY (meeting_id) REFERENCES collab_meeting(id),
    CONSTRAINT ck_collab_note_type CHECK (note_type IN ('SUMMARY','DISCUSSION','DECISION_NOTE','RISK_NOTE','ISSUE_NOTE','ACTION_NOTE','CLIENT_NOTE','INTERNAL_NOTE','OTHER'))
);
CREATE INDEX IF NOT EXISTS idx_collab_note_meeting ON collab_meeting_note(meeting_id);

CREATE TABLE IF NOT EXISTS collab_meeting_action_item (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    meeting_id UUID NOT NULL,
    agenda_item_id UUID,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    owner_target_type VARCHAR(50),
    owner_target_id UUID,
    due_date DATE,
    status VARCHAR(50) NOT NULL,
    linked_task_id UUID,
    linked_raid_action_id UUID,
    completed_at TIMESTAMPTZ,
    completed_by UUID,
    completion_note TEXT,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting_action_item PRIMARY KEY (id),
    CONSTRAINT fk_collab_action_meeting FOREIGN KEY (meeting_id) REFERENCES collab_meeting(id),
    CONSTRAINT ck_collab_action_status CHECK (status IN ('OPEN','IN_PROGRESS','DONE','BLOCKED','CANCELLED','OVERDUE','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_collab_action_meeting ON collab_meeting_action_item(meeting_id);
CREATE INDEX IF NOT EXISTS idx_collab_action_due ON collab_meeting_action_item(due_date);

CREATE TABLE IF NOT EXISTS collab_meeting_artifact_link (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    meeting_id UUID NOT NULL,
    agenda_item_id UUID,
    note_id UUID,
    action_item_id UUID,
    target_type VARCHAR(50) NOT NULL,
    target_id UUID NOT NULL,
    link_type VARCHAR(50) NOT NULL,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_meeting_artifact_link PRIMARY KEY (id),
    CONSTRAINT fk_collab_link_meeting FOREIGN KEY (meeting_id) REFERENCES collab_meeting(id),
    CONSTRAINT ck_collab_link_type CHECK (link_type IN ('DISCUSSED','REVIEWED','DECIDED','ACTION_FOR','BLOCKED_BY','FOLLOW_UP_FOR','EVIDENCE','REFERENCE'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_collab_artifact_link_active
    ON collab_meeting_artifact_link(meeting_id, target_type, target_id) WHERE archived_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_collab_link_meeting ON collab_meeting_artifact_link(meeting_id);

CREATE TABLE IF NOT EXISTS collab_comment_thread (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    target_type VARCHAR(50) NOT NULL,
    target_id UUID NOT NULL,
    title VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    internal_only BOOLEAN NOT NULL DEFAULT TRUE,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMPTZ,
    resolved_by UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_comment_thread PRIMARY KEY (id),
    CONSTRAINT ck_collab_thread_status CHECK (status IN ('OPEN','RESOLVED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_collab_thread_target ON collab_comment_thread(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_collab_thread_project ON collab_comment_thread(project_id);

CREATE TABLE IF NOT EXISTS collab_comment (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    thread_id UUID NOT NULL,
    parent_comment_id UUID,
    author_type VARCHAR(50) NOT NULL,
    author_id UUID,
    author_display_name_snapshot VARCHAR(255),
    body TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    internal_only BOOLEAN NOT NULL DEFAULT TRUE,
    client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    edited_at TIMESTAMPTZ,
    edited_by UUID,
    deleted_at TIMESTAMPTZ,
    deleted_by UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_comment PRIMARY KEY (id),
    CONSTRAINT fk_collab_comment_thread FOREIGN KEY (thread_id) REFERENCES collab_comment_thread(id),
    CONSTRAINT ck_collab_comment_status CHECK (status IN ('ACTIVE','EDITED','DELETED_SOFT','ARCHIVED')),
    CONSTRAINT ck_collab_comment_author CHECK (author_type IN ('INTERNAL_USER','EXTERNAL_PORTAL_ACCOUNT','SYSTEM','AI_ASSISTANT'))
);
CREATE INDEX IF NOT EXISTS idx_collab_comment_thread ON collab_comment(thread_id);

CREATE TABLE IF NOT EXISTS collab_mention (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    source_type VARCHAR(50) NOT NULL,
    source_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id UUID,
    notification_sent BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_collab_mention PRIMARY KEY (id),
    CONSTRAINT ck_collab_mention_source CHECK (source_type IN ('COMMENT','MEETING_NOTE','MEETING_ACTION_ITEM','MEETING_MINUTES')),
    CONSTRAINT ck_collab_mention_target CHECK (target_type IN ('INTERNAL_USER','TEAM','EXTERNAL_CONTACT','PROJECT_ROLE','STAKEHOLDER'))
);
CREATE INDEX IF NOT EXISTS idx_collab_mention_source ON collab_mention(source_type, source_id);
