package pl.lodz.p.it.pas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterClientDTO {

    @NotNull
    private String username;

    @NotNull
    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String firstName;

    @NotNull
    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String lastName;

    @NotNull
    @Pattern(regexp = "[0-9]+")
    private String personalID;

    @NotNull
    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String city;

    @NotNull
    @Pattern(regexp = "[^\\d\\s!?_#$%^&*()@=+,.|/~`'\"\\\\]+")
    private String street;

    @NotNull
    @Min(0)
    private Integer number;
}
