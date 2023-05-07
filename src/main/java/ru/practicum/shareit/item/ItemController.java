package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.CommentRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemWithRequest itemRequest, @Positive @RequestHeader(xSharerUserId) Long userId) {
        Object newItem = itemService.addItem(itemRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive Long itemId,
                                              @Valid @RequestBody ItemDto itemDto, @Positive @RequestHeader(xSharerUserId) Long userId) {
        ItemDto updatedItem = itemService.updateItem(itemId, Item.toItem(itemDto), userId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId) {
        Object itemDetails = itemService.getItemDetails(itemId, userId);
        return ResponseEntity.ok(itemDetails);
    }

    @GetMapping
    public ResponseEntity<List<ItemDetailsDto>> getAllItemsWithUser(@Positive @RequestHeader(xSharerUserId) Long userId) {
        List<ItemDetailsDto> items = itemService.getAllItemsWithUser(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String text) {
        List<ItemDto> items = itemService.searchItem(text);
        return ResponseEntity.ok(items);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> getComments(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId,
                                                  @Valid @RequestBody CommentRequest text) throws Exception {
        CommentDto comment = itemService.addComment(itemId, userId, text);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
}
