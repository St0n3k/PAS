package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.dto.UpdateRentBoardDTO;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.pas.exception.InvalidInputException;
import pl.lodz.pas.exception.rent.CreateRentException;
import pl.lodz.pas.exception.rent.RemoveRentException;
import pl.lodz.pas.exception.rent.RentNotFoundException;
import pl.lodz.pas.exception.room.RoomNotFoundException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.RoomRepository;
import pl.lodz.pas.repository.impl.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
public class RentManager {

    @Inject
    private UserRepository userRepository;
    @Inject
    private RoomRepository roomRepository;
    @Inject
    private RentRepository rentRepository;

    @Context
    private SecurityContext securityContext;


    /**
     * Method for creating a new rent. Rent will be created if client and room exists in database, and if rent period
     * is not colliding with existing rents.
     *
     * @param createRentDTO object containing information about rent which creation will be attempted.
     * @return
     * @throws UserNotFoundException if user was not found
     * @throws RoomNotFoundException if room was not found
     * @throws InactiveUserException if user is inactive
     */
    public Rent rentRoom(CreateRentDTO createRentDTO) throws
                                                      UserNotFoundException,
                                                      RoomNotFoundException,
                                                      InactiveUserException,
                                                      CreateRentException {
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

        Rent created = rentRepository.add(rent); //synchronized method

        if (created == null) {
            throw new CreateRentException();
        }

        return rent;
    }


    /**
     * Method used to find rent by id
     *
     * @param id id of rent
     * @return rent
     * @throws RentNotFoundException if rent with given id was not found
     */
    public Rent getRentById(Long id) throws RentNotFoundException {
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            throw new RentNotFoundException();
        }
        return optionalRent.get();
    }


    /**
     * @return list of all rents in the database
     */
    public List<Rent> getAllRents() {
        return rentRepository.getAll();
    }


    /**
     * Method used to change board option for given rent, cost is recalculated before saving to database
     *
     * @param id id of the rent to be updated
     * @param dto object containing the choice of board option (true/false)
     * @return status 200 (OK) if rent was updated, 409 (CONFLICT) otherwise
     * @throws InvalidInputException if user input is invalid
     * @throws RentNotFoundException if rent with given id does not exist
     */
    public Rent updateRentBoard(Long id, UpdateRentBoardDTO dto) throws InvalidInputException, RentNotFoundException {
        if (dto == null) {
            throw new InvalidInputException();
        }
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            throw new RentNotFoundException();
        }
        if (dto.getBoard() == null) {
            throw new InvalidInputException();
        }
        Rent rentToModify = optionalRent.get();

        rentToModify.setBoard(dto.getBoard());
        double newCost = calculateTotalCost(rentToModify.getBeginTime(),
                                            rentToModify.getEndTime(),
                                            rentToModify.getRoom().getPrice(),
                                            rentToModify.isBoard(),
                                            rentToModify.getClient().getClientType());
        rentToModify.setFinalCost(newCost);

        Optional<Rent> updatedRent = rentRepository.update(rentToModify);
        if (updatedRent.isEmpty()) {
            throw new InvalidInputException();
        }
        return updatedRent.get();
    }

    /**
     * Method for removing future rents, archived rent will not be removed
     *
     * @param rentId id of the rent to be removed
     * @return void
     */
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

    /**
     * Private method used to calculate total cost of rent on creation or on board option update
     *
     * @param beginTime begin date of the rent
     * @param endTime end date of the rent
     * @param costPerDay room price per day
     * @param board determines if board option is chosen
     * @param clientType client type defines percentage discount for total cost
     * @return total cost
     */
    private double calculateTotalCost(LocalDateTime beginTime, LocalDateTime endTime, double costPerDay, boolean board,
                                      ClientType clientType) {
        Duration duration = Duration.between(beginTime, endTime);
        if (board) {
            costPerDay += 50; //Daily board is worth 50
        }
        return clientType.applyDiscount(Math.ceil(duration.toHours() / 24.0) * costPerDay);
    }
}
