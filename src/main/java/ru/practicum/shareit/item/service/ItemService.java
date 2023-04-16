package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;

    public Item addItem(Item item, Long userId) {
        log.info("Adding item with name: {} for user with id: {}", item.getName(), userId);
        return itemRepository.addItem(item, userId);
    }

    public Item updateItem(Long itemId, Item item, Long userId) {
        log.info("Updating item with id: {} for user with id: {}", itemId, userId);
        return itemRepository.updateItem(itemId, item, userId);
    }

    public Item getItem(Long itemId, Long userId) {
        log.info("Getting item with id: {} for user with id: {}", itemId, userId);
        return itemRepository.getItem(itemId, userId);
    }

    public List<Item> getAllItemsWithUser(Long userId) {
        log.info("Getting all items for user with id: {}", userId);
        return itemRepository.getAllItemsWithUser(userId);
    }

    public List<Item> searchItem(String text) {
        log.info("Searching items with text: {}", text);
        return itemRepository.searchItem(text);
    }
}
