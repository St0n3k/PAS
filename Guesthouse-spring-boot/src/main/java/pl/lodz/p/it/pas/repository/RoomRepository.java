package pl.lodz.p.it.pas.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.it.pas.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(int roomNumber);
}
