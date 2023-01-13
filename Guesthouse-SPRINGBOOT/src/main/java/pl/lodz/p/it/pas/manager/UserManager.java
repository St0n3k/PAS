package pl.lodz.p.it.pas.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.exception.user.ClientTypeNotFoundException;
import pl.lodz.p.it.pas.exception.user.CreateUserException;
import pl.lodz.p.it.pas.exception.user.UpdateUserException;
import pl.lodz.p.it.pas.exception.user.UserNotFoundException;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
@RequestScope
@Service
public class UserManager {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RentRepository rentRepository;

    @Autowired
    private final ClientTypeRepository clientTypeRepository;


    public Client registerClient(RegisterClientDTO rcDTO)
            throws ClientTypeNotFoundException, CreateUserException {
        Address address = new Address(rcDTO.getCity(), rcDTO.getStreet(), rcDTO.getNumber());
        Optional<ClientType> defaultClientTypeOptional = clientTypeRepository.getByType(Default.class);

        if (defaultClientTypeOptional.isEmpty()) {
            throw new ClientTypeNotFoundException();
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

        Employee addedEmployee = (Employee) userRepository.add(employee);

        if (addedEmployee == null) {
            throw new CreateUserException();
        }
        return addedEmployee;
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
                throw new UpdateUserException();
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
                throw new UpdateUserException();
            }
            user = optionalUser.get();
        }
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


    public User deactivateUser(Long id) throws UserNotFoundException, UpdateUserException {
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
