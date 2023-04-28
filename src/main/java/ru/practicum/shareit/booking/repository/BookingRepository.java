package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.id = ?1 and " +
            "(b.booker.id = ?2 or b.item.owner.id = ?2)")
    Optional<Booking> findByIdAndBookerIdOrOwnerId(Long bookingId, Long bookerIdOrOwnerId);

    //state = FUTURE
    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    //state = PAST
    List<Booking> findAllByBookerIdAndEndIsBeforeAndStatusNotLike(Long bookerId, LocalDateTime now, StatusBooking status, Sort sort);

    //state = CURRENT
    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and ?2 > b.start " +
            "and ?2 < b.end " +
            "order by b.start ASC")
    List<Booking> findAllByBookerStateCurrent(Long bookerId, LocalDateTime now);

    //state = ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    //state = WAITING , REJECTED
    List<Booking> findAllByBookerIdAndStatusIsLike(Long bookerId, StatusBooking status, Sort sort);

    // OWNERS
    //state = FUTURE
    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    //state = PAST
    List<Booking> findAllByItemOwnerIdAndEndBeforeAndStatusNotLike(Long ownerId, LocalDateTime now, StatusBooking status, Sort sort);

    //state = CURRENT
    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and ?2 > b.start " +
            "and ?2 < b.end " +
            "order by b.start ASC")
    List<Booking> findAllByOwnerStateCurrent(Long ownerId, LocalDateTime now);

    //state = ALL
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    //state = WAITING , REJECTED
    List<Booking> findAllByItemOwnerIdAndStatusIsLike(Long ownerId, StatusBooking status, Sort sort);

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
