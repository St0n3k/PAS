package pl.lodz.p.it.pas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.exception.user.ClientTypeNotFoundException;
import pl.lodz.p.it.pas.exception.user.CreateUserException;
import pl.lodz.p.it.pas.exception.user.UpdateUserException;
import pl.lodz.p.it.pas.exception.user.UserNotFoundException;
import pl.lodz.p.it.pas.manager.UserManager;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.Employee;
import pl.lodz.p.it.pas.model.user.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserManager userManager;

    @PostMapping("/clients")
    public ResponseEntity<Client> registerClient(@Valid @RequestBody RegisterClientDTO rcDTO)
            throws ClientTypeNotFoundException, CreateUserException {
        Client client = userManager.registerClient(rcDTO);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> registerEmployee(@Valid @RequestBody RegisterEmployeeDTO reDTO)
            throws CreateUserException {
        Employee employee = userManager.registerEmployee(reDTO);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) throws UserNotFoundException {
        User user = userManager.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@Param("username") String username) {
        List<User> users = userManager.getAllUsers(username);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username)
            throws UserNotFoundException {
        User user = userManager.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping("/{id}/rents")
    public ResponseEntity<List<Rent>> getAllRentsOfClient(@PathVariable("id") Long clientId,
                                                          @Param("past") Boolean past)
            throws UserNotFoundException {
        List<Rent> rents = userManager.getAllRentsOfClient(clientId, past);
        return new ResponseEntity<>(rents, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserDTO dto)
            throws UserNotFoundException, UpdateUserException {
        User user = userManager.updateUser(id, dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable("id") Long id)
            throws UserNotFoundException, UpdateUserException {
        User user = userManager.activateUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable("id") Long id)
            throws UserNotFoundException, UpdateUserException {
        User user = userManager.deactivateUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
