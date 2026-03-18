package ru.yandex.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    @Query("""
           select b
           from Booking b
           where b.id = :bookingId
             and (b.booker.id = :userId or b.item.owner.id = :userId)
           """)
    Optional<Booking> findAccessibleById(@Param("bookingId") Long bookingId,
                                         @Param("userId") Long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2
    );

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            Long bookerId, LocalDateTime now
    );

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now
    );

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            Long bookerId, Status status
    );

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now1, LocalDateTime now2
    );

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long ownerId, LocalDateTime now
    );

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now
    );

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(
            Long ownerId, Status status
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(
            Long itemId, Status status, LocalDateTime now
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
            Long itemId, Status status, LocalDateTime now
    );

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId,
                                                           Long bookerId,
                                                           Status status,
                                                           LocalDateTime end);
}


