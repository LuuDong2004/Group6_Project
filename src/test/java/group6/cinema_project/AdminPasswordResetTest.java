package group6.cinema_project;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import group6.cinema_project.dto.AdminPasswordResetDto;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.service.EmailService;
import group6.cinema_project.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminPasswordResetTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private AdminPasswordResetDto adminPasswordResetDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setUserName("testuser");
        testUser.setPassword("oldEncodedPassword");

        adminPasswordResetDto = new AdminPasswordResetDto();
        adminPasswordResetDto.setUserId(1);
    }

    @Test
    void testAdminResetPassword_GenerateRandomPassword_SendEmail() {
        // Arrange
        adminPasswordResetDto.setGenerateRandomPassword(true);
        adminPasswordResetDto.setSendEmail(true);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        boolean result = userService.adminResetPassword(adminPasswordResetDto);

        // Assert
        assertTrue(result);
        verify(userRepository).findById(1);
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(testUser);
        verify(emailService).sendAdminPasswordResetEmail(eq("test@example.com"), eq("testuser"), anyString(), eq("Quản trị viên"));
    }

    @Test
    void testAdminResetPassword_CustomPassword_SendEmail() {
        // Arrange
        adminPasswordResetDto.setGenerateRandomPassword(false);
        adminPasswordResetDto.setCustomPassword("customPass123");
        adminPasswordResetDto.setSendEmail(true);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("customPass123")).thenReturn("encodedCustomPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        boolean result = userService.adminResetPassword(adminPasswordResetDto);

        // Assert
        assertTrue(result);
        verify(userRepository).findById(1);
        verify(passwordEncoder).encode("customPass123");
        verify(userRepository).save(testUser);
        verify(emailService).sendAdminPasswordResetEmail(eq("test@example.com"), eq("testuser"), eq("customPass123"), eq("Quản trị viên"));
    }

    @Test
    void testAdminResetPassword_GenerateRandomPassword_NoEmail() {
        // Arrange
        adminPasswordResetDto.setGenerateRandomPassword(true);
        adminPasswordResetDto.setSendEmail(false);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        boolean result = userService.adminResetPassword(adminPasswordResetDto);

        // Assert
        assertTrue(result);
        verify(userRepository).findById(1);
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(testUser);
        verify(emailService, never()).sendAdminPasswordResetEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testAdminResetPassword_CustomPassword_NoEmail() {
        // Arrange
        adminPasswordResetDto.setGenerateRandomPassword(false);
        adminPasswordResetDto.setCustomPassword("customPass123");
        adminPasswordResetDto.setSendEmail(false);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("customPass123")).thenReturn("encodedCustomPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        boolean result = userService.adminResetPassword(adminPasswordResetDto);

        // Assert
        assertTrue(result);
        verify(userRepository).findById(1);
        verify(passwordEncoder).encode("customPass123");
        verify(userRepository).save(testUser);
        verify(emailService, never()).sendAdminPasswordResetEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testAdminResetPassword_UserNotFound() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        adminPasswordResetDto.setUserId(999);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.adminResetPassword(adminPasswordResetDto);
        });
        
        verify(userRepository).findById(999);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendAdminPasswordResetEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testAdminResetPassword_EmailServiceException() {
        // Arrange
        adminPasswordResetDto.setGenerateRandomPassword(true);
        adminPasswordResetDto.setSendEmail(true);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doThrow(new RuntimeException("Email service error"))
            .when(emailService).sendAdminPasswordResetEmail(anyString(), anyString(), anyString(), anyString());

        // Act
        boolean result = userService.adminResetPassword(adminPasswordResetDto);

        // Assert
        assertTrue(result); // Should still return true even if email fails
        verify(userRepository).findById(1);
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(testUser);
        verify(emailService).sendAdminPasswordResetEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testAdminPasswordResetDto_HasCustomPassword() {
        // Test with custom password
        adminPasswordResetDto.setCustomPassword("test123");
        assertTrue(adminPasswordResetDto.hasCustomPassword());
        
        // Test with empty custom password
        adminPasswordResetDto.setCustomPassword("");
        assertFalse(adminPasswordResetDto.hasCustomPassword());
        
        // Test with null custom password
        adminPasswordResetDto.setCustomPassword(null);
        assertFalse(adminPasswordResetDto.hasCustomPassword());
        
        // Test with whitespace custom password
        adminPasswordResetDto.setCustomPassword("   ");
        assertFalse(adminPasswordResetDto.hasCustomPassword());
    }
} 