package pl.lodz.p.it.pas.controller;

import javax.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.pas.dto.CreateRoomDto;
import pl.lodz.p.it.pas.dto.UpdateRoomDto;
import pl.lodz.p.it.pas.exception.CreateRoomException;
import pl.lodz.p.it.pas.exception.RoomHasActiveReservationsException;
import pl.lodz.p.it.pas.exception.RoomNotFoundException;
import pl.lodz.p.it.pas.exception.UpdateRoomException;
import pl.lodz.p.it.pas.manager.RoomManager;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomDto dto) throws CreateRoomException {
        var created = roomManager.addRoom(dto);

        if (created != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
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
        var updatedRoom = roomManager.updateRoom(id, dto);

        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) throws RoomHasActiveReservationsException {
        roomManager.deleteRoomById(id);
    }
}
