ALTER TABLE knowledge_graph_node
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
