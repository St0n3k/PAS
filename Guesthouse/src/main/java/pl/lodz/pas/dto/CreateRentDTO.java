package pl.lodz.pas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateRentDTO {
    @NotNull
    @FutureOrPresent
    private LocalDateTime beginTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    private boolean board;

    @NotNull
    private Long clientId;

    @NotNull
    private Long roomId;
}
