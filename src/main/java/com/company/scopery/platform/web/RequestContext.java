package com.company.scopery.platform.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestContext {

    private String traceId;
    private String userId;
    private String username;
    private String officeCode;
}
