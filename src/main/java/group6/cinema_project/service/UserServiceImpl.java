package group6.cinema_project.service;

import group6.cinema_project.entity.User;
import group6.cinema_project.dto.UserDTO;
import group6.cinema_project.repository.UserRepository;
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
    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        
        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.fullName = user.getFullName();
        dto.phone = user.getPhone();
        dto.address = user.getAddress();
        return dto;
    }
} 