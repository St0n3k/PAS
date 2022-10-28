package pl.lodz.p.it.pas.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomDto implements Serializable {
    @NotNull
    @Min(value = 1)
    private int roomNumber;

    @NotNull
    @Min(value = 1)
    private double price;

    @NotNull
    @Min(value = 1)
    private int size;
}
