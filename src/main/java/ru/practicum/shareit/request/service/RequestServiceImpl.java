package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemWithRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.utils.ItemMapper.itemListToRequest;
import static ru.practicum.shareit.request.dto.ItemRequestDto.toItemRequestDto;
import static ru.practicum.shareit.item.utils.ItemMapper.itemListToRequestWithoutId;


@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createRequest(ItemRequest itemRequest, Long userId) {
        User user = User.toUser(userService.getUser(userId));
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        requestRepository.save(itemRequest);
        return toItemRequestDto(itemRequest);
    }

    @Transactional
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        User user = User.toUser(userService.getUser(userId));
        ItemRequest request = requestRepository.getReferenceById(requestId);
        ItemRequestDto requestDto = toItemRequestDto(request);
        List<ItemWithRequest> items = itemListToRequest(itemRepository.findAllByRequestId(requestId), request);
        requestDto.setItems(items);
        return requestDto;
    }

    @Transactional
    public List<ItemRequestDto> getAllRequests(Long userId) {
        User user = User.toUser(userService.getUser(userId));
        List<ItemRequest> requests = requestRepository.getAllByRequesterOrderByCreatedDesc(user);
        log.info("Created request with user id: {}", userId);

        return requests.stream().map(request -> {
            ItemRequestDto requestDto = toItemRequestDto(request);
            List<ItemWithRequest> items = itemListToRequest(itemRepository.findAllByRequestId(request.getId()), request);
            requestDto.setItems(items);
            return requestDto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<ItemRequestDto> getAllRequestsPagination(Long userId, Pageable pageable) {
        User user = User.toUser(userService.getUser(userId));

        return requestRepository.findAllOrderByCreatedDesc(pageable)
                .map(request -> {
                    ItemRequestDto requestDto = toItemRequestDto(request);
                    List<ItemWithRequest> items = itemListToRequestWithoutId(itemRepository.findAllByRequestId(request.getId()));
                    requestDto.setItems(items);
                    return requestDto;
                })
                .getContent();
    }
}
