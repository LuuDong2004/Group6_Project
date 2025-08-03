package group6.cinema_project.service.User.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import group6.cinema_project.dto.Login.AdminPasswordResetDto;
import group6.cinema_project.dto.Login.ChangePasswordDto;
import group6.cinema_project.dto.Login.PasswordResetConfirmDto;
import group6.cinema_project.dto.Login.PasswordResetRequestDto;
import group6.cinema_project.dto.Login.UserLoginDto;
import group6.cinema_project.dto.Login.UserRegistrationDto;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.entity.Enum.AuthProvider;
import group6.cinema_project.entity.Enum.Role;
import group6.cinema_project.entity.PasswordResetToken;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.User.PasswordResetTokenRepository;
import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.service.User.IUserService;
import group6.cinema_project.service.User.MailService;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private MailService emailService;

    @Value("${domain}")
    private String baseUrl;

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
        User user = new User();
        user.setUserName(registrationDto.getUserName());
        user.setPhone(registrationDto.getPhone());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        // Nếu không có dateOfBirth hoặc address thì set null
        user.setDateOfBirth(registrationDto.getDateOfBirth() != null ? registrationDto.getDateOfBirth() : null);
        user.setAddress(registrationDto.getAddress() != null ? registrationDto.getAddress() : null);
        user.setRole(Role.USER); // Default role
        user.setProvider(AuthProvider.LOCAL); // Đăng ký thường là LOCAL

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
//        if (!existingUser.getUserName().equals(userDto.getUserName()) &&
//                isUsernameExists(userDto.getUserName())) {
//            throw new IllegalArgumentException("Username already exists");
//        }

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
    public void updateUserRole(int userId, String newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        User user = userOpt.get();

        // Validate role
        try {
            Role role = Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        }

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

    @Override
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean requestPasswordReset(PasswordResetRequestDto requestDto) {
        Optional<User> userOpt = userRepository.findByEmail(requestDto.getEmail());
        if (userOpt.isEmpty()) {
            // Don't reveal if email exists or not for security
            return true;
        }

        User user = userOpt.get();

        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUserEmail(user.getEmail());

        // Generate new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(resetToken);

        // Send email
        String resetLink = baseUrl + "/reset-password/confirm?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink, user.getUserName());

        return true;
    }

    @Override
    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        return !resetToken.isExpired() && !resetToken.isUsed();
    }

    @Override
    public boolean confirmPasswordReset(PasswordResetConfirmDto confirmDto) {
        if (!confirmDto.isPasswordsMatching()) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(confirmDto.getToken());
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Token không hợp lệ");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Token đã hết hạn");
        }

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token đã được sử dụng");
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(confirmDto.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Send success email
        emailService.sendPasswordResetSuccessEmail(user.getEmail(), user.getUserName());

        return true;
    }

    @Override
    public String resetPassword(int userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) throw new RuntimeException("Không tìm thấy người dùng!");
        var user = userOpt.get();
        // Sinh mật khẩu mới
        String newPassword = generateRandomPassword(8);
        // Mã hóa mật khẩu
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String encoded = encoder.encode(newPassword);
        user.setPassword(encoded);
        userRepository.save(user);
        // TODO: Gửi email chứa mật khẩu mới cho user.getEmail()
        return newPassword;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean adminResetPassword(AdminPasswordResetDto adminPasswordResetDto) {
        Optional<User> userOpt = userRepository.findById(adminPasswordResetDto.getUserId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + adminPasswordResetDto.getUserId());
        }

        User user = userOpt.get();
        String newPassword;

        // Xác định mật khẩu mới
        if (adminPasswordResetDto.hasCustomPassword()) {
            newPassword = adminPasswordResetDto.getCustomPassword();
        } else {
            newPassword = generateRandomPassword(8);
        }

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Gửi email nếu được yêu cầu
        if (adminPasswordResetDto.isSendEmail()) {
            try {
                // Lấy thông tin admin hiện tại (có thể lấy từ SecurityContext)
                String adminName = "Quản trị viên";
                emailService.sendAdminPasswordResetEmail(user.getEmail(), user.getUserName(), newPassword, adminName);
            } catch (Exception e) {
                System.err.println("Error sending admin reset email: " + e.getMessage());
                // Không throw exception vì reset password đã thành công
            }
        }

        return true;
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.util.Random rnd = new java.util.Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    // Helper method to convert User entity to UserDto
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword(null); // Không expose password
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        return dto;
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
