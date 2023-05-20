package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Valid
public class ItemRequestController {
    private final String xSharerUserId = "X-Sharer-User-Id";
    RequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@Valid @RequestBody ItemRequest itemRequest,
                                                        @Positive @RequestHeader(xSharerUserId) Long userId) {
        ItemRequestDto itemRequestDto = requestService.createRequest(itemRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@Positive @RequestHeader(xSharerUserId) Long userId) {
        List<ItemRequestDto> requests = requestService.getAllRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestDto> getRequest(@Positive @RequestHeader(xSharerUserId) Long userId,
                                                     @PathVariable @Positive Long requestId) {
        ItemRequestDto itemRequestDto = requestService.getRequest(userId, requestId);
        return ResponseEntity.ok(itemRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getRequestsPagination(@Positive @RequestHeader(xSharerUserId) Long userId,
                                                                      @RequestParam(name = "from", defaultValue = "0") @Positive int from,
                                                                      @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequestDto> requests = requestService.getAllRequestsPagination(userId, pageable);
        return ResponseEntity.ok(requests);
    }
}
