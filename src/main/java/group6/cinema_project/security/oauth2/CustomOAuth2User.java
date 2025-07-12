package group6.cinema_project.security.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import group6.cinema_project.entity.User;

public class CustomOAuth2User implements OAuth2User {
    private final OAuth2User oAuth2User;
    private final User user;

    public CustomOAuth2User(OAuth2User oAuth2User, User user) {
        this.oAuth2User = oAuth2User;
        this.user = user;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return authorities based on user role
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getName() {
        // Return email as the principal name for Spring Security
        return user.getEmail();
    }
    
    // Getter for the user entity
    public User getUser() {
        return user;
    }
}
