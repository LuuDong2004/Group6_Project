
package group6.cinema_project.security;

import group6.cinema_project.service.User.Impl.CustomUserDetailsServiceImpl;
import group6.cinema_project.service.User.Impl.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // http
    // .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
    // .csrf(csrf -> csrf.disable());
    // return http.build();
    // }

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            private RequestCache requestCache = new HttpSessionRequestCache();

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                    HttpServletResponse response,
                    org.springframework.security.core.Authentication authentication)
                    throws IOException, ServletException {
                // kiểm tra tiếp tục url trong session truoc
                String continueUrl = (String) request.getSession().getAttribute("continueAfterLogin");
                if (continueUrl != null) {
                    request.getSession().removeAttribute("continueAfterLogin");
                    response.sendRedirect(continueUrl);
                    return;
                }

                SavedRequest savedRequest = requestCache.getRequest(request, response);
                if (savedRequest != null) {
                    String targetUrl = savedRequest.getRedirectUrl();
                    if (isBookingRelatedUrl(targetUrl)) {
                        requestCache.removeRequest(request, response);
                        response.sendRedirect(targetUrl);
                        return;
                    }
                }

                // Phân quyền redirect
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                boolean isStaff = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

                if (isAdmin) {
                    response.sendRedirect("/admin/dashboard");
                } else if (isStaff) {
                    response.sendRedirect("/staff");
                } else {
                    response.sendRedirect("/dashboard");
                }
            }

            private boolean isBookingRelatedUrl(String url) {
                return url.contains("/ticket-booking") ||
                        url.contains("/seat") ||
                        url.contains("/booking") ||
                        url.contains("/showtimes") ||
                        url.contains("/payment");
            }
        };
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/secret-login", "/admin/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**", "/assets/**", "/static/**")
                        .permitAll()
                        .requestMatchers("/admin/secret-login").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .formLogin(form -> form
                        .loginPage("/admin/secret-login")
                        .loginProcessingUrl("/admin/secret-login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/admin/secret-login?error=true")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/secret-login?logout=true")
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**", "/assets/**", "/static/**", "/", "/login",
                                "/register", "/sign_in",
                                "/dashboard", "/movie/**", "/schedule/**", "/about", "/contact", "/ticket-booking",
                                "/payment/sepay/webhook", "/ws/**",
                                "/forgot-password", "/reset-password/confirm", "/api/forgot-password",
                                "/api/reset-password/confirm",
                                "/blog", "/blog/**",
                                "/blogs", "/blogs/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService())))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/payment/sepay/webhook") // Tắt CSRF cho webhook
                        .disable());
        return http.build();
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService();
    }
}
