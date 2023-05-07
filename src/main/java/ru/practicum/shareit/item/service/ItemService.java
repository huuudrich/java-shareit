package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.CommentRequest;

import java.util.List;

public interface ItemService {
    Object addItem(ItemWithRequest itemDto, Long userId);

    ItemDto updateItem(Long itemId, Item item, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDetailsDto> getAllItemsWithUser(Long userId);

    List<ItemDto> searchItem(String text);

    Object getItemDetails(Long itemId, Long userId);

    CommentDto addComment(Long itemId, Long userId, CommentRequest text) throws Exception;
}
