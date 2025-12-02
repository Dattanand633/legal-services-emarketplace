package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Booking;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUser(User user);
    
    List<Booking> findByProvider(Provider provider);
    
    List<Booking> findByUserAndStatus(User user, Booking.BookingStatus status);
    
    List<Booking> findByProviderAndStatus(Provider provider, Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.provider = :provider AND b.bookingDate = :date AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Booking> findActiveBookingsByProviderAndDate(@Param("provider") Provider provider, @Param("date") LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE b.provider = :provider AND b.bookingDate = :date AND b.startTime = :startTime AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Booking> findConflictingBookings(@Param("provider") Provider provider, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate = :date AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' AND b.provider = :provider")
    List<Booking> findCompletedBookingsByProvider(@Param("provider") Provider provider);
}
