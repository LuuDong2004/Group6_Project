package group6.cinema_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3-50 ký tự")
    @Column(name = "user_name", nullable = false, unique = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String userName;

    @Column(name = "phone", nullable = false, unique = true, length = 15, columnDefinition = "VARCHAR(15)")
    private String phone;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(name = "email", nullable = false, unique = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String email;
    //@NotBlank(message = "Mật khẩu không được để trống")
    //@Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Column(name = "password", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String password;

    @Column(name = "date_of_birth", nullable = false, length = 10, columnDefinition = "VARCHAR(10)")
    private String dateOfBirth;

    @Column(name = "address", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private AuthProvider provider = AuthProvider.LOCAL;

    public User() {
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public User(String userName, String phone, String email, String password, String dateOfBirth, String address, Role role) {
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.role = role;
        this.provider = AuthProvider.LOCAL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
