package pl.lodz.pas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterClientDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String personalID;
    private String city;
    private String street;
    private Integer number;
}
