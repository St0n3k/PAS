package pl.lodz.p.it.pas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import pl.lodz.p.it.pas.common.MyValidator;

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

    @Column(name = "room_number", unique = true)
    @Min(value = 1)
    private int roomNumber;

    @Column
    @Min(value = 1)
    private double price;

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
