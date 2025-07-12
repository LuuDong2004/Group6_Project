package group6.cinema_project.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import group6.cinema_project.entity.AuthProvider;
import group6.cinema_project.entity.Role;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.security.oauth2.CustomOAuth2User;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
   @Autowired
    private UserRepository userRepository;

   @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
         OAuth2User oAuth2User = super.loadUser(userRequest);
         String email = oAuth2User.getAttribute("email");
         String name = oAuth2User.getAttribute("name");
         String givenName = oAuth2User.getAttribute("given_name");
         String familyName = oAuth2User.getAttribute("family_name");
         String picture = oAuth2User.getAttribute("picture");

         System.out.println("OAuth2 Login - Email: " + email);
         System.out.println("OAuth2 Login - Name: " + name);

         Optional<User> userOpt = userRepository.findByEmail(email);
         User user;
         if(userOpt.isPresent()) {
             user = userOpt.get();
             System.out.println("Found existing user: " + user.getEmail() + " with role: " + user.getRole());
             // Update existing user's information if needed
             if (user.getUserName() == null || user.getUserName().isEmpty()) {
                 user.setUserName(name != null ? name : email);
             }
             if (user.getProvider() == null) {
                 user.setProvider(AuthProvider.GOOGLE);
             }
             userRepository.save(user);
         } else {
             // If the user does not exist, create a new user
             System.out.println("Creating new OAuth2 user with email: " + email);
             user = new User();
             user.setEmail(email);
             user.setUserName(name != null ? name : email);
             user.setRole(Role.USER);
             user.setProvider(AuthProvider.GOOGLE);
             user.setPassword(null); // OAuth2 users don't need password
             user.setPhone(null); // Can be updated later
             user.setDateOfBirth(null); // Can be updated later
             user.setAddress(null); // Can be updated later

             user = userRepository.save(user);
             System.out.println("Created new user with ID: " + user.getId());
         }
         return new CustomOAuth2User(oAuth2User, user);
    }
}
