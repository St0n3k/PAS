package pl.lodz.p.it.pas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.lodz.p.it.pas.common.MyValidator;
import pl.lodz.p.it.pas.model.user.Client;


@Entity
@Data
@NamedQueries({
        @NamedQuery(name = "Rent.getAll",
                query = "SELECT r FROM Rent r"),
        @NamedQuery(name = "Rent.getByRoomId",
                query = "SELECT r FROM Rent r WHERE r.room.id = :roomId"),
        @NamedQuery(name = "Rent.getByClientPersonalId",
                query = "SELECT r FROM Rent r WHERE r.client.personalId = :personalId"),
        @NamedQuery(name = "Rent.getRentsColliding",
                query = """
                    SELECT r FROM Rent r
                    WHERE r.room.roomNumber = :roomNumber
                          AND ((:beginDate BETWEEN r.beginTime AND r.endTime)
                          OR (:endDate BETWEEN r.beginTime AND r.endTime)
                          OR (r.beginTime between :beginDate and :endDate)
                          OR (r.endTime BETWEEN :beginDate AND :endDate))"""),
        @NamedQuery(name = "Rent.removeById",
                query = "DELETE FROM Rent r WHERE r.id = :id"),
        @NamedQuery(name = "Rent.getByClientUsername",
                query = "SELECT r FROM Rent r WHERE r.client.username = :username"),
        @NamedQuery(name = "Rent.getByClientId",
                query = "SELECT r FROM Rent r WHERE r.client.id = :id"),
        @NamedQuery(name = "Rent.getPastRentsByRoom",
                query = "SELECT r from Rent r WHERE (r.endTime < CURRENT_TIMESTAMP) AND r.room.id = :id"),
        @NamedQuery(name = "Rent.getActiveRentsByRoom",
                query = "SELECT r from Rent r WHERE (r.endTime > CURRENT_TIMESTAMP) AND r.room.id = :id"),
        @NamedQuery(name = "Rent.getPastRentsByClient",
                query = "SELECT r from Rent r WHERE (r.endTime < CURRENT_TIMESTAMP) AND r.client.id = :id"),
        @NamedQuery(name = "Rent.getActiveRentsByClient",
                query = "SELECT r from Rent r WHERE (r.endTime > CURRENT_TIMESTAMP) AND r.client.id = :id")})
@NoArgsConstructor
public class Rent extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "rentId", strategy = GenerationType.IDENTITY)
    @Column(name = "rent_id")
    private Long id;

    @NotNull
    @Column(name = "begin_time")
    @Future
    private LocalDateTime beginTime;

    @NotNull
    @Column(name = "end_time")
    @Future
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
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.board = board;
        this.finalCost = finalCost;
        this.client = client;
        this.room = room;
        MyValidator.validate(this);
    }

    @AssertTrue
    private boolean isEndDateAfterBeginDate() {
        return endTime.isAfter(beginTime);
    }
}
