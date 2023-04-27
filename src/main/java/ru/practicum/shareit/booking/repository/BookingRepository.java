package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import org.springframework.data.domain.Pageable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.id = ?1 and " +
            "(b.booker.id = ?2 or b.item.owner.id = ?2)")
    Optional<Booking> findByIdAndBookerIdOrOwnerId(Long bookingId, Long bookerIdOrOwnerId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and current_timestamp < b.start " +
            "order by b.start DESC ")
    List<Booking> findAllBookingsByBookerWithStateFuture(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and current_timestamp > b.start " +
            "and current_timestamp > b.end " +
            "order by b.start DESC")
    List<Booking> findAllBookingsByBookerWithStatePast(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and current_timestamp < b.start " +
            "and current_timestamp > b.end " +
            "order by b.start DESC")
    List<Booking> findAllBookingsByBookerWithStateCurrent(Long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.status = 'WAITING'" +
            "order by b.start DESC")
    List<Booking> findAllByBookerAndStatusIsWaitingOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.status = 'REJECTED'" +
            "order by b.start DESC")
    List<Booking> findAllByBookerAndStatusIsRejectedOrderByStartDesc(Long userId);

    // OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and current_timestamp < b.start " +
            "and current_timestamp < b.end " +
            "order by b.start DESC")
    List<Booking> findAllBookingsByOwnerWithStateFuture(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and current_timestamp > b.start " +
            "and current_timestamp > b.end " +
            "order by b.start DESC")
    List<Booking> findAllBookingsByOwnerWithStatePast(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and current_timestamp < b.start " +
            "and current_timestamp > b.end " +
            "order by b.start DESC")
    List<Booking> findAllBookingsByOwnerWithStateCurrent(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "order by b.start DESC")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.status = 'WAITING'" +
            "order by b.start DESC")
    List<Booking> findAllByOwnerAndStatusIsWaitingOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.status = 'REJECTED'" +
            "order by b.start DESC")
    List<Booking> findAllByOwnerAndStatusIsRejectedOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.item.id = ?2 " +
            "and current_timestamp < b.start " +
            "order by b.start ASC")
    List<Booking> findAllByItemOwnerAndEndBefore(Long userId, Long itemId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.item.id = ?2 and b.start > ?3 " +
            "order by b.start ASC")
    List<Booking> findAllByItemOwnerAndStartAfter(Long userId, Long itemId, ZonedDateTime time);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.item.id = ?2 and b.status != 'REJECTED'")
    List<Booking> findAllByBookerAndItemId(Long bookerId, Long itemId);

}
