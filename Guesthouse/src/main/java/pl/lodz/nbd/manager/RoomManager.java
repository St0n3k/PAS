package pl.lodz.nbd.manager;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.nbd.model.Room;
import pl.lodz.nbd.repository.impl.RoomRepository;

@AllArgsConstructor
@NoArgsConstructor
@ApplicationScoped
public class RoomManager {

    @Inject
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
