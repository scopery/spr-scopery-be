CREATE TABLE IF NOT EXISTS raid_decision_link (
    id UUID NOT NULL,
    decision_id UUID NOT NULL,
    project_id UUID NOT NULL,
    link_type VARCHAR(100) NOT NULL,
    target_type VARCHAR(100) NOT NULL,
    target_id UUID NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_raid_decision_link PRIMARY KEY (id),
    CONSTRAINT fk_raid_decision_link_decision FOREIGN KEY (decision_id) REFERENCES raid_decision_record(id)
);

CREATE INDEX IF NOT EXISTS idx_raid_decision_link_decision ON raid_decision_link(decision_id);
