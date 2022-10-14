package pl.lodz.nbd;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import pl.lodz.nbd.model.Address;
import pl.lodz.nbd.model.Client;
import pl.lodz.nbd.model.Rent;
import pl.lodz.nbd.model.Room;

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
        assertThrows(ValidationException.class, () -> new Client("Rafał", "Strzałkowski", "0003334", null, null));

        //Null client and room
        assertThrows(ValidationException.class, () -> new Rent(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), true, 1000.0, null, null));
    }
}
