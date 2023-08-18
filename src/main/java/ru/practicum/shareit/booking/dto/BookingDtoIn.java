package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Входящий ДТО бронирования
 */
@Data
public class BookingDtoIn {

    @NotNull
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;
    @NotNull
    @Future(message = "Дата окончания не может быть в прошлом")
    private LocalDateTime end;
    @NotNull(message = "Номер бронируемой вещи должен быть указан")
    private Long itemId;

    @AssertTrue(message = "Окончание может быть только после старта")
    public boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }

    @AssertTrue(message = "Окончание не может быть одновременно со стартом")
    public boolean isEndEqualsStart() {
        return end == null || !end.equals(start);
    }

}
