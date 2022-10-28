package pl.lodz.pas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.pas.common.MyValidator;

@Entity
@NamedQueries({
    @NamedQuery(name = "Room.getAll",
                query = "SELECT r FROM Room r"),
    @NamedQuery(name = "Room.getByRoomNumber",
                query = "SELECT r FROM Room r WHERE r.roomNumber = :roomNumber"),
    @NamedQuery(name = "Room.existsById",
                query = "select (count(r) > 0) from Room r where r.id = :id")
})
@Data
@NoArgsConstructor
public class Room extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "roomId", strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @NotNull
    @Column(name = "room_number", unique = true)
    @Min(value = 1)
    private int roomNumber;

    @NotNull
    @Column
    @Min(value = 1)
    private double price;

    @NotNull
    @Column
    @Min(value = 1)
    private int size;

    public Room(int roomNumber, double price, int size) {
        this.roomNumber = roomNumber;
        this.price = price;
        this.size = size;
        MyValidator.validate(this);
    }
}
