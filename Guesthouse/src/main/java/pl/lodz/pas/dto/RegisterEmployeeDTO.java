package pl.lodz.pas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEmployeeDTO {

    @NotNull
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}
