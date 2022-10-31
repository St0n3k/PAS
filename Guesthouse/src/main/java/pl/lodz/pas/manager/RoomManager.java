package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.CreateRoomDTO;
import pl.lodz.pas.dto.UpdateRoomDTO;
import pl.lodz.pas.exception.room.CreateRoomException;
import pl.lodz.pas.exception.room.RoomHasActiveReservationsException;
import pl.lodz.pas.exception.room.RoomNotFoundException;
import pl.lodz.pas.exception.room.UpdateRoomException;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.RoomRepository;

import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
public class RoomManager {

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private RentRepository rentRepository;


    /**
     * Endpoint which is used to save room to database, room number has to be unique, otherwise method will throw exception
     *
     * @param dto room to be saved
     * @return status code
     * 201(CREATED) if room was successfully saved,
     * 409(CONFLICT) if room was not saved due to constraints(room id / room number)
     */
    public Room addRoom(CreateRoomDTO dto) throws CreateRoomException {
        try {
            Room room = new Room(dto.getRoomNumber(), dto.getPrice(), dto.getSize());
            return roomRepository.add(room);
        } catch (Exception e) {
            throw new CreateRoomException();
        }
    }


    public Room getRoomById(Long id) throws RoomNotFoundException {
        Optional<Room> optionalRoom = roomRepository.getById(id);

        if (optionalRoom.isEmpty()) {
            throw new RoomNotFoundException();
        }
        return optionalRoom.get();
    }


    /**
     * Endpoint which is used to get all saved rooms if param number is not set,
     * otherwise it will return room with given room number
     *
     * @return status code
     * 200(OK) and list of all rooms
     * 200(OK) if number parameter was set and room was found
     * 404(NOT_FOUND) if number parameter was set, but room was not found
     */
    public List<Room> getAllRooms() {
        return roomRepository.getAll();
    }


    public Room getRoomByNumber(Integer number) throws RoomNotFoundException {
        Optional<Room> optionalRoom = roomRepository.getByRoomNumber(number);
        if (optionalRoom.isEmpty()) {
            throw new RoomNotFoundException();
        }
        return optionalRoom.get();
    }


    /**
     * Endpoint which returns list of rents of given room
     *
     * @param roomId room id
     * @param past   flag which indicates if the result will be list of past rents or future rents.
     *               If this parameter is not set, the result of the method will be list of all rents of given room
     * @return list of rents that meet given criteria
     */
    public List<Rent> getAllRentsOfRoom(Long roomId, Boolean past) throws RoomNotFoundException {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException();
        }

        List<Rent> rents;
        if (past != null) { // find past or active rents
            rents = rentRepository.findByRoomAndStatus(roomId, past);
        } else { // find all rents
            rents = rentRepository.getByRoomId(roomId);
        }
        return rents;
    }


    /**
     * Endpoint which is used to update room properties
     *
     * @param id            id of room to be updated
     * @param updateRoomDTO object containing new properties of existing room
     */
    public Room updateRoom(Long id, UpdateRoomDTO updateRoomDTO) throws RoomNotFoundException, UpdateRoomException {
        Optional<Room> optionalRoom = roomRepository.getById(id);

        if (optionalRoom.isEmpty()) {
            throw new RoomNotFoundException();
        }
        Room existingRoom = optionalRoom.get();
        Double newPrice = updateRoomDTO.getPrice();
        Integer newNumber = updateRoomDTO.getRoomNumber();
        Integer newSize = updateRoomDTO.getSize();

        existingRoom.setPrice(newPrice == null ? existingRoom.getPrice() : newPrice);
        existingRoom.setSize(newSize == null ? existingRoom.getSize() : newSize);
        existingRoom.setRoomNumber(newNumber == null ? existingRoom.getRoomNumber() : newNumber);

        Optional<Room> updatedRoom;
        try {
            updatedRoom = roomRepository.update(existingRoom);
        } catch (Exception e) {
            throw new UpdateRoomException();
        }

        if (updatedRoom.isEmpty()) {
            throw new UpdateRoomException();
        }
        return updatedRoom.get();
    }


    /**
     * Endpoint for removing room from database. Room can be removed only if there are no current or future rents
     *
     * @param id id of the room to be removed
     * @return status code
     * 204(NO_CONTENT) if room was removed or was not found
     * 409(CONFLICT) if there are current or future rents for room with given id
     */
    public void removeRoom(Long id) throws RoomHasActiveReservationsException {
        Optional<Room> roomOptional = roomRepository.getById(id);

        if (roomOptional.isEmpty()) {
            return;
        }
        Room room = roomOptional.get();

        List<Rent> rentsForRoom = rentRepository.findByRoomAndStatus(room.getId(), false);
        if (rentsForRoom.isEmpty()) {
            roomRepository.remove(room);
        } else {
            throw new RoomHasActiveReservationsException();
        }
    }
}
