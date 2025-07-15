package group6.cinema_project.service.impl;

import group6.cinema_project.entity.User;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User getUserById(Long id) { 
        return userRepository.findById(id).orElse(null); 
    }
    
    @Override
    public UserDto getUserDTOById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.username = user.getUserName();
        dto.email = user.getEmail();
        dto.fullName = user.getUserName(); // Using userName as fullName since fullName doesn't exist
        dto.phone = user.getPhone();
        dto.address = user.getAddress();
        return dto;
    }
} 