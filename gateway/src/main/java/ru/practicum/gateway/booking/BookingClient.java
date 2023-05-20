package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Object bookingDto, Long userId) {
        return post("", userId, bookingDto);
    }


    public ResponseEntity<Object> setStatusForBookingByOwner(Long bookingId, long userId, Boolean status) {
        return patch("/" + bookingId + "?approved=" + status, userId);
    }

    public ResponseEntity<Object> getBookingByIdForUserOrOwner(Long bookingId, Long bookerIdOrOwnerId) {
        return get("/" + bookingId, bookerIdOrOwnerId);
    }

    public ResponseEntity<Object> getAllBookings(Long userId, String state, boolean isOwner, int from, int size) {
        Map<String, Object> parameters;
        if (state != null) {
            parameters = Map.of(
                    "state", state,
                    "from", from,
                    "size", size
            );
            if (isOwner) {
                return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
            } else {
                return get("?state={state}&from={from}&size={size}", userId, parameters);
            }
        } else {
            parameters = Map.of(
                    "from", from,
                    "size", size
            );
        }
        if (isOwner) {
            return get("/owner?from={from}&size={size}", userId, parameters);
        } else {
            return get("?from={from}&size={size}", userId, parameters);
        }
    }
}
