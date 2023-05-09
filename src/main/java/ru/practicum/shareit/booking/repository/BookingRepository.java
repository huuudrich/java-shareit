package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.id = ?1 and " +
            "(b.booker.id = ?2 or b.item.owner.id = ?2)")
    Optional<Booking> findByIdAndBookerIdOrOwnerId(Long bookingId, Long bookerIdOrOwnerId);

    //state = FUTURE
    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    //state = PAST
    Page<Booking> findAllByBookerIdAndEndIsBeforeAndStatusNotLike(Long bookerId, LocalDateTime now, StatusBooking status, Pageable pageable);

    //state = CURRENT
    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and ?2 > b.start " +
            "and ?2 < b.end " +
            "order by b.start ASC")
    Page<Booking> findAllByBookerStateCurrent(Long bookerId, LocalDateTime now, Pageable pageable);

    //state = ALL
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    //state = WAITING , REJECTED
    Page<Booking> findAllByBookerIdAndStatusIsLike(Long bookerId, StatusBooking status, Pageable pageable);

    // OWNERS
    //state = FUTURE
    Page<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Pageable pageable);

    //state = PAST
    Page<Booking> findAllByItemOwnerIdAndEndBeforeAndStatusNotLike(Long ownerId, LocalDateTime now, StatusBooking status, Pageable pageable);

    //state = CURRENT
    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and ?2 > b.start " +
            "and ?2 < b.end " +
            "order by b.start ASC")
    Page<Booking> findAllByOwnerStateCurrent(Long ownerId, LocalDateTime now, Pageable pageable);

    //state = ALL
    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    //state = WAITING , REJECTED
    Page<Booking> findAllByItemOwnerIdAndStatusIsLike(Long ownerId, StatusBooking status, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.item.id = ?2 and b.status != 'REJECTED'")
    List<Booking> findAllByBookerAndItemId(Long bookerId, Long itemId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.item.id = ?2 and b.status != 'REJECTED' " +
            "and b.start > ?3 " +
            "order by b.start ASC ")
    List<Booking> findNextBooking(Long ownerId, Long itemId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.item.id = ?2 and b.status != 'REJECTED' " +
            "and b.start < ?3 " +
            "order by b.start DESC ")
    List<Booking> findLastBooking(Long ownerId, Long itemId, LocalDateTime now);
}
