package pl.lodz.p.it.pas.manager;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class RoomManager {

    private final RoomRepository roomRepository;
    private final RentRepository rentRepository;

    public Room getRoomById(Long id) throws RoomNotFoundException {
        return roomRepository.getById(id)
                             .orElseThrow(RoomNotFoundException::new);
    }

    public List<Room> getAllRooms() {
        return roomRepository.getAll();
    }

    public Room getByRoomNumber(Integer number) throws RoomNotFoundException {
        return roomRepository.getByRoomNumber(number)
                             .orElseThrow(RoomNotFoundException::new);
    }

    public Room addRoom(CreateRoomDto dto) throws CreateRoomException {
        try {
            Room room = new Room(dto.getRoomNumber(), dto.getPrice(), dto.getSize());
            return roomRepository.add(room);
        } catch (Exception e) {
            throw new CreateRoomException();
        }
    }

    public List<Rent> getAllRentsOfRoom(Long id, Boolean past)
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

    public Room updateRoom(Long id, UpdateRoomDto dto)
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
            return roomRepository.update(room).orElseThrow();
        } catch (Exception e) {
            throw new UpdateRoomException();
        }
    }

    public void deleteRoomById(Long id) throws RoomHasActiveReservationsException {
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
    }

}
