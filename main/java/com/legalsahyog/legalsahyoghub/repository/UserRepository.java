package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByIsActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.city = :city AND u.isActive = true")
    List<User> findByCityAndActive(@Param("city") String city);
    
    @Query("SELECT u FROM User u WHERE u.state = :state AND u.isActive = true")
    List<User> findByStateAndActive(@Param("state") String state);
}
