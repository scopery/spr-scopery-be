-- V94 used CREATE TABLE IF NOT EXISTS after V85 already created a slim
-- support_incident_record. Align columns with SupportIncidentRecordJpaEntity.
ALTER TABLE support_incident_record
    ADD COLUMN IF NOT EXISTS service_profile_id UUID,
    ADD COLUMN IF NOT EXISTS incident_number VARCHAR(150),
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS impact_summary TEXT,
    ADD COLUMN IF NOT EXISTS client_visible_summary TEXT,
    ADD COLUMN IF NOT EXISTS owner_user_id UUID,
    ADD COLUMN IF NOT EXISTS detected_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS acknowledged_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS resolved_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS closed_at TIMESTAMPTZ;
