package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.CreateRentDTO;
import pl.lodz.pas.model.user.Client;
import pl.lodz.pas.model.user.ClientTypes.ClientType;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.impl.UserRepository;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.RoomRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
@Path("/rents")
public class RentManager {

    @Inject
    private UserRepository userRepository;
    @Inject
    private RoomRepository roomRepository;
    @Inject
    private RentRepository rentRepository;

    public List<Rent> getAllRentsOfRoom(int roomNumber) {
        // TODO Move to RentManager
        return rentRepository.getByRoomNumber(roomNumber);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Rent getRentById(@PathParam("id") Long id) {
        Rent rent = rentRepository.getById(id);

        if (rent == null) {
            throw new NotFoundException();
        }
        return rent;
    }

    public List<Rent> getAllRentsOfClient(String personalId) {
        // TODO Move to ClientManager
        return rentRepository.getByClientPersonalId(personalId);
    }

    @DELETE
    @Path("/{id}")
    public void removeRent(@PathParam("id") Long rentId) {
        if (!rentRepository.removeById(rentId)) {
            throw new NotFoundException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Rent rentRoom(CreateRentDTO createRentDTO) {
        // TODO change status code to 201 Created

        //Guard clause
        if (createRentDTO.getBeginTime().isAfter(createRentDTO.getEndTime())) {
            throw new BadRequestException();
        }

        Client client = userRepository.getById(createRentDTO.getClientId());
        Room room = roomRepository.getById(createRentDTO.getRoomId());

        if (client == null) {
            throw new NotFoundException("Client not found");
        }
        if (room == null) {
            throw new NotFoundException("Room not found");
        }

        double finalCost = calculateTotalCost(createRentDTO.getBeginTime(), createRentDTO.getEndTime(),
                room.getPrice(), createRentDTO.isBoard(), client.getClientType());
        Rent rent = new Rent(createRentDTO.getBeginTime(), createRentDTO.getEndTime(), createRentDTO.isBoard(),
                finalCost, client, room);

        return rentRepository.add(rent);
    }

    @PUT
    @Path("/{id}/board")
    @Produces(MediaType.APPLICATION_JSON)
    public Rent updateRentBoard(@PathParam("id") Long id, Boolean board) {
        if (board == null) {
            throw new BadRequestException();
        }
        Rent rentToModify = rentRepository.getById(id);


        System.out.println(rentToModify);

        if (rentToModify == null) {
            throw new NotFoundException();
        }
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
