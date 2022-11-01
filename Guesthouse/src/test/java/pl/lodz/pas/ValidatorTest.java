package pl.lodz.pas;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.model.Address;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.model.user.Client;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorTest {
    @Test
    void validatorTest() {
        //Null city
        assertThrows(ValidationException.class, () -> new Address(null, "Gorna", 11));

        //Price = 0 (has to be greater or equal 1)
        assertThrows(ValidationException.class, () -> new Room(178, 0, 1));

        //Null address and client type
        assertThrows(ValidationException.class, () -> new Client("rs", "Rafał", "Strzałkowski", "0003334", null, null));

        //Null client and room
        assertThrows(ValidationException.class,
                () -> new Rent(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), true, 1000.0,
                        null, null));
    }
}
