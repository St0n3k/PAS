package pl.lodz.p.it.pas.manager;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.dto.UpdateRentBoardDTO;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.p.it.pas.repository.impl.RentRepository;
import pl.lodz.p.it.pas.repository.impl.RoomRepository;
import pl.lodz.p.it.pas.repository.impl.UserRepository;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@NoArgsConstructor
@RequestMapping("/rents")
@RestController
public class RentManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RentRepository rentRepository;

    @PostMapping
    public ResponseEntity<Rent> rentRoom(@Valid @RequestBody CreateRentDTO createRentDTO) {
        Optional<User> optionalUser = userRepository.getById(createRentDTO.getClientId());
        Optional<Room> optionalRoom = roomRepository.getById(createRentDTO.getRoomId());

        if (optionalUser.isEmpty() || optionalRoom.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Client client = (Client) optionalUser.get();
        Room room = optionalRoom.get();

        if (!client.isActive()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        double finalCost = calculateTotalCost(createRentDTO.getBeginTime(), createRentDTO.getEndTime(),
                                              room.getPrice(), createRentDTO.isBoard(), client.getClientType());

        Rent rent = new Rent(createRentDTO.getBeginTime(), createRentDTO.getEndTime(), createRentDTO.isBoard(),
                             finalCost, client, room);

        Optional<Rent> optionalRent;

        optionalRent = Optional.ofNullable(rentRepository.add(rent));

        if (optionalRent.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(optionalRent.get(), HttpStatus.CREATED);

    }
//

    @GetMapping("/{id}")
    public ResponseEntity<Rent> getRentById(@PathVariable("id") Long id) {
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalRent.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Rent>> getAllRents() {
        List<Rent> list = rentRepository.getAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
//
//
    @PatchMapping("/{id}/board")
    public ResponseEntity<Rent> updateRentBoard(@PathVariable("id") Long id, @RequestBody UpdateRentBoardDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Rent rentToModify = optionalRent.get();

        rentToModify.setBoard(dto.getBoard());
        double newCost = calculateTotalCost(rentToModify.getBeginTime(),
                                            rentToModify.getEndTime(),
                                            rentToModify.getRoom().getPrice(),
                                            rentToModify.isBoard(),
                                            rentToModify.getClient().getClientType());
        rentToModify.setFinalCost(newCost);

        Optional<Rent> optionalUpdatedRent = rentRepository.update(rentToModify);
        if (optionalUpdatedRent.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(optionalRent.get(), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Rent> removeRent(@PathVariable("id") Long rentId) {
        Optional<Rent> optionalRent = rentRepository.getById(rentId);
        if (optionalRent.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Rent rent = optionalRent.get();
        LocalDateTime now = LocalDateTime.now();

        if (rent.getBeginTime().isAfter(now)) {
            rentRepository.removeById(rentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
       return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    private double calculateTotalCost(LocalDateTime beginTime, LocalDateTime endTime, double costPerDay, boolean board,
                                      ClientType clientType) {
        Duration duration = Duration.between(beginTime, endTime);
        if (board) {
            costPerDay += 50; //Daily board is worth 50
        }
        return clientType.applyDiscount(Math.ceil(duration.toHours() / 24.0) * costPerDay);
    }

}
