package com.company.scopery.modules.elicitation.shared.constant;

public final class ElicitationActivityActions {

    public static final String START_SESSION             = "START_ELICITATION_SESSION";
    public static final String CLOSE_SESSION             = "CLOSE_ELICITATION_SESSION";
    public static final String CANCEL_SESSION            = "CANCEL_ELICITATION_SESSION";

    public static final String GENERATE_QUESTIONS        = "GENERATE_ELICITATION_QUESTIONS";
    public static final String ANSWER_QUESTION           = "ANSWER_ELICITATION_QUESTION";
    public static final String SKIP_QUESTION             = "SKIP_ELICITATION_QUESTION";

    public static final String SUBMIT_ROUND              = "SUBMIT_ELICITATION_ROUND";

    public static final String GENERATE_SUGGESTIONS      = "GENERATE_ELICITATION_SUGGESTIONS";
    public static final String APPROVE_SUGGESTION_ITEM   = "APPROVE_ELICITATION_SUGGESTION_ITEM";
    public static final String REJECT_SUGGESTION_ITEM    = "REJECT_ELICITATION_SUGGESTION_ITEM";

    private ElicitationActivityActions() {}
}
