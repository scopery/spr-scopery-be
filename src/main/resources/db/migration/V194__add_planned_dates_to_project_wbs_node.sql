-- Planning element (WBS) schedule window — optional; Gantt falls back to task rollup when null.
ALTER TABLE project_wbs_node
    ADD COLUMN IF NOT EXISTS planned_start_date DATE,
    ADD COLUMN IF NOT EXISTS planned_end_date DATE;
