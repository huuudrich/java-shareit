package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items;
    private final UserRepository userRepository;
    private final AtomicLong idCounter;

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.items = new HashMap<>();
        this.idCounter = new AtomicLong(1);
    }

    @Override
    public Item addItem(Item item, Long userId) {
        if (!mapUsers().containsKey(userId)) {
            throw new NotFoundException(String.format("User not found with id: %d", userId));
        }
        item.setOwner(mapUsers().get(userId));
        long newId = idCounter.getAndIncrement();
        item.setId(newId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item, Long userId) {
        if (items.containsKey(itemId) && mapUsers().containsKey(userId)) {
            item.setOwner(mapUsers().get(userId));
            setFields(item, itemId);
            items.put(itemId, item);
            return item;
        } else {
            throw new NotFoundException(String.format("User or Item not found with userId: %d, itemId: %d", userId, itemId));
        }
    }

    @Override
    public Item getItem(Long itemId, Long userId) {
        if (items.containsKey(itemId) && mapUsers().containsKey(userId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException(String.format("Item not found with id: %d", itemId));
        }
    }

    @Override
    public List<Item> getAllItemsWithUser(Long userId) {
        if (!mapUsers().containsKey(userId)) {
            throw new NotFoundException(String.format("User not found with id: %d", userId));
        }
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        String normalizedSearchText = text.toLowerCase();
        if (text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(normalizedSearchText)
                        || item.getDescription().toLowerCase().contains(normalizedSearchText)))
                .collect(Collectors.toList());
    }

    private Map<Long, User> mapUsers() {
        List<User> usersList = userRepository.findAll();
        Map<Long, User> usersMap = usersList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        return usersMap;
    }

    private void setFields(Item item, Long itemId) {
        Item oldItem = items.get(itemId);
        if (!Objects.equals(item.getOwner().getId(), oldItem.getOwner().getId()))
            throw new NotFoundException("The item belongs to other user");
        if (item.getId() == null) item.setId(itemId);
        if (item.getName() == null) item.setName(oldItem.getName());
        if (item.getDescription() == null) item.setDescription(oldItem.getDescription());
        if (item.getAvailable() == null) item.setAvailable(oldItem.getAvailable());
        if (item.getOwner() == null) item.setOwner(oldItem.getOwner());
    }
}
