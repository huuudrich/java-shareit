package ru.practicum.gateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody(required = false) Object itemRequest,
                                          @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemClient.addItem(itemRequest, userId);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive Long itemId,
                                             @Valid @RequestBody Object itemDto, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemClient.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsWithUser(@Positive @RequestHeader(xSharerUserId) Long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemClient.getAllItemsWithUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam("text") String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                             @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComments(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId,
                                              @Valid @RequestBody Object text) {
        return itemClient.addComment(itemId, userId, text);
    }
}
