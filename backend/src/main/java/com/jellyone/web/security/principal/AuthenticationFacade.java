package com.jellyone.web.security.principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getAuthName() {
        Authentication authentication = getAuthentication();
        return (authentication != null) ? authentication.getName() : "Unknown";
    }

    @Override
    public void setAdminRole() {
        Authentication currentAuth = getAuthentication();
        if (currentAuth == null) {
            return;
        }

        List<GrantedAuthority> newAuthorities = new ArrayList<>(currentAuth.getAuthorities());
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                currentAuth.getPrincipal(),
                currentAuth.getCredentials(),
                newAuthorities
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
