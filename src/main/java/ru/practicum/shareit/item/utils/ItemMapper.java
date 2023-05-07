package ru.practicum.shareit.item.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .owner(itemDto.getOwner())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(item.getOwner())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static ItemDetailsDto itemDetailsDto(Item item, ItemDetailsDto.BookingInfo lastBooking,
                                         ItemDetailsDto.BookingInfo nextBooking, List<Comment> comments) {
        return ItemDetailsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(CommentMapper.toCommentDtoList(comments))
                .build();
    }

    public static List<ItemDto> userListToDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    public static Item requestToItem(ItemWithRequest itemDto, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .owner(itemDto.getOwner())
                .request(itemRequest).build();
    }

    public static ItemWithRequest itemToRequest(Item item, ItemRequest itemRequest) {
        return ItemWithRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .owner(item.getOwner())
                .requestId(itemRequest != null ? itemRequest.getId() : null)
                .build();
    }

    public static List<ItemWithRequest> itemListToRequest(List<Item> items, ItemRequest itemRequest) {
        List<ItemWithRequest> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemToRequest(item, itemRequest));
        }
        return itemsDto;
    }
}
