package group6.cinema_project.service;

import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public User getUserById(Long id) { return userRepository.findById(id).orElse(null); }
} 