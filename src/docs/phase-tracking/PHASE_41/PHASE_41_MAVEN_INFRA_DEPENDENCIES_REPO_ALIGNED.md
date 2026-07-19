# Phase 41 — Maven and Infrastructure Dependency Lock

> Add only after checking the repository BOM/dependencyManagement. Prefer managed versions already present.

Required capabilities/artifacts:

```xml
<!-- Elasticsearch official Java client -->
<dependency>
  <groupId>co.elastic.clients</groupId>
  <artifactId>elasticsearch-java</artifactId>
</dependency>

<!-- S3-compatible storage: MinIO local / Cloudflare R2 production -->
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>s3</artifactId>
</dependency>

<!-- PDF text extraction -->
<dependency>
  <groupId>org.apache.pdfbox</groupId>
  <artifactId>pdfbox</artifactId>
</dependency>

<!-- DOCX extraction -->
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi-ooxml</artifactId>
</dependency>
```

Rules:

```text
- Do not add a MinIO-specific Java client; use the S3-compatible AWS SDK adapter.
- Do not add Spring Data Elasticsearch unless the implementation explicitly needs its repository abstraction; the official client is the default.
- Reuse existing Jackson and HTTP transport.
- Keep Elasticsearch client and server compatible with 8.19.16.
- Add Testcontainers modules only if repository tests already use Testcontainers or the phase explicitly introduces it consistently.
```

Required configuration groups:

```text
scopery.elasticsearch.*
scopery.storage.*
scopery.embedding.*
scopery.knowledge.chunking.*
```
