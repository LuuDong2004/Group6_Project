package group6.cinema_project.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        System.out.println("CustomUserDetailsService - Loading user: " + user.getEmail());
        System.out.println("User password length: " + (user.getPassword() != null ? user.getPassword().length() : "null"));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // For OAuth2 users (null or empty password), use a special password that won't be checked
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            // Use a placeholder password for OAuth2 users
            password = "{noop}oauth2_user";
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(password)
                .authorities(authorities)
                .build();
    }
}

