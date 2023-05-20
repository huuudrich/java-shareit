package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exceptions.NotValidCommentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.utils.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.utils.ItemMapper.*;
import static ru.practicum.shareit.request.dto.ItemRequestDto.toItemRequestDto;
import static ru.practicum.shareit.user.utils.UserMapper.toUser;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    public Object addItem(ItemWithRequest itemWithRequest, Long userId) {

        ItemRequest request = null;
        List<ItemWithRequest> items = new ArrayList<>();

        if (itemWithRequest.getRequestId() != null) {
            Optional<ItemRequest> optionalRequest = requestRepository.findById(itemWithRequest.getRequestId());

            if (optionalRequest.isPresent()) {
                request = optionalRequest.get();
                items.add(itemWithRequest);
                toItemRequestDto(request).setItems(items);
            }
        }

        Item item = itemWithRequestToItem(itemWithRequest, request);

        log.info("Adding item with name: {} for user with id: {}", item.getName(), userId);

        User user = toUser(userService.getUser(userId));
        item.setOwner(user);

        if (request == null) {
            return toItemDto(itemRepository.save(item));
        }
        return toItemWithRequest(itemRepository.save(item), request);
    }

    public CommentDto addComment(Long itemId, Long userId, CommentRequest textRequest) {
        String text = textRequest.getText();
        List<Booking> bookings = bookingRepository.findAllByBookerAndItemId(userId, itemId);
        if (bookings.isEmpty()) {
            throw new NotValidCommentException(String.format("Error for added comment with user id: %d ", userId));
        }

        if (bookings.stream().allMatch(b -> b.getStart().isAfter(LocalDateTime.now()))) {
            throw new NotValidCommentException(String.format("Cannot add comment for future booking with user id: %d ", userId));
        }
        Item item = itemRepository.getReferenceById(itemId);
        User author = userRepository.getReferenceById(userId);
        Comment comment = new Comment(text, item, author, LocalDateTime.now());
        commentRepository.save(comment);
        return toCommentDto(comment);
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

        return toItemDto(itemRepository.save(existingItem));
    }

    public ItemDto getItem(Long itemId, Long userId) {
        log.info("Getting item with id: {} for user with id: {}", itemId, userId);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        }
        return toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item with id " + itemId + " not found for user with id " + userId)));
    }

    public List<ItemDetailsDto> getAllItemsWithUser(Long userId, Pageable pageable) {
        log.info("Getting all items for user with id: {}", userId);

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable).getContent();

        return items.stream()
                .map(item -> constructItemDtoForOwner(item.getOwner(), item, commentRepository.findAllByItem_Id(item.getId())))
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text, Pageable pageable) {
        log.info("Searching items with text: {}", text);
        if (text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return toItemListDto(itemRepository.search(text, pageable).getContent());
    }

    public Object getItemDetails(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        User user = userRepository.getReferenceById(userId);
        List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());

        return constructItemDtoForOwner(user, item, comments);
    }

    private ItemDetailsDto constructItemDtoForOwner(User owner, Item item, List<Comment> comments) {
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookingRepository.findLastBooking(owner.getId(), item.getId(), now)
                .stream().findFirst().orElse(null);
        Booking nextBooking = bookingRepository.findNextBooking(owner.getId(), item.getId(), now)
                .stream().findFirst().orElse(null);

        return toItemDetailsDto(item,
                BookingMapper.bookingInItemDto(lastBooking),
                BookingMapper.bookingInItemDto(nextBooking), comments);
    }
}
