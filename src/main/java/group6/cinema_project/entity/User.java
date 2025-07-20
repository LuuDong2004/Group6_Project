package group6.cinema_project.entity;

import group6.cinema_project.entity.Enum.AuthProvider;
import group6.cinema_project.entity.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3-50 ký tự")
    @Column(name = "username", nullable = false, unique = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String userName;

    @Column(name = "password", nullable = true, length = 100, columnDefinition = "VARCHAR(100)")
    private String password;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(name = "email", nullable = false, unique = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String email;

//    @NotBlank(message = "Số điện thoại không được để trống")
//    @Size(min = 10, max = 11, message = "Số điện thoại không hợp lệ")
    @Column(name = "phone", nullable = true, unique = true, length = 15, columnDefinition = "VARCHAR(15)")
    private String phone;
    @Column(name = "address", nullable = true, length = 100, columnDefinition = "VARCHAR(100)")
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private AuthProvider provider = AuthProvider.LOCAL;
}
