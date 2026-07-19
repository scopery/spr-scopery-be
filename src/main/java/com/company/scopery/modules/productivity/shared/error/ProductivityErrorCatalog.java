package com.company.scopery.modules.productivity.shared.error;
import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;
public enum ProductivityErrorCatalog implements ErrorCatalog {
    SAVED_SEARCH_NOT_FOUND("SAVED_SEARCH_NOT_FOUND","Saved search not found",HttpStatus.NOT_FOUND),
    SAVED_VIEW_NOT_FOUND("SAVED_VIEW_NOT_FOUND","Saved view not found",HttpStatus.NOT_FOUND),
    FAVORITE_NOT_FOUND("FAVORITE_NOT_FOUND","Favorite not found",HttpStatus.NOT_FOUND),
    PIN_NOT_FOUND("PIN_NOT_FOUND","Pinned item not found",HttpStatus.NOT_FOUND),
    INBOX_ITEM_NOT_FOUND("WORK_INBOX_ITEM_NOT_FOUND","Work inbox item not found",HttpStatus.NOT_FOUND),
    COMMAND_NOT_FOUND("COMMAND_NOT_FOUND","Command not found",HttpStatus.NOT_FOUND),
    PRODUCTIVITY_ACCESS_DENIED("PRODUCTIVITY_ACCESS_DENIED","Productivity access denied",HttpStatus.FORBIDDEN),
    PRODUCTIVITY_NAME_REQUIRED("PRODUCTIVITY_NAME_REQUIRED","Name is required",HttpStatus.BAD_REQUEST);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    ProductivityErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}
