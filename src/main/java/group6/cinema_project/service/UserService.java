package group6.cinema_project.service;

import group6.cinema_project.entity.User;
import group6.cinema_project.dto.UserDto;

public interface UserService {
    User getUserById(Long id);
    UserDto getUserDTOById(Long id);
} 