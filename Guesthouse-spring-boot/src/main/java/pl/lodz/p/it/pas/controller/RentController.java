package pl.lodz.p.it.pas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.dto.UpdateRentBoardDTO;
import pl.lodz.p.it.pas.exception.*;
import pl.lodz.p.it.pas.exception.rent.CreateRentException;
import pl.lodz.p.it.pas.exception.rent.RemoveRentException;
import pl.lodz.p.it.pas.exception.rent.RentNotFoundException;
import pl.lodz.p.it.pas.exception.rent.UpdateRentException;
import pl.lodz.p.it.pas.exception.room.RoomNotFoundException;
import pl.lodz.p.it.pas.exception.user.InactiveUserException;
import pl.lodz.p.it.pas.exception.user.UserNotFoundException;
import pl.lodz.p.it.pas.manager.RentManager;
import pl.lodz.p.it.pas.model.Rent;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rents")
@RequiredArgsConstructor
public class RentController {

    @Autowired
    private final RentManager rentManager;


    @PostMapping
    public ResponseEntity<Rent> rentRoom(@Valid @RequestBody CreateRentDTO createRentDTO)
            throws
            UserNotFoundException,
            RoomNotFoundException,
            InactiveUserException,
            CreateRentException,
            InvalidInputException {
        Rent rent = rentManager.rentRoom(createRentDTO);
        return new ResponseEntity<>(rent, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Rent> getRentById(@PathVariable("id") Long id)
            throws RentNotFoundException {
        Rent rent = rentManager.getRentById(id);
        return new ResponseEntity<>(rent, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Rent>> getAllRents() {
        List<Rent> rents = rentManager.getAllRents();
        return new ResponseEntity<>(rents, HttpStatus.OK);
    }


    @PatchMapping("/{id}/board")
    public ResponseEntity<Rent> updateRentBoard(@PathVariable("id") Long id, @RequestBody UpdateRentBoardDTO dto)
            throws InvalidInputException, UpdateRentException, RentNotFoundException {
        Rent rent = rentManager.updateRentBoard(id, dto);
        return new ResponseEntity<>(rent, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Rent> removeRent(@PathVariable("id") Long rentId)
            throws RemoveRentException {
        rentManager.removeRent(rentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
