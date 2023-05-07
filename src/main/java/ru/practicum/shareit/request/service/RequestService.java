package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(ItemRequest itemRequest, Long userId);

    ItemRequestDto getRequest(Long userId, Long requestId);

    List<ItemRequestDto> getAllRequests(Long userId);

    List<ItemRequestDto> getAllRequestsPagination(Long userId, Pageable pageable);
}
