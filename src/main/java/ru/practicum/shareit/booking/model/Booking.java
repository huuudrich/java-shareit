package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import javax.validation.constraints.FutureOrPresent;

@Data
@Builder
@Entity
@Table(name = "bookings", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {
    @Id
    private Long id;

    @FutureOrPresent(message = "Start date must be in the future or present")
    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date")
    private ZonedDateTime start;

    @FutureOrPresent(message = "End date must be in the future or present")
    @NotNull(message = "End date cannot be null")
    @Column(name = "end_date")
    private ZonedDateTime end;

    @NotNull(message = "Item cannot be null")
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull(message = "Booker cannot be null")
    @OneToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    private StatusBooking status;
}
