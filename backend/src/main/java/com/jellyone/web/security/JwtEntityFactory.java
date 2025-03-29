package com.jellyone.web.security;

import com.jellyone.domain.User;
import com.jellyone.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JwtEntityFactory {

    public static JwtEntity create(User user) {
        return new JwtEntity(
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthorities(Collections.singletonList(user.getRole())),
                user.getId()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }
}
