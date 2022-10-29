package pl.lodz.p.it.pas.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.pas.dto.CreateRoomDto;
import pl.lodz.p.it.pas.dto.UpdateRoomDto;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.repository.impl.RentRepository;
import pl.lodz.p.it.pas.repository.impl.RoomRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
public class RoomManager {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RentRepository rentRepository;


    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Room> getRoomById(@PathVariable("id") Long id) throws Exception {
        Optional<Room> room = roomRepository.getById(id);

        if (room.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(room.get(), HttpStatus.OK);
        }
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity getAllRooms(@Param("number") Integer number) {
        if (number == null) {
            return new ResponseEntity<>(roomRepository.getAll(), HttpStatus.OK);
        } else {
            Optional<Room> room = roomRepository.getByRoomNumber(number);
            if (room.isPresent()) {
                return new ResponseEntity<>(room.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<Room> addRoom(@Valid @RequestBody CreateRoomDto dto) {
        Room room = new Room(dto.getRoomNumber(), dto.getPrice(), dto.getSize());
        try {
            room = roomRepository.add(room);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(room, HttpStatus.CREATED);

    }

    @GetMapping(value = "/{id}/rents", produces = "application/json")
    public ResponseEntity<List<Rent>> getAllRentsOfRoom(@PathVariable("id") Long id, @Param("past") Boolean past) {
        try {
            if (!roomRepository.existsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<Rent> rents;
            if (past != null) { // find past or active rents
                rents = rentRepository.findByRoomAndStatus(id, past);
            } else { // find all rents
                rents = rentRepository.getByRoomId(id);
            }
            return new ResponseEntity<>(rents, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @Valid @RequestBody UpdateRoomDto dto) {
        Optional<Room> existingRoom = roomRepository.getById(id);

        if (existingRoom.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Room room = existingRoom.get();

        Double newPrice = dto.getPrice();
        Integer newNumber = dto.getRoomNumber();
        Integer newSize = dto.getSize();

        room.setPrice(newPrice == null ? room.getPrice() : newPrice);
        room.setSize(newSize == null ? room.getSize() : newSize);
        room.setRoomNumber(newNumber == null ? room.getRoomNumber() : newNumber);

        Optional<Room> updatedRoom;
        try {
            updatedRoom = roomRepository.update(room);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (updatedRoom.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(updatedRoom.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRoomById(@PathVariable("id") Long id) {
        Optional<Room> roomOptional = roomRepository.getById(id);

        if (roomOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Room room = roomOptional.get();

        List<Rent> rentsForRoom = rentRepository.findByRoomAndStatus(room.getId(), false);
        if (rentsForRoom.isEmpty()) {
            roomRepository.remove(room);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

}
