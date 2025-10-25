package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    public UserDetailsManager userDetailsService(PasswordEncoder encoder) {
		UserDetails admin = User.withUsername("admin")
				.password(encoder.encode("admin123"))
				.roles("ADMIN")
				.build();
		UserDetails customer = User.withUsername("customer")
				.password(encoder.encode("cust123"))
				.roles("CUSTOMER")
				.build();
		UserDetails staff = User.withUsername("staff")
				.password(encoder.encode("staff123"))
				.roles("STAFF")
				.build();
		UserDetails supplier = User.withUsername("supplier")
				.password(encoder.encode("supp123"))
				.roles("SUPPLIER")
				.build();
		return new InMemoryUserDetailsManager(admin, customer, staff, supplier);
	}

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isStaff = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
            boolean isSupplier = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPPLIER"));
            
            if (isAdmin) {
                response.sendRedirect("/admin/products");
            } else if (isStaff) {
                response.sendRedirect("/reports/staff");
            } else if (isSupplier) {
                response.sendRedirect("/reports/supplier");
            } else {
                response.sendRedirect("/customer");
            }
        };
    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/h2-console/**", "/login", "/signup").permitAll()
					.requestMatchers("/admin/**", "/admin").hasRole("ADMIN")
					.requestMatchers("/cart/**", "/checkout/**").hasAnyRole("CUSTOMER", "ADMIN")
					.requestMatchers("/staff/**", "/staff").hasAnyRole("STAFF", "ADMIN")
					.requestMatchers("/supplier/**", "/supplier").hasAnyRole("SUPPLIER", "ADMIN")
					.requestMatchers("/customer/**", "/customer").hasAnyRole("CUSTOMER", "ADMIN")
					.requestMatchers("/reports/admin/**").hasRole("ADMIN")
					.requestMatchers("/reports/staff/**").hasAnyRole("STAFF", "ADMIN")
					.requestMatchers("/reports/supplier/**").hasAnyRole("SUPPLIER", "ADMIN")
					.requestMatchers("/feedback/admin/**", "/rating/admin/**").hasRole("ADMIN")
					.requestMatchers("/feedback/**", "/rating/**").hasAnyRole("CUSTOMER", "ADMIN")
					.anyRequest().authenticated()
			)
			.formLogin(form -> form
					.loginPage("/login").permitAll()
					.successHandler(roleBasedSuccessHandler())
			)
            .logout(logout -> logout.logoutSuccessUrl("/login"))
			.headers(h -> h.frameOptions(f -> f.disable()))
			.sessionManagement(sm -> sm.sessionCreationPolicy(IF_REQUIRED));

		return http.build();
	}
}


