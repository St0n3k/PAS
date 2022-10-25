package pl.lodz.pas.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.lodz.pas.common.MyValidator;
import pl.lodz.pas.model.user.Client;

@Entity
@NamedQueries({
    @NamedQuery(name = "Rent.getAll",
                query = "SELECT r FROM Rent r"),
    @NamedQuery(name = "Rent.getByRoomId",
                query = "SELECT r FROM Rent r WHERE r.room.id = :roomId"),
    @NamedQuery(name = "Rent.getByClientPersonalId",
                query = "SELECT r FROM Rent r WHERE r.client.personalId = :personalId"),
    @NamedQuery(name = "Rent.getRentsColliding",
                query = "SELECT r FROM Rent r WHERE (r.room.roomNumber = :roomNumber AND ((:beginDate between r.beginTime and r.endTime) OR (:endDate between r.beginTime and r.endTime) OR (r.beginTime between :beginDate and :endDate) OR (r.endTime BETWEEN :beginDate and :endDate)))"),
    @NamedQuery(name = "Rent.removeById",
                query = "DELETE FROM Rent r WHERE r.id = :id"),
    @NamedQuery(name = "Rent.getByClientUsername",
                query = "SELECT r FROM Rent r WHERE r.client.username = :username"),
})
@Data
@NoArgsConstructor
public class Rent extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "rentId", strategy = GenerationType.IDENTITY)
    @Column(name = "rent_id")
    private Long id;

    @NotNull
    @Column(name = "begin_time")
    private LocalDateTime beginTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Column
    private boolean board;

    @NotNull
    @Column(name = "final_cost")
    private double finalCost;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client_id")
    private Client client;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "room_id")
    private Room room;

    public Rent(LocalDateTime beginTime, LocalDateTime endTime, boolean board, double finalCost, Client client,
                Room room) {
        if (beginTime.isAfter(endTime)) {
            throw new RuntimeException("Wrong chronological order");
        }
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.board = board;
        this.finalCost = finalCost;
        this.client = client;
        this.room = room;
        MyValidator.validate(this);
    }
}
