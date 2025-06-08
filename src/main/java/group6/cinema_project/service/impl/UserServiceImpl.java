package group6.cinema_project.service.impl;

import group6.cinema_project.dto.ChangePasswordDto;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.dto.UserLoginDto;
import group6.cinema_project.dto.UserRegistrationDto;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        // Validate password matching
        if (!registrationDto.isPasswordsMatching()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if userName already exists
//        if (isUsernameExists(registrationDto.getUserName())) {
//            throw new IllegalArgumentException("Username already exists");
//        }

        // Check if email already exists
        if (isEmailExists(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if phone already exists
        if (isPhoneExists(registrationDto.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create new user entity
        User user = new User(
                registrationDto.getUserName(),
                registrationDto.getPhone(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),// In production, hash this password
                registrationDto.getDateOfBirth(),
                registrationDto.getAddress(),
                "USER" // Default role
        );

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public UserDto loginUser(UserLoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByEmail(loginDto.getEmail());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOpt.get();

        // In production, use password hashing comparison
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }


        return convertToDto(user);
    }

    @Override
    public UserDto updateUserProfile(String email, UserDto userDto) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        User existingUser = userOpt.get();

        // Check if new username/phone already exists (excluding current user)
        if (!existingUser.getUserName().equals(userDto.getUserName()) &&
                isUsernameExists(userDto.getUserName())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (!existingUser.getPhone().equals(userDto.getPhone()) &&
                isPhoneExists(userDto.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Update user fields (không update email và password)
        existingUser.setUserName(userDto.getUserName());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setDateOfBirth(userDto.getDateOfBirth());
        existingUser.setAddress(userDto.getAddress());

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Override
    public boolean changePassword(String email, ChangePasswordDto changePasswordDto) {
        if (!changePasswordDto.isPasswordsMatching()) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();

        // Verify old password
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        return true;
    }




    @Override
    public UserDto getUserById(int id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return convertToDto(userOpt.get());
    }

    @Override
    public UserDto getUserByUsername(String userName) {
        Optional<User> userOpt = userRepository.findByUserName(userName);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with username: " + userName);
        }
        return convertToDto(userOpt.get());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return convertToDto(userOpt.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        User existingUser = userOpt.get();

        // Check if new username/email/phone already exists (excluding current user)
        if (!existingUser.getUserName().equals(userDto.getUserName()) &&
                isUsernameExists(userDto.getUserName())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (!existingUser.getEmail().equals(userDto.getEmail()) &&
                isEmailExists(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (!existingUser.getPhone().equals(userDto.getPhone()) &&
                isPhoneExists(userDto.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Update user fields
        existingUser.setUserName(userDto.getUserName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setDateOfBirth(userDto.getDateOfBirth());
        existingUser.setAddress(userDto.getAddress());
        existingUser.setRole(userDto.getRole());

        // Only update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }


        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Override
    public boolean deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // ✅ THÊM LOG ĐỂ DEBUG
        System.out.println("Found user: " + user.getEmail());
        System.out.println("Password starts with: " + user.getPassword().substring(0, Math.min(10, user.getPassword().length())));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }


    @Override
    public boolean isUsernameExists(String userName) {
        return userRepository.findByUserName(userName).isPresent();
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean isPhoneExists(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    // Helper method to convert User entity to UserDto
    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhone(),
                null, // Don't expose password in DTO
                user.getDateOfBirth(),
                user.getAddress(),
                user.getRole()
        );
    }

    // Helper method to convert UserDto to User entity (if needed)
    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPassword(userDto.getPassword());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setAddress(userDto.getAddress());
        user.setRole(userDto.getRole());
        return user;
    }
}
