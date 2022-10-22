package pl.lodz.pas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEmployeeDTO {
    private String username;
    private String firstName;
    private String lastName;
}
