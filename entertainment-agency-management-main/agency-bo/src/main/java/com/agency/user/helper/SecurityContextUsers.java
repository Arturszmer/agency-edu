package com.agency.user.helper;

import com.agency.security.AuthenticationToken;
import com.agency.security.TokenClaims;
import com.agency.user.model.UserProfile;
import com.agency.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextUsers {

    private final UserProfileRepository repository;

    public static String getUsernameFromAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof AuthenticationToken token){
            return token.getUsername();

        } else if (authentication instanceof UsernamePasswordAuthenticationToken token){
            User principal = (User) token.getPrincipal();
            return principal.getUsername();

        } else {
            return "NO USER IN CONTEXT";
        }
    }

    public String getUserEmailFromAuthenticationUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof AuthenticationToken token){
            return token.getEmail();
        } else {
            return "NO USER IN CONTEXT";
        }
    }

    public TokenClaims getTokenClaimsFromToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof AuthenticationToken token){
            return token.getTokenClaims();
        } else {
            throw new ClassCastException("NO CLAIMS IN CONTEXT");
        }
    }

    public UserProfile getUserProfileFromAuthentication(){
        String username = getUsernameFromAuthenticatedUser();
        Optional<UserProfile> userProfileByEmail = repository.findUserProfileByUsername(username);

        if(userProfileByEmail.isEmpty()){
            log.error("WARNING: User with email: {} from authentication is not exists! Contact with admin", username);
            throw new UsernameNotFoundException("User from authentication not found");
        }

        return userProfileByEmail.get();
    }
}
