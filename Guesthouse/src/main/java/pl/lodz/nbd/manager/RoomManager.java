package pl.lodz.nbd.manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.nbd.model.Room;
import pl.lodz.nbd.repository.impl.RoomRepository;

@AllArgsConstructor
@NoArgsConstructor
public class RoomManager {

    private RoomRepository roomRepository;

    public Room addRoom(double price, int size, int number) {
        Room room = new Room(number, price, size);
        return roomRepository.add(room);
    }

    public Room updateRoom(Room room) {
        return roomRepository.update(room);
    }

    public Room getByRoomNumber(int number) {
        return roomRepository.getByRoomNumber(number);
    }

    public boolean removeRoom(Room room) {
        return roomRepository.remove(room);
    }
}
