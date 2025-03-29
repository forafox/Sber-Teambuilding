package com.jellyone.web.security.principal;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    String getAuthName();
    void setAdminRole();
    boolean isAdmin();
}
