package pl.lodz.pas.manager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.pas.model.Address;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.user.Admin;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;
import pl.lodz.p.it.pas.model.user.ClientTypes.Default;
import pl.lodz.p.it.pas.model.user.Employee;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.pas.exception.user.CreateUserException;
import pl.lodz.pas.exception.user.UpdateUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;
import pl.lodz.pas.repository.impl.ClientTypeRepository;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.UserRepository;


@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
public class UserManager {

    @Inject
    private UserRepository userRepository;
    @Inject
    private RentRepository rentRepository;
    @Inject
    private ClientTypeRepository clientTypeRepository;


    public Client registerClient(RegisterClientDTO rcDTO) throws CreateUserException {
        Address address = new Address(rcDTO.getCity(), rcDTO.getStreet(), rcDTO.getNumber());
        Optional<ClientType> defaultClientTypeOptional = clientTypeRepository.getByType(Default.class);

        if (defaultClientTypeOptional.isEmpty()) {
            throw new BadRequestException();
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
            throw new CreateUserException();
        }
        return client;
    }


    public Employee registerEmployee(RegisterEmployeeDTO reDTO) throws CreateUserException {
        Employee employee = new Employee(reDTO.getUsername(), reDTO.getFirstName(), reDTO.getLastName());
        employee = (Employee) userRepository.add(employee);

        if (employee == null) {
            throw new CreateUserException();
        }
        return employee;
    }

    public User getUserById(Long id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        return optionalUser.get();
    }

    public List<User> getAllUsers(String username) {
        List<User> users;
        if (username == null) {
            users = userRepository.getAllUsers();
        } else {
            users = userRepository.matchUserByUsername(username);
        }
        return users;
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.getUserByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        return optionalUser.get();
    }

    public List<Rent> getAllRentsOfClient(Long clientId, Boolean past) throws UserNotFoundException {
        if (userRepository.getById(clientId).isEmpty()) {
            throw new UserNotFoundException();
        }
        List<Rent> rents;
        if (past != null) { // find past or active rents
            rents = rentRepository.findByClientAndStatus(clientId, past);
        } else { // find all rents
            rents = rentRepository.getByClientId(clientId);
        }
        return rents;
    }

    public User updateUser(Long id, UpdateUserDTO dto) throws UserNotFoundException, UpdateUserException {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = optionalUser.get();

        String username = dto.getUsername();
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        String personalId = dto.getPersonalId();
        String city = dto.getCity();
        String street = dto.getStreet();
        Integer number = dto.getNumber();

        if (username != null && userRepository.getUserByUsername(username).isPresent()) {
            throw new UpdateUserException();
        }
        User updatedUser;
        if (Objects.equals(user.getRole(), "CLIENT")) {
            Client client = (Client) user;

            client.setUsername(username == null ? client.getUsername() : username);
            client.setFirstName(firstName == null ? client.getFirstName() : firstName);
            client.setLastName(lastName == null ? client.getLastName() : lastName);
            client.setPersonalId(personalId == null ? client.getPersonalId() : personalId);

            Address address = client.getAddress();

            address.setCity(city == null ? address.getCity() : city);
            address.setStreet(street == null ? address.getStreet() : street);
            address.setHouseNumber(number == null ? address.getHouseNumber() : number);

            updatedUser = client;
        } else if (Objects.equals(user.getRole(), "EMPLOYEE")) {
            Employee employee = (Employee) user;

            employee.setUsername(username == null ? employee.getUsername() : username);
            employee.setFirstName(firstName == null ? employee.getFirstName() : firstName);
            employee.setLastName(lastName == null ? employee.getLastName() : lastName);

            updatedUser = employee;

        } else if (Objects.equals(user.getRole(), "ADMIN")) {
            Admin admin = (Admin) user;

            admin.setUsername(username == null ? admin.getUsername() : username);

            updatedUser = admin;
        } else {
            throw new UpdateUserException();
        }

        try {
            optionalUser = userRepository.update(updatedUser);
        } catch (Exception e) {
            throw new UpdateUserException();
        }

        if (optionalUser.isEmpty()) {
            throw new UpdateUserException();
        }
        user = optionalUser.get();
        return user;
    }

    public User activateUser(Long id) throws UserNotFoundException, UpdateUserException {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = optionalUser.get();
        user.setActive(true);

        optionalUser = userRepository.update(user);
        if (optionalUser.isEmpty()) {
            throw new UpdateUserException();
        }
        user = optionalUser.get();
        return user;
    }

    public User deactivateUser(Long id) throws UpdateUserException, UserNotFoundException {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = optionalUser.get();
        user.setActive(false);

        optionalUser = userRepository.update(user);
        if (optionalUser.isEmpty()) {
            throw new UpdateUserException();
        }
        user = optionalUser.get();
        return user;
    }

}
