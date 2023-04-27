package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemCommentDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private BookingInfo lastBooking;
    private BookingInfo nextBooking;

    private Comment comment;

    @Data
    @Builder
    public static class BookingInfo {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;

        public BookingInfo(Long id, LocalDateTime start, LocalDateTime end, Long bookerId) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.bookerId = bookerId;
        }
    }
}
