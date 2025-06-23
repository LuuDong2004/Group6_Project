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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import group6.cinema_project.dto.PasswordResetConfirmDto;
import group6.cinema_project.dto.PasswordResetRequestDto;
import group6.cinema_project.entity.PasswordResetToken;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.PasswordResetTokenRepository;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.service.EmailService;
import group6.cinema_project.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PasswordResetTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private PasswordResetToken testToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setUserName("testuser");
        testUser.setPassword("encodedPassword");

        testToken = new PasswordResetToken();
        testToken.setId(1L);
        testToken.setToken("test-token-123");
        testToken.setUser(testUser);
        testToken.setUsed(false);
    }

    @Test
    void testRequestPasswordReset_UserExists() {
        // Arrange
        PasswordResetRequestDto requestDto = new PasswordResetRequestDto("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(testToken);

        // Act
        boolean result = userService.requestPasswordReset(requestDto);

        // Assert
        assertTrue(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordResetTokenRepository).deleteByUserEmail("test@example.com");
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), anyString(), eq("testuser"));
    }

    @Test
    void testRequestPasswordReset_UserNotExists() {
        // Arrange
        PasswordResetRequestDto requestDto = new PasswordResetRequestDto("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.requestPasswordReset(requestDto);

        // Assert
        assertTrue(result);
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testValidateResetToken_ValidToken() {
        // Arrange
        when(passwordResetTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(testToken));

        // Act
        boolean result = userService.validateResetToken("valid-token");

        // Assert
        assertTrue(result);
        verify(passwordResetTokenRepository).findByToken("valid-token");
    }

    @Test
    void testValidateResetToken_InvalidToken() {
        // Arrange
        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.validateResetToken("invalid-token");

        // Assert
        assertFalse(result);
        verify(passwordResetTokenRepository).findByToken("invalid-token");
    }

    @Test
    void testValidateResetToken_ExpiredToken() {
        // Arrange
        testToken.setExpiryDate(java.time.LocalDateTime.now().minusHours(1)); // Expired 1 hour ago
        when(passwordResetTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(testToken));

        // Act
        boolean result = userService.validateResetToken("expired-token");

        // Assert
        assertFalse(result);
        verify(passwordResetTokenRepository).findByToken("expired-token");
    }

    @Test
    void testValidateResetToken_UsedToken() {
        // Arrange
        testToken.setUsed(true);
        when(passwordResetTokenRepository.findByToken("used-token")).thenReturn(Optional.of(testToken));

        // Act
        boolean result = userService.validateResetToken("used-token");

        // Assert
        assertFalse(result);
        verify(passwordResetTokenRepository).findByToken("used-token");
    }

    @Test
    void testConfirmPasswordReset_Success() {
        // Arrange
        PasswordResetConfirmDto confirmDto = new PasswordResetConfirmDto("test-token", "newPassword123", "newPassword123");
        when(passwordResetTokenRepository.findByToken("test-token")).thenReturn(Optional.of(testToken));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(testToken);

        // Act
        boolean result = userService.confirmPasswordReset(confirmDto);

        // Assert
        assertTrue(result);
        verify(passwordResetTokenRepository).findByToken("test-token");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
        verify(passwordResetTokenRepository).save(testToken);
        verify(emailService).sendPasswordResetSuccessEmail("test@example.com", "testuser");
        assertTrue(testToken.isUsed());
    }

    @Test
    void testConfirmPasswordReset_PasswordsNotMatching() {
        // Arrange
        PasswordResetConfirmDto confirmDto = new PasswordResetConfirmDto("test-token", "newPassword123", "differentPassword");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.confirmPasswordReset(confirmDto);
        });
    }

    @Test
    void testConfirmPasswordReset_InvalidToken() {
        // Arrange
        PasswordResetConfirmDto confirmDto = new PasswordResetConfirmDto("invalid-token", "newPassword123", "newPassword123");
        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.confirmPasswordReset(confirmDto);
        });
    }

    @Test
    void testConfirmPasswordReset_ExpiredToken() {
        // Arrange
        testToken.setExpiryDate(java.time.LocalDateTime.now().minusHours(1)); // Expired 1 hour ago
        PasswordResetConfirmDto confirmDto = new PasswordResetConfirmDto("expired-token", "newPassword123", "newPassword123");
        when(passwordResetTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(testToken));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.confirmPasswordReset(confirmDto);
        });
    }

    @Test
    void testConfirmPasswordReset_UsedToken() {
        // Arrange
        testToken.setUsed(true);
        PasswordResetConfirmDto confirmDto = new PasswordResetConfirmDto("used-token", "newPassword123", "newPassword123");
        when(passwordResetTokenRepository.findByToken("used-token")).thenReturn(Optional.of(testToken));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.confirmPasswordReset(confirmDto);
        });
    }
} 