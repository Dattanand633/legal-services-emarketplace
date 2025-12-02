package com.legalsahyog.legalsahyoghub.security;

import com.legalsahyog.legalsahyoghub.entity.Admin;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.repository.AdminRepository;
import com.legalsahyog.legalsahyoghub.repository.ProviderRepository;
import com.legalsahyog.legalsahyoghub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find user first
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent() && user.get().getIsActive()) {
            return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
        
        // Try to find provider
        Optional<Provider> provider = providerRepository.findByEmail(username);
        if (provider.isPresent() && provider.get().getIsActive()) {
            return new org.springframework.security.core.userdetails.User(
                provider.get().getEmail(),
                provider.get().getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROVIDER"))
            );
        }
        
        // Try to find admin
        Optional<Admin> admin = adminRepository.findByEmail(username);
        if (admin.isPresent() && admin.get().getIsActive()) {
            String role = admin.get().getRole() == Admin.Role.SUPER_ADMIN ? "ROLE_SUPER_ADMIN" : "ROLE_ADMIN";
            return new org.springframework.security.core.userdetails.User(
                admin.get().getEmail(),
                admin.get().getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        }
        
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}
