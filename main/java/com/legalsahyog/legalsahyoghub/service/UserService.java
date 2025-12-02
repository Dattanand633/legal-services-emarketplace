package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(@NonNull Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public User updateUser(@NonNull Long id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setPhone(userDetails.getPhone());
            user.setAddress(userDetails.getAddress());
            user.setCity(userDetails.getCity());
            user.setState(userDetails.getState());
            user.setPincode(userDetails.getPincode());
            user.setProfileImage(userDetails.getProfileImage());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + id);
    }
    
    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> getUsersByCity(String city) {
        return userRepository.findByCityAndActive(city);
    }
    
    public List<User> getUsersByState(String state) {
        return userRepository.findByStateAndActive(state);
    }
    
    public User activateUser(@NonNull Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(true);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + id);
    }
    
    public User deactivateUser(@NonNull Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(false);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + id);
    }
}
