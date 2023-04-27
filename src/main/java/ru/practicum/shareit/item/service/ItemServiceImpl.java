package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    public ItemDto addItem(Item item, Long userId) {
        log.info("Adding item with name: {} for user with id: {}", item.getName(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public Comment addComment(Long itemId, Long userId, CommentRequest request) throws Exception {
        String text = request.getText();
        if (bookingRepository.findAllByBookerAndItemId(userId, itemId).isEmpty() || text == null || text.trim().isEmpty()) {
            throw new Exception(String.format("Error for added comment with user id: %d ", userId));
        }
        Item item = itemRepository.getReferenceById(itemId);
        User author = userRepository.getReferenceById(userId);
        Comment comment = new Comment(text, item, author, LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public ItemDto updateItem(Long itemId, Item item, Long userId) {
        log.info("Updating item with id: {} for user with id: {}", itemId, userId);
        Item existingItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Item with id " + itemId + " not found for user with id " + userId));

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(existingItem));
    }

    public ItemDto getItem(Long itemId, Long userId) {
        log.info("Getting item with id: {} for user with id: {}", itemId, userId);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        }
        return itemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item with id " + itemId + " not found for user with id " + userId)));
    }

    public List<ItemDetailsDto> getAllItemsWithUser(Long userId) {
        log.info("Getting all items for user with id: {}", userId);

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);

        return items.stream()
                .map(item -> constructItemDtoForOwner(item.getOwner(), item, commentRepository.findAllByItem_Id(item.getId())))
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text) {
        log.info("Searching items with text: {}", text);
        if (text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.userListToDto(itemRepository.search(text));
    }

    public Object getItemDetails(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        User user = userRepository.getReferenceById(userId);
        List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());

        return constructItemDtoForOwner(user, item, comments);
    }

    private ItemDetailsDto constructItemDtoForOwner(User owner, Item item, List<Comment> comments) {
        PageRequest topTwo = PageRequest.of(0, 2);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndEndBefore(owner.getId(), item.getId(), topTwo);
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (!bookings.isEmpty()) {
            lastBooking = bookings.get(0);
            if (bookings.size() > 1) {
                nextBooking = bookings.get(1);
            }
        }
        return itemMapper.itemDetailsDto(item,
                BookingMapper.bookingInItemDto(lastBooking),
                BookingMapper.bookingInItemDto(nextBooking), comments);
    }
}
