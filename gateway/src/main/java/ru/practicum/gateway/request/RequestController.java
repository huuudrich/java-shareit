package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class RequestController {
    private final String xSharerUserId = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody Object itemRequest,
                                                @Positive @RequestHeader(xSharerUserId) Long userId) {
        return requestClient.createRequest(itemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@Positive @RequestHeader(xSharerUserId) Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@Positive @RequestHeader(xSharerUserId) Long userId,
                                             @PathVariable @Positive Long requestId) {
        return requestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsPagination(@Positive @RequestHeader(xSharerUserId) Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        return requestClient.getAllRequestsPagination(userId, from, size);
    }
}
