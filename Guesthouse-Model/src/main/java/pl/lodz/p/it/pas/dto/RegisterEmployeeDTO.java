package pl.lodz.p.it.pas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String firstName;

    @NotNull
    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String lastName;
}
