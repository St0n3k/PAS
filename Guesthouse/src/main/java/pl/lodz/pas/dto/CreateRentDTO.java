package pl.lodz.pas.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateRentDTO {
    @NotNull
    @Future
    private LocalDateTime beginTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    private boolean board;

    @NotNull
    private Long clientId;

    @NotNull
    private Long roomId;

    @AssertTrue
    private boolean isEndDateAfterBeginDate() {
        return endTime.isAfter(beginTime);
    }
}
