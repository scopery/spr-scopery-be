-- Product decision: WBS "Deliverable" and "Milestone" are the same concept.
-- Canonical value is MILESTONE; migrate legacy DELIVERABLE rows.
UPDATE project_wbs_node
SET node_type = 'MILESTONE'
WHERE node_type = 'DELIVERABLE';

UPDATE project_template_wbs_node
SET node_type = 'MILESTONE'
WHERE node_type = 'DELIVERABLE';
