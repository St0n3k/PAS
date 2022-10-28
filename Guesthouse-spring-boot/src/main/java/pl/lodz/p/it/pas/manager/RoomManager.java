package pl.lodz.p.it.pas.manager;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.pas.dto.CreateRoomDto;
import pl.lodz.p.it.pas.dto.UpdateRoomDto;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.repository.RoomRepository;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomManager {

    private final RoomRepository roomRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable("id") Long id) {
        return roomRepository.findById(id)
                             .map(ResponseEntity::ok)
                             .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        // TODO add filtering with query params
        return ResponseEntity.ok(roomRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Room> addRoom(@Valid @RequestBody CreateRoomDto dto) {
        // TODO
        return null;
    }

    @GetMapping("/{id}/rents")
    public ResponseEntity<List<Rent>> getAllRentsOfRoom(@PathVariable("id") Long id) {
        // TODO
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@Valid @RequestBody UpdateRoomDto dto) {
        // TODO
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoomById(@PathVariable("id") Long id) {
        roomRepository.deleteById(id);
    }

}
