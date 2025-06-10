package group6.cinema_project.service.impl;

import group6.cinema_project.entity.AuthProvider;
import group6.cinema_project.entity.Role;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.security.oauth2.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
   @Autowired
    private UserRepository userRepository;

   @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
         OAuth2User oAuth2User = super.loadUser(userRequest);
         String email = oAuth2User.getAttribute("email");
         String name = oAuth2User.getAttribute("name");

         Optional<User> userOpt = userRepository.findByEmail(email);
         User user;
         if(userOpt.isPresent()) {
             user = userOpt.get();
         } else {
             // If the user does not exist, create a new user
             user = new User();
             user.setEmail(email);
             user.setUserName(name); // Assuming name is used as username
             user.setRole(Role.USER);
             user.setProvider(AuthProvider.GOOGLE);// Default role, can be changed based on your requirements
             user.setPassword("");

             userRepository.save(user);
         }
         return new CustomOAuth2User(oAuth2User , user);
    }

}
