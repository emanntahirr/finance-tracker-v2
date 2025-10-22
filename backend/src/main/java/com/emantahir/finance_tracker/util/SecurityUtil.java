package com.emantahir.finance_tracker.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

@Component
public class SecurityUtil {

    public String getCurrentUserUsername() {
        //Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //if (principal instanceof UserDetails) {
        //    return ((UserDetails) principal).getUsername();
        //}

        //return principal.toString();
    //}
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated - authentication is null or not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        } else {
            throw new RuntimeException("Unexpected principal type: " + principal.getClass().getName());
        }
    }
}

