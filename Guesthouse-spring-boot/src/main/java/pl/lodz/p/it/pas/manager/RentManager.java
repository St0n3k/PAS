package pl.lodz.p.it.pas.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.dto.UpdateRentBoardDTO;
import pl.lodz.p.it.pas.exception.*;
import pl.lodz.p.it.pas.exception.rent.CreateRentException;
import pl.lodz.p.it.pas.exception.rent.RemoveRentException;
import pl.lodz.p.it.pas.exception.rent.RentNotFoundException;
import pl.lodz.p.it.pas.exception.rent.UpdateRentException;
import pl.lodz.p.it.pas.exception.room.RoomNotFoundException;
import pl.lodz.p.it.pas.exception.user.InactiveUserException;
import pl.lodz.p.it.pas.exception.user.UserNotFoundException;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.p.it.pas.repository.impl.RentRepository;
import pl.lodz.p.it.pas.repository.impl.RoomRepository;
import pl.lodz.p.it.pas.repository.impl.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class RentManager {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoomRepository roomRepository;

    @Autowired
    private final RentRepository rentRepository;


    public Rent rentRoom(CreateRentDTO createRentDTO) throws
            UserNotFoundException,
            RoomNotFoundException,
            InactiveUserException,
            CreateRentException,
            InvalidInputException {

        if (createRentDTO == null) {
            throw new InvalidInputException();
        }

        Optional<User> optionalUser = userRepository.getById(createRentDTO.getClientId());
        Optional<Room> optionalRoom = roomRepository.getById(createRentDTO.getRoomId());

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        if (optionalRoom.isEmpty()) {
            throw new RoomNotFoundException();
        }

        Client client = (Client) optionalUser.get();
        Room room = optionalRoom.get();

        if (!client.isActive()) {
            throw new InactiveUserException();
        }
        double finalCost = calculateTotalCost(createRentDTO.getBeginTime(), createRentDTO.getEndTime(),
                                              room.getPrice(), createRentDTO.isBoard(), client.getClientType());

        Rent rent = new Rent(createRentDTO.getBeginTime(), createRentDTO.getEndTime(), createRentDTO.isBoard(),
                             finalCost, client, room);

        Optional<Rent> optionalRent;

        optionalRent = Optional.ofNullable(rentRepository.add(rent));

        if (optionalRent.isEmpty()) {
            throw new CreateRentException();
        }
        return optionalRent.get();

    }


    public Rent getRentById(Long id) throws RentNotFoundException {
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            throw new RentNotFoundException();
        }

        return optionalRent.get();
    }



    public List<Rent> getAllRents() {
        return rentRepository.getAll();
    }



    public Rent updateRentBoard(Long id, UpdateRentBoardDTO dto)
            throws RentNotFoundException, InvalidInputException, UpdateRentException {
        if (dto == null) {
            throw new InvalidInputException();
        }
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            throw new RentNotFoundException();
        }
        Rent rentToModify = optionalRent.get();

        rentToModify.setBoard(dto.getBoard());
        double newCost = calculateTotalCost(rentToModify.getBeginTime(),
                                            rentToModify.getEndTime(),
                                            rentToModify.getRoom().getPrice(),
                                            rentToModify.isBoard(),
                                            rentToModify.getClient().getClientType());
        rentToModify.setFinalCost(newCost);

        Optional<Rent> optionalUpdatedRent = rentRepository.update(rentToModify);
        if (optionalUpdatedRent.isEmpty()) {
            throw new UpdateRentException();
        }
        return optionalUpdatedRent.get();
    }



    public void removeRent(Long rentId) throws RemoveRentException {
        Optional<Rent> optionalRent = rentRepository.getById(rentId);
        if (optionalRent.isEmpty()) {
            return;
        }
        Rent rent = optionalRent.get();
        LocalDateTime now = LocalDateTime.now();

        if (rent.getBeginTime().isAfter(now)) {
            rentRepository.removeById(rentId);
            return;
        }
       throw new RemoveRentException();
    }

    private double calculateTotalCost(LocalDateTime beginTime, LocalDateTime endTime, double costPerDay, boolean board,
                                      ClientType clientType) {
        Duration duration = Duration.between(beginTime, endTime);
        if (board) {
            costPerDay += 50; //Daily board is worth 50
        }
        return clientType.applyDiscount(Math.ceil(duration.toHours() / 24.0) * costPerDay);
    }
}
