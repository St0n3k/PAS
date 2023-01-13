package pl.lodz.p.it.pas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.it.pas.dto.CreateRoomDto;
import pl.lodz.p.it.pas.dto.UpdateRoomDto;
import pl.lodz.p.it.pas.exception.room.CreateRoomException;
import pl.lodz.p.it.pas.exception.room.RoomHasActiveReservationsException;
import pl.lodz.p.it.pas.exception.room.RoomNotFoundException;
import pl.lodz.p.it.pas.exception.room.UpdateRoomException;
import pl.lodz.p.it.pas.manager.RoomManager;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestScope
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    @Autowired
    private final RoomManager roomManager;

    @GetMapping("/{id}")
    public ResponseEntity<Room> getById(@PathVariable("id") Long id) throws RoomNotFoundException {
        Room room = roomManager.getRoomById(id);

        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAll() {
        List<Room> rooms = roomManager.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/search/{number}")
    public ResponseEntity<Room> getByRoomNumber(@PathVariable("number") Integer number) throws RoomNotFoundException {
        var room = roomManager.getByRoomNumber(number);

        return ResponseEntity.ok(room);
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomDto dto) throws CreateRoomException {
        Room created = roomManager.addRoom(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/rents")
    public ResponseEntity<List<Rent>> getRentsByRoomId(@PathVariable("id") Long id,
                                                       @RequestParam(value = "past", required = false) Boolean past)
        throws RoomNotFoundException {
        List<Rent> rents = roomManager.getAllRentsOfRoom(id, past);
        return ResponseEntity.ok(rents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable("id") Long id,
                                       @Valid @RequestBody UpdateRoomDto dto)
        throws RoomNotFoundException, UpdateRoomException {
        Room updatedRoom = roomManager.updateRoom(id, dto);

        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) throws RoomHasActiveReservationsException {
        roomManager.deleteRoomById(id);
    }
}
