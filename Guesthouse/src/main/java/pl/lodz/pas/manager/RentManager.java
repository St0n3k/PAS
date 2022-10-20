package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.model.Client;
import pl.lodz.pas.model.ClientTypes.ClientType;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.impl.ClientRepository;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.RoomRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
public class RentManager {

    @Inject
    private ClientRepository clientRepository;
    @Inject
    private RoomRepository roomRepository;
    @Inject
    private RentRepository rentRepository;

    public List<Rent> getAllRentsOfRoom(int roomNumber) {
        return rentRepository.getByRoomNumber(roomNumber);
    }

    public Rent getRentById(Long id) {
        return rentRepository.getById(id);
    }

    public List<Rent> getAllRentsOfClient(String personalId) {
        return rentRepository.getByClientPersonalId(personalId);
    }

    public boolean removeRent(Rent rent) {
        return rentRepository.remove(rent);
    }

    public Rent rentRoom(LocalDateTime beginTime, LocalDateTime endTime, boolean board, String clientPersonalId, int roomNumber) {

        //Guard clause
        if (beginTime.isAfter(endTime)) return null;

        Client client = clientRepository.getClientByPersonalId(clientPersonalId);
        Room room = roomRepository.getByRoomNumber(roomNumber);

        if (client == null || room == null) return null;

        double finalCost = calculateTotalCost(beginTime, endTime, room.getPrice(), board, client.getClientType());
        Rent rent = new Rent(beginTime, endTime, board, finalCost, client, room);

        return rentRepository.add(rent);
    }

    public Rent updateRentBoard(Long rentId, boolean board) {
        Rent rentToModify = rentRepository.getById(rentId);
        rentToModify.setBoard(board);
        double newCost = calculateTotalCost(
                rentToModify.getBeginTime(),
                rentToModify.getEndTime(),
                rentToModify.getRoom().getPrice(),
                rentToModify.isBoard(),
                rentToModify.getClient().getClientType()
        );
        rentToModify.setFinalCost(newCost);

        return rentRepository.update(rentToModify);
    }

    private double calculateTotalCost(LocalDateTime beginTime, LocalDateTime endTime, double costPerDay, boolean board, ClientType clientType) {
        Duration duration = Duration.between(beginTime, endTime);
        if (board) costPerDay += 50; //Daily board is worth 50
        return clientType.applyDiscount(Math.ceil(duration.toHours() / 24.0) * costPerDay);
    }

}
