package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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
    public ItemDto addItem(@Valid @RequestBody Item item, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@PathVariable @Positive Long itemId, @Valid @RequestBody ItemDto itemDto, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.updateItem(itemId, Item.toItem(itemDto), userId);
    }

    @GetMapping("{itemId}")
    public Object getItem(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.getItemDetails(itemId, userId);
    }

    @GetMapping
    public List<ItemDetailsDto> getAllItemsWithUser(@Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemService.getAllItemsWithUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam("text") String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("{itemId}/comment")
    public Comment getComments(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId, @RequestBody String text) {
        return itemService.addComment(itemId, userId, text);
    }
}
