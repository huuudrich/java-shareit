package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl itemServiceImpl;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemServiceImpl itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody Item item, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemServiceImpl.addItem(item, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@PathVariable @Positive Long itemId, @Valid @RequestBody ItemDto itemDto, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemServiceImpl.updateItem(itemId, Item.toItem(itemDto), userId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable @Positive Long itemId, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemServiceImpl.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsWithUser(@Positive @RequestHeader(xSharerUserId) Long userId) {
        return itemServiceImpl.getAllItemsWithUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam("text") String text) {
        return itemServiceImpl.searchItem(text);
    }

}
