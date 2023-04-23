package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Item item, Long userId);

    ItemDto updateItem(Long itemId, Item item, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDetailsDto> getAllItemsWithUser(Long userId);

    List<ItemDto> searchItem(String text);

    Object getItemDetails(Long itemId, Long userId);
}
