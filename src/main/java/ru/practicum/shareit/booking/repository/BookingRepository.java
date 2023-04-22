package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.id = ?1 and " +
            "(b.booker.id = ?2 or b.item.owner.id = ?2)")
    Optional<Booking> findByIdAndBookerIdOrOwnerId(Long bookingId, Long bookerIdOrOwnerId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and current_timestamp < b.start " +
            "order by b.start")
    List<Booking> findAllBookingsByBookerWithStateFuture(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and current_timestamp > b.start " +
            "and current_timestamp > b.end " +
            "order by b.start")
    List<Booking> findAllBookingsByBookerWithStatePast(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and current_timestamp < b.start " +
            "and current_timestamp > b.end " +
            "order by b.start")
    List<Booking> findAllBookingsByBookerWithStateCurrent(Long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.status = 'WAITING'" +
            "order by b.start")
    List<Booking> findAllByBookerAndStatusIsWaitingOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.status = 'REJECTED'" +
            "order by b.start")
    List<Booking> findAllByBookerAndStatusIsRejectedOrderByStartDesc(Long userId);

    // OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and current_timestamp < b.start " +
            "and current_timestamp < b.end " +
            "order by b.start")
    List<Booking> findAllBookingsByOwnerWithStateFuture(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and current_timestamp > b.start " +
            "and current_timestamp > b.end " +
            "order by b.start")
    List<Booking> findAllBookingsByOwnerWithStatePast(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and current_timestamp < b.start " +
            "and current_timestamp > b.end " +
            "order by b.start")
    List<Booking> findAllBookingsByOwnerWithStateCurrent(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "order by b.start")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.status = 'WAITING'" +
            "order by b.start")
    List<Booking> findAllByOwnerAndStatusIsWaitingOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.status = 'REJECTED'" +
            "order by b.start")
    List<Booking> findAllByOwnerAndStatusIsRejectedOrderByStartDesc(Long userId);
}
