package pl.lodz.p.it.pas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private String username;

    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String firstName;

    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String lastName;

    @Pattern(regexp = "[0-9]+")
    private String personalId;

    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String city;

    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String street;

    @Min(0)
    private Integer number;
}
