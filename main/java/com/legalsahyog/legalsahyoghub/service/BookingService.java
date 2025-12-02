package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.Admin;
import com.legalsahyog.legalsahyoghub.entity.Booking;
import com.legalsahyog.legalsahyoghub.entity.Provider;
// Avoid importing the entity `Service` to prevent name clash with Spring's `@Service`
import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.repository.AdminRepository;
import com.legalsahyog.legalsahyoghub.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AdminRepository adminRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(@NonNull Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }

    public List<Booking> getBookingsByProvider(Provider provider) {
        return bookingRepository.findByProvider(provider);
    }

    public List<Booking> getBookingsByUserAndStatus(User user, Booking.BookingStatus status) {
        return bookingRepository.findByUserAndStatus(user, status);
    }

    public List<Booking> getBookingsByProviderAndStatus(Provider provider, Booking.BookingStatus status) {
        return bookingRepository.findByProviderAndStatus(provider, status);
    }

    public List<Booking> getActiveBookingsByProviderAndDate(Provider provider, LocalDate date) {
        return bookingRepository.findActiveBookingsByProviderAndDate(provider, date);
    }

    public List<Booking> getConfirmedBookingsByDate(LocalDate date) {
        return bookingRepository.findConfirmedBookingsByDate(date);
    }

    /**
     * Create a booking.
     *
     * Behavior:
     * - If serviceId is provided, validate and use that service's price.
     * - If serviceId is null, use provider.consultationFee (must be non-null and > 0).
     * - Do NOT auto-create a Service when serviceId is null (so no DB insert into services).
     *
     * NOTE: If bookings.service_id is NOT NULL in DB, saving a booking with null service will fail.
     *       To allow null service relationships, run:
     *         ALTER TABLE bookings ALTER COLUMN service_id DROP NOT NULL;
     */
    public Booking createBooking(Long userId, Long providerId, Long serviceId,
                                 LocalDate bookingDate, LocalTime startTime, LocalTime endTime,
                                 String notes) {

        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }
        if (providerId == null) {
            throw new RuntimeException("Provider ID cannot be null");
        }

        // load user and provider using existing services
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Provider provider = providerService.getProviderById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        com.legalsahyog.legalsahyoghub.entity.Service serviceEntity = null;
        BigDecimal totalAmount;

        if (serviceId != null) {
            // validate provided service
            serviceEntity = serviceService.getServiceById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Invalid serviceId: " + serviceId));
            totalAmount = serviceEntity.getPrice();
            if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Service has invalid price. Please contact provider.");
            }
        } else {
            // No service selected — use provider's consultation fee
            totalAmount = provider.getConsultationFee();
            if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("No service or consultation fee available. Please contact the provider to set a consultation fee.");
            }

            // DO NOT create a Service record here. Booking will reference null service.
            serviceEntity = null;
        }

        // Check for conflicts (existing bookings at same provider/date/time)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(provider, bookingDate, startTime);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot is not available");
        }

        // Calculate amounts (platform fee is 15% of service price)
        BigDecimal platformFee = totalAmount.multiply(new BigDecimal("0.15"));
        BigDecimal providerEarnings = totalAmount.subtract(platformFee);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setProvider(provider);
        booking.setService(serviceEntity); // may be null
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setNotes(notes);
        booking.setTotalAmount(totalAmount);
        booking.setPlatformFee(platformFee);
        booking.setProviderEarnings(providerEarnings);
        booking.setStatus(Booking.BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // Send notifications (best-effort, do not fail booking if notifications fail)
        try {
            String serviceName = (serviceEntity != null && serviceEntity.getTitle() != null)
                    ? serviceEntity.getTitle()
                    : "Legal Consultation";
            String bookingDateStr = bookingDate.toString();
            String bookingTimeStr = startTime.toString();
            String notesStr = notes != null ? notes : "";

            notificationService.notifyNewBooking(
                    userId,
                    providerId,
                    savedBooking.getId(),
                    serviceName,
                    bookingDateStr,
                    bookingTimeStr,
                    notesStr
            );

            // Notify all admins
            String userName = user.getFirstName() + " " + user.getLastName();
            String providerName = provider.getFirstName() + " " + provider.getLastName();
            String adminMessage = String.format(
                    "New booking created:\nUser: %s\nProvider: %s\nService: %s\nDate: %s\nTime: %s%s",
                    userName,
                    providerName,
                    serviceName,
                    bookingDateStr,
                    bookingTimeStr,
                    (notesStr != null && !notesStr.isEmpty()) ? "\nReason/Notes: " + notesStr : ""
            );

            List<Admin> allAdmins = adminRepository.findAll();
            for (Admin admin : allAdmins) {
                if (admin.getIsActive() != null && admin.getIsActive()) {
                    notificationService.createAdminNotification(
                            admin.getId(),
                            "New Booking Created",
                            adminMessage,
                            com.legalsahyog.legalsahyoghub.entity.Notification.NotificationType.BOOKING_CONFIRMED
                    );
                }
            }
        } catch (Exception e) {
            // Do not fail booking creation due to notification errors
            System.err.println("Error sending notifications: " + e.getMessage());
            e.printStackTrace();
        }

        return savedBooking;
    }

    public Booking updateBookingStatus(@NonNull Long id, Booking.BookingStatus status) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setStatus(status);

            // Generate meeting link if confirmed
            if (status == Booking.BookingStatus.CONFIRMED) {
                booking.setMeetingLink(generateMeetingLink(booking));
            }

            return bookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found with id: " + id);
    }

    public Booking confirmBooking(@NonNull Long id) {
        return updateBookingStatus(id, Booking.BookingStatus.CONFIRMED);
    }

    public Booking completeBooking(@NonNull Long id) {
        Booking booking = updateBookingStatus(id, Booking.BookingStatus.COMPLETED);

        // Update provider statistics
        Long providerId = booking.getProvider().getId();
        if (providerId != null) {
            providerService.incrementSessionCount(providerId);
            providerService.updateEarnings(providerId, booking.getProviderEarnings());
        }

        return booking;
    }

    public Booking cancelBooking(@NonNull Long id) {
        return updateBookingStatus(id, Booking.BookingStatus.CANCELLED);
    }

    public void deleteBooking(@NonNull Long id) {
        bookingRepository.deleteById(id);
    }

    public List<LocalTime> getAvailableTimeSlots(Long providerId, LocalDate date) {
        if (providerId == null) {
            throw new RuntimeException("Provider ID cannot be null");
        }
        Provider provider = providerService.getProviderById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        // Get existing bookings for the date
        List<Booking> existingBookings = getActiveBookingsByProviderAndDate(provider, date);

        // Generate available time slots (9 AM to 6 PM, 1-hour slots)
        List<LocalTime> availableSlots = List.of(
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                LocalTime.of(12, 0), LocalTime.of(14, 0), LocalTime.of(15, 0),
                LocalTime.of(16, 0), LocalTime.of(17, 0)
        );

        // Filter out booked slots
        return availableSlots.stream()
                .filter(slot -> existingBookings.stream()
                        .noneMatch(booking -> booking.getStartTime().equals(slot)))
                .toList();
    }

    private String generateMeetingLink(Booking booking) {
        // Generate a unique meeting room ID
        String roomId = "legal-" + booking.getId() + "-" + System.currentTimeMillis();
        return "https://meet.jit.si/" + roomId;
    }
}
