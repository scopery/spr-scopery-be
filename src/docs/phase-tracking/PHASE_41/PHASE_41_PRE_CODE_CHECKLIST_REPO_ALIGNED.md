# Phase 41 — Repository-Aligned Pre-Code Checklist

```text
[ ] Read CLAUDE.md / CLAUDE.ms and Coding_convention.md.
[ ] Confirm latest Flyway version; V95/V96 free or atomically renumber artifacts.
[ ] Confirm documenthub_version columns storage_key/content_type/checksum.
[ ] Confirm file_size_bytes actual existence/type before applying optional size checks.
[ ] Inspect existing ErrorResponse JSON fields and AppException factories.
[ ] Inspect existing Knowledge package/file naming.
[ ] Inspect IAM permission initializer interface and an existing module implementation.
[ ] Inspect EventDefinition initializer interface and an existing module implementation.
[ ] Merge Elasticsearch/MinIO compose services.
[ ] Add official ES client, AWS SDK S3, PDFBox, POI dependencies.
[ ] Apply V95 on clean/upgrade database.
[ ] Apply V96 on clean/upgrade database.
[ ] Implement Document Hub presigned upload/complete/download.
[ ] Implement R2 staging and MinIO integration tests.
[ ] Implement TASK/DOCUMENT_VERSION/MEETING_MINUTE adapters only.
[ ] Implement PDF/DOCX/TXT/MD extraction only.
[ ] Implement deterministic ACL/chunk tests.
[ ] Implement ES mapping/aliases/reindex.
[ ] Implement BM25+KNN+RRF and graph response.
[ ] Implement KnowledgePermissionInitializer and KnowledgeEventDefinitionInitializer.
[ ] Run mvn compile and mvn test.
[ ] Create completion file with current-vs-TO-BE evidence.
```
