package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item addItem(@Valid @RequestBody Item item, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("{itemId}")
    public Item updateItem(@PathVariable @Positive Long itemId, @Valid @RequestBody ItemDto itemDto, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.updateItem(itemId, Item.toItem(itemDto), userId);
    }

    @GetMapping("{itemId}")
    public Item getItem(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<Item> getAllItemsWithUser(@Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.getAllItemsWithUser(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam("text") String text) {
        return itemService.searchItem(text);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleCustomNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleCustomValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
