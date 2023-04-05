package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item, Long userId);

    Item updateItem(Long itemId, Item item, Long userId);

    Item getItem(Long itemId, Long userId);

    List<Item> getAllItemsWithUser(Long userId);

    List<Item> searchItem(String text);
}
