package com.company.scopery.modules.productivity.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class ProductivityExceptions {
    private ProductivityExceptions(){}
    public static AppException savedSearchNotFound(UUID id){ return new AppException(ProductivityErrorCatalog.SAVED_SEARCH_NOT_FOUND,"Saved search not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException savedViewNotFound(UUID id){ return new AppException(ProductivityErrorCatalog.SAVED_VIEW_NOT_FOUND,"Saved view not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException favoriteNotFound(UUID id){ return new AppException(ProductivityErrorCatalog.FAVORITE_NOT_FOUND,"Favorite not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException pinNotFound(UUID id){ return new AppException(ProductivityErrorCatalog.PIN_NOT_FOUND,"Pin not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException inboxNotFound(UUID id){ return new AppException(ProductivityErrorCatalog.INBOX_ITEM_NOT_FOUND,"Inbox item not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException commandNotFound(String code){ return new AppException(ProductivityErrorCatalog.COMMAND_NOT_FOUND,"Command not found: "+code, Map.of("code", code==null?"":code)); }
    public static AppException accessDenied(){ return new AppException(ProductivityErrorCatalog.PRODUCTIVITY_ACCESS_DENIED); }
    public static AppException nameRequired(){ return new AppException(ProductivityErrorCatalog.PRODUCTIVITY_NAME_REQUIRED); }
}
