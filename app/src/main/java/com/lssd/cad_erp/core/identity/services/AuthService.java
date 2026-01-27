package com.lssd.cad_erp.core.identity.services;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationContext authenticationContext;

    public AuthService(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public boolean isAuthenticated() {
        return authenticationContext.isAuthenticated();
    }

    public void logout() {
        authenticationContext.logout();
    }

    public Optional<UserDetails> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class);
    }

    public Optional<String> getPrincipalName() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername);
    }
}