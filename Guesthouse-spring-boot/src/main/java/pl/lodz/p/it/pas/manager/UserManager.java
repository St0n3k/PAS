package pl.lodz.p.it.pas.manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.model.Address;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;
import pl.lodz.p.it.pas.model.user.ClientTypes.Default;
import pl.lodz.p.it.pas.model.user.Employee;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.p.it.pas.repository.impl.ClientTypeRepository;
import pl.lodz.p.it.pas.repository.impl.RentRepository;
import pl.lodz.p.it.pas.repository.impl.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/users")
@RestController
public class UserManager {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentRepository rentRepository;
    @Autowired
    private ClientTypeRepository clientTypeRepository;



    @PostMapping("/clients")
    public ResponseEntity<Client> registerClient(@Valid @RequestBody RegisterClientDTO rcDTO) {
        Address address = new Address(rcDTO.getCity(), rcDTO.getStreet(), rcDTO.getNumber());
        Optional<ClientType> defaultClientTypeOptional = clientTypeRepository.getByType(Default.class);

        if (defaultClientTypeOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Client client = new Client(rcDTO.getUsername(),
                                   rcDTO.getFirstName(),
                                   rcDTO.getLastName(),
                                   rcDTO.getPersonalID(),
                                   address,
                                   defaultClientTypeOptional.get());

        try {
            client = (Client) userRepository.add(client);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> registerEmployee(@Valid @RequestBody RegisterEmployeeDTO reDTO) {
        Employee employee = new Employee(reDTO.getUsername(), reDTO.getFirstName(), reDTO.getLastName());

        Employee addedEmployee = (Employee) userRepository.add(employee);

        if (addedEmployee == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllUsers(@Param("username") String username, @Param("match") boolean match) {
        List<User> users;
        if (username == null) {
            users = userRepository.getAllUsers();
        } else {
            if (!match) {
                Optional<User> optionalUser = userRepository.getUserByUsername(username);

                if (optionalUser.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
            }
            users = userRepository.matchUserByUsername(username);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }



    @GetMapping("/{id}/rents")
    public ResponseEntity<List<Rent>> getAllRentsOfClient(@PathVariable("id") Long clientId,
                                                          @Param("past") Boolean past) {
        if (userRepository.getById(clientId).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Rent> rents;
        if (past != null) { // find past or active rents
            rents = rentRepository.findByClientAndStatus(clientId, past);
        } else { // find all rents
            rents = rentRepository.getByClientId(clientId);
        }
        return new ResponseEntity<>(rents, HttpStatus.OK);

    }


    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserDTO dto) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();

        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        String personalId = dto.getPersonalId();
        String city = dto.getCity();
        String street = dto.getStreet();
        Integer number = dto.getNumber();

        if (Objects.equals(user.getRole(), "EMPLOYEE")) {
            Employee employee = (Employee) user;
            employee.setFirstName(firstName == null ? employee.getFirstName() : firstName);
            employee.setLastName(lastName == null ? employee.getLastName() : lastName);
            optionalUser = userRepository.update(employee);
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            user = optionalUser.get();
        }

        if (Objects.equals(user.getRole(), "CLIENT")) {
            Client client = (Client) user;

            client.setFirstName(firstName == null ? client.getFirstName() : firstName);
            client.setLastName(lastName == null ? client.getLastName() : lastName);
            client.setPersonalId(personalId == null ? client.getPersonalId() : personalId);

            Address address = client.getAddress();

            address.setCity(city == null ? address.getCity() : city);
            address.setStreet(street == null ? address.getStreet() : street);
            address.setHouseNumber(number == null ? address.getHouseNumber() : number);
            optionalUser = userRepository.update(client);
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            user = optionalUser.get();
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable("id") Long id) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setActive(true);

        optionalUser = userRepository.update(user);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user = optionalUser.get();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable("id") Long id) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setActive(false);

        optionalUser = userRepository.update(user);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user = optionalUser.get();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
