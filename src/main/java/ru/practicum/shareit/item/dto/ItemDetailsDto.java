package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemDetailsDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private BookingInfo lastBooking;
    private BookingInfo nextBooking;

    private List<CommentDto> comments;

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
