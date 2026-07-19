package com.company.scopery.modules.raid.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class RaidExceptions {
    private RaidExceptions() {}
    public static AppException itemNotFound(UUID id){ return new AppException(RaidErrorCatalog.RAID_ITEM_NOT_FOUND, "RAID item not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException actionNotFound(UUID id){ return new AppException(RaidErrorCatalog.RAID_ACTION_NOT_FOUND, "RAID action not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException invalidStatus(String d){ return new AppException(RaidErrorCatalog.RAID_INVALID_STATUS, d, Map.of()); }
    public static AppException accessDenied(){ return new AppException(RaidErrorCatalog.RAID_ACCESS_DENIED); }
    public static AppException projectArchived(UUID id){ return new AppException(RaidErrorCatalog.RAID_PROJECT_ARCHIVED, "Project archived: "+id, Map.of("projectId", id)); }
    public static AppException titleRequired(){ return new AppException(RaidErrorCatalog.RAID_TITLE_REQUIRED); }
    public static AppException escalationReasonRequired(){ return new AppException(RaidErrorCatalog.RAID_ESCALATION_REASON_REQUIRED); }
    public static AppException resolutionNoteRequired(){ return new AppException(RaidErrorCatalog.RAID_RESOLUTION_NOTE_REQUIRED); }
    public static AppException decisionNotFound(UUID id){ return new AppException(RaidErrorCatalog.DECISION_NOT_FOUND, "Decision not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException decisionInvalidStatus(String d){ return new AppException(RaidErrorCatalog.DECISION_INVALID_STATUS, d, Map.of()); }
    public static AppException outcomeRequired(){ return new AppException(RaidErrorCatalog.DECISION_OUTCOME_REQUIRED); }
    public static AppException rationaleRequired(){ return new AppException(RaidErrorCatalog.DECISION_RATIONALE_REQUIRED); }
    public static AppException linkNotFound(UUID id){ return new AppException(RaidErrorCatalog.RAID_LINK_NOT_FOUND, "RAID link not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException linkTypeRequired(){ return new AppException(RaidErrorCatalog.RAID_LINK_TYPE_REQUIRED); }
    public static AppException targetTypeRequired(){ return new AppException(RaidErrorCatalog.RAID_TARGET_TYPE_REQUIRED); }
    public static AppException actionAlreadyLinked(UUID id){ return new AppException(RaidErrorCatalog.RAID_ACTION_ALREADY_LINKED, "RAID action already linked: "+id, Map.of("id", id==null?"":id)); }
    public static AppException actionNotOpen(){ return new AppException(RaidErrorCatalog.RAID_ACTION_NOT_OPEN); }
    public static AppException noProjectPhase(UUID projectId){ return new AppException(RaidErrorCatalog.RAID_NO_PROJECT_PHASE, "No active phase for project: "+projectId, Map.of("projectId", projectId==null?"":projectId)); }
    public static AppException decisionOptionNotFound(UUID id){ return new AppException(RaidErrorCatalog.DECISION_OPTION_NOT_FOUND, "Decision option not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException decisionReplacementRequired(){ return new AppException(RaidErrorCatalog.DECISION_REPLACEMENT_REQUIRED); }
    public static AppException decisionLinkNotFound(UUID id){ return new AppException(RaidErrorCatalog.DECISION_LINK_NOT_FOUND, "Decision link not found: "+id, Map.of("id", id==null?"":id)); }
}
