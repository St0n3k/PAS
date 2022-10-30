package pl.lodz.p.it.pas.manager;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import pl.lodz.p.it.pas.exception.CreateRoomException;
import pl.lodz.p.it.pas.exception.RoomHasActiveReservationsException;
import pl.lodz.p.it.pas.exception.RoomNotFoundException;
import pl.lodz.p.it.pas.exception.UpdateRoomException;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.repository.impl.RentRepository;
import pl.lodz.p.it.pas.repository.impl.RoomRepository;


@RequestMapping("/rooms")
@RestController
public class RoomManager {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RentRepository rentRepository;


    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable("id") Long id) throws RoomNotFoundException {
        return roomRepository.getById(id)
                             .orElseThrow(RoomNotFoundException::new);
    }

    @GetMapping
    public ResponseEntity getAllRooms(@Param("number") Integer number) throws RoomNotFoundException {
        if (number == null) {
            return ResponseEntity.ok(roomRepository.getAll());
        } else {
            return roomRepository.getByRoomNumber(number)
                                 .map(ResponseEntity::ok)
                                 .orElseThrow(RoomNotFoundException::new);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room addRoom(@Valid @RequestBody CreateRoomDto dto) throws CreateRoomException {
        try {
            Room room = new Room(dto.getRoomNumber(), dto.getPrice(), dto.getSize());
            return roomRepository.add(room);
        } catch (Exception e) {
            throw new CreateRoomException();
        }
    }

    @GetMapping("/{id}/rents")
    public List<Rent> getAllRentsOfRoom(@PathVariable("id") Long id,
                                        @Param("past") Boolean past)
        throws RoomNotFoundException {

        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException();
        }

        if (past != null) { // find past or active rents
            return rentRepository.findByRoomAndStatus(id, past);
        } else { // find all rents
            return rentRepository.getByRoomId(id);
        }
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable Long id,
                           @Valid @RequestBody UpdateRoomDto dto)
        throws RoomNotFoundException, UpdateRoomException {

        Room room = roomRepository.getById(id)
                                  .orElseThrow(RoomNotFoundException::new);

        Double newPrice = dto.getPrice();
        Integer newNumber = dto.getRoomNumber();
        Integer newSize = dto.getSize();

        room.setPrice(newPrice == null ? room.getPrice() : newPrice);
        room.setSize(newSize == null ? room.getSize() : newSize);
        room.setRoomNumber(newNumber == null ? room.getRoomNumber() : newNumber);

        try {
            return roomRepository.update(room).get();
        } catch (Exception e) {
            throw new UpdateRoomException();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoomById(@PathVariable("id") Long id) throws RoomHasActiveReservationsException {
        Optional<Room> roomOptional = roomRepository.getById(id);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            List<Rent> rentsForRoom = rentRepository.findByRoomAndStatus(room.getId(), false);
            if (rentsForRoom.isEmpty()) {
                roomRepository.remove(room);
            } else {
                throw new RoomHasActiveReservationsException();
            }
        }
        ;
    }

}
