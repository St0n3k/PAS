package pl.lodz.pas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomDTO {
    private Integer roomNumber;
    private Integer size;
    private Double price;
}
