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
            "and now() < b.start " +
            "and now() < b.end " +
            "order by b.start")
    List<Booking> findAllBookingsWithStateFuture(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and now() > b.start " +
            "and now() > b.end " +
            "order by b.start")
    List<Booking> findAllBookingsWithStatePast(Long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.status = 'WAITING'" +
            "order by b.start")
    List<Booking> findAllByBookerAndStatusIsWaitingOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 " +
            "and b.status = 'REJECTED'" +
            "order by b.start")
    List<Booking> findAllByBookerAndStatusIsRejectedOrderByStartDesc(Long userId);
}
