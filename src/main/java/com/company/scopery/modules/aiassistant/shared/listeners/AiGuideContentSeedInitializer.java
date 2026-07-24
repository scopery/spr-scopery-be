package com.company.scopery.modules.aiassistant.shared.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds guide definitions and suggested questions for the AI assistant.
 *
 * HOW TO UPDATE DOCS:
 * Edit the GUIDES and SUGGESTED_QUESTIONS lists below and redeploy.
 * The seeder runs an UPSERT (insert-or-update) on startup, so changes
 * take effect immediately on the next restart - no new Flyway migration needed.
 *
 * Page codes in use:
 *   AI_ASSISTANT    - main AI chat page (/workspace/{id}/ai)
 *   DOCUMENT_DETAIL - when AI is used while viewing a document
 */
@Component
@Order(20)
public class AiGuideContentSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiGuideContentSeedInitializer.class);
    private static final String LOCALE = "en-US";

    private final NamedParameterJdbcTemplate jdbc;

    public AiGuideContentSeedInitializer(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        upsertGuides();
        upsertSuggestedQuestions();
        log.info("[AiGuideContentSeed] Guide definitions and suggested questions seeded successfully.");
    }

    // -------------------------------------------------------------------------
    //  GUIDE DEFINITIONS
    //  body_markdown is displayed when users click "Explain this page" in the AI chat.
    //  To update: edit the body below and redeploy.
    // -------------------------------------------------------------------------

    private record Guide(String code, String pageCode, String title, String body) {}

    /**
     * * EDIT THIS LIST to update what the AI says when explaining Scopery pages.
     */
    private static final List<Guide> GUIDES = List.of(

            new Guide(
                    "guide::AI_ASSISTANT::en-US::1",
                    "AI_ASSISTANT",
                    "What can Scopery AI do?",
                    """
                    ## Scopery AI Assistant

                    Scopery AI is your intelligent project management assistant. Here's how it can help you:

                    ### Project Planning
                    - Summarize project status, phases, and upcoming milestones
                    - Identify overdue tasks and team workload distribution
                    - Suggest next actions based on current project progress
                    - Answer questions about timeline, schedule, and resource allocation

                    ### Work Items & Tasks
                    - **Create a task**: Go to *Work Items* -> click **+ Add task** -> fill in title, assignee, due date, priority, and phase
                    - **Assign tasks**: Open any task and select a team member from the Assignee field
                    - **Update status**: Drag tasks between columns (TODO -> IN PROGRESS -> DONE) or use the status dropdown

                    ### Project Phases & Planning
                    - **Create a phase**: Go to *Work Items* -> click **Manage phases** -> add a new phase with start/end dates
                    - **Link tasks to phases**: Open a task and select the phase in the Phase field
                    - **View in timeline**: Switch to the *Timeline* page for a Gantt-style view

                    ### Scope & Deliverables
                    - **Define scope**: Use the *Scope* page to document what's included and excluded from the project
                    - **Track deliverables**: The *Deliverables* page lists all expected outputs with status and owners

                    ### Change Requests
                    - Navigate to *Change Requests* (under Control section in the left menu)
                    - Click **+ New Change Request** and fill in the change description, impact, and justification
                    - Change requests go through an approval workflow before affecting scope or budget

                    ### RAID (Risks, Actions, Issues, Decisions)
                    - **Risks**: Log potential threats to the project and assign mitigation plans
                    - **Actions**: Track action items from meetings and decisions
                    - **Issues**: Record active problems that need resolution
                    - **Decisions**: Document key decisions made during the project lifecycle

                    ### Meetings
                    - Create meeting records and attach minutes
                    - AI can summarize meeting minutes and extract action items automatically

                    ### Reports & Dashboards
                    - View project health, budget tracking, and KPI dashboards under *Reports*
                    - AI can generate executive summaries of project status on demand

                    ---
                    **Tip**: You can ask me anything about your project data. I have access to your tasks, milestones, phases, and key metrics.
                    """
            ),

            new Guide(
                    "guide::DOCUMENT_DETAIL::en-US::1",
                    "DOCUMENT_DETAIL",
                    "Working with Documents",
                    """
                    ## Document AI Assistant

                    You are viewing a Scopery document. The AI can help you:

                    - **Summarize** the document's key points
                    - **Identify gaps** or missing sections
                    - **Suggest improvements** to the content structure
                    - **Answer questions** about specific sections
                    - **Compare** this document against project requirements or scope

                    ### Document Types in Scopery
                    - **Project Brief**: High-level overview of project objectives and context
                    - **Requirements Document**: Detailed functional and non-functional requirements
                    - **Scope Document**: Project boundaries, inclusions, and exclusions
                    - **Status Report**: Periodic updates on project progress
                    - **Meeting Minutes**: Notes and decisions from project meetings

                    Ask me anything about this document's content!
                    """
            )
    );

    // -------------------------------------------------------------------------
    //  SUGGESTED QUESTIONS
    //  These appear as clickable chips in the AI chat when no messages exist.
    //  To update: edit the list below and redeploy.
    // -------------------------------------------------------------------------

    private record SuggestedQuestion(String code, String pageCode, String questionText, int displayOrder) {}

    /**
     * * EDIT THIS LIST to change the suggested questions shown in the AI chat.
     */
    private static final List<SuggestedQuestion> SUGGESTED_QUESTIONS = List.of(

            // -- AI_ASSISTANT page (main AI chat page) --
            new SuggestedQuestion("sq::AI_ASSISTANT::status::1",          "AI_ASSISTANT", "What is the current status of my active projects?", 1),
            new SuggestedQuestion("sq::AI_ASSISTANT::create_task::1",     "AI_ASSISTANT", "How do I create a task and assign it to a team member?", 2),
            new SuggestedQuestion("sq::AI_ASSISTANT::risks::1",           "AI_ASSISTANT", "Show me the top risks and open issues across my projects.", 3),
            new SuggestedQuestion("sq::AI_ASSISTANT::change_request::1",  "AI_ASSISTANT", "How do I log a change request and what happens next?", 4),
            new SuggestedQuestion("sq::AI_ASSISTANT::timeline::1",        "AI_ASSISTANT", "How do I set up a project timeline and assign phases?", 5),
            new SuggestedQuestion("sq::AI_ASSISTANT::overdue::1",         "AI_ASSISTANT", "Which tasks are overdue and who is responsible?", 6),
            new SuggestedQuestion("sq::AI_ASSISTANT::meetings::1",        "AI_ASSISTANT", "Summarize the action items from recent meetings.", 7),
            new SuggestedQuestion("sq::AI_ASSISTANT::capabilities::1",    "AI_ASSISTANT", "What can Scopery AI help me with?", 8),

            // -- DOCUMENT_DETAIL page (viewing a specific document) --
            new SuggestedQuestion("sq::DOCUMENT_DETAIL::summarize::1",    "DOCUMENT_DETAIL", "Summarize the key points of this document.", 1),
            new SuggestedQuestion("sq::DOCUMENT_DETAIL::gaps::1",         "DOCUMENT_DETAIL", "Are there any gaps or missing sections in this document?", 2),
            new SuggestedQuestion("sq::DOCUMENT_DETAIL::improve::1",      "DOCUMENT_DETAIL", "How can I improve this document?", 3),
            new SuggestedQuestion("sq::DOCUMENT_DETAIL::scope_link::1",   "DOCUMENT_DETAIL", "How does this document relate to the project scope?", 4)
    );

    // -------------------------------------------------------------------------
    //  DB operations (do not edit below this line)
    // -------------------------------------------------------------------------

    private static final String UPSERT_GUIDE = """
            INSERT INTO aiassistant_guide_definition
                (id, code, page_code, field_code, action_code, locale, title, body_markdown,
                 metadata_version, source_kind, status, created_at, created_by, updated_at, updated_by, version)
            VALUES
                (gen_random_uuid(), :code, :pageCode, NULL, NULL, :locale, :title, :body,
                 1, 'ADMIN_CURATED', 'ACTIVE', NOW(), NULL, NOW(), NULL, 0)
            ON CONFLICT (code) DO UPDATE SET
                title         = EXCLUDED.title,
                body_markdown = EXCLUDED.body_markdown,
                status        = EXCLUDED.status,
                updated_at    = NOW()
            """;

    private static final String UPSERT_QUESTION = """
            INSERT INTO aiassistant_suggested_question
                (id, code, page_code, entity_type, action_code, locale, question_text,
                 display_order, status, created_at, created_by, updated_at, updated_by, version)
            VALUES
                (gen_random_uuid(), :code, :pageCode, NULL, NULL, :locale, :questionText,
                 :displayOrder, 'ACTIVE', NOW(), NULL, NOW(), NULL, 0)
            ON CONFLICT (code) DO UPDATE SET
                question_text = EXCLUDED.question_text,
                display_order = EXCLUDED.display_order,
                status        = EXCLUDED.status,
                updated_at    = NOW()
            """;

    private void upsertGuides() {
        for (Guide g : GUIDES) {
            jdbc.update(UPSERT_GUIDE, new MapSqlParameterSource()
                    .addValue("code", g.code())
                    .addValue("pageCode", g.pageCode())
                    .addValue("locale", LOCALE)
                    .addValue("title", g.title())
                    .addValue("body", g.body()));
        }
    }

    private void upsertSuggestedQuestions() {
        for (SuggestedQuestion q : SUGGESTED_QUESTIONS) {
            jdbc.update(UPSERT_QUESTION, new MapSqlParameterSource()
                    .addValue("code", q.code())
                    .addValue("pageCode", q.pageCode())
                    .addValue("locale", LOCALE)
                    .addValue("questionText", q.questionText())
                    .addValue("displayOrder", q.displayOrder()));
        }
    }
}
