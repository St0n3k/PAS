package pl.lodz.pas.manager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.CreateRentDTO;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.model.user.Client;
import pl.lodz.pas.model.user.ClientTypes.ClientType;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.RoomRepository;
import pl.lodz.pas.repository.impl.UserRepository;

//TODO removing a rent should check if the rent is ended
//TODO beginDate should not be in the past while adding a rent

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

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRentById(@PathParam("id") Long id) {
        Rent rent = rentRepository.getById(id);

        if (rent == null) {
            throw new NotFoundException();
        }

        return Response.status(Response.Status.OK).entity(rent).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRents() {
        List<Rent> list = rentRepository.getAll();

        return Response.status(Response.Status.OK).entity(list).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeRent(@PathParam("id") Long rentId) {
        rentRepository.removeById(rentId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rentRoom(CreateRentDTO createRentDTO) {
        //Guard clause
        if (createRentDTO.getBeginTime().isAfter(createRentDTO.getEndTime())) {
            throw new BadRequestException();
        }

        Client client = userRepository.getById(createRentDTO.getClientId());
        Room room = roomRepository.getById(createRentDTO.getRoomId());

        if (client == null) {
            throw new BadRequestException("Client not found");
        }
        if (room == null) {
            throw new BadRequestException("Room not found");
        }

        double finalCost = calculateTotalCost(createRentDTO.getBeginTime(), createRentDTO.getEndTime(),
                                              room.getPrice(), createRentDTO.isBoard(), client.getClientType());
        Rent rent = new Rent(createRentDTO.getBeginTime(), createRentDTO.getEndTime(), createRentDTO.isBoard(),
                             finalCost, client, room);

        synchronized (rentRepository) {
            Rent created = rentRepository.add(rent);

            if (created == null) {
                throw new BadRequestException();
            }

            return Response.status(Response.Status.CREATED).entity(created).build();
        }
    }

    @PATCH
    @Path("/{id}/board")
    @Produces(MediaType.APPLICATION_JSON)
    public Rent updateRentBoard(@PathParam("id") Long id, Boolean board) {
        if (board == null) {
            throw new BadRequestException();
        }
        Rent rentToModify = rentRepository.getById(id);

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

    private double calculateTotalCost(LocalDateTime beginTime, LocalDateTime endTime, double costPerDay, boolean board,
                                      ClientType clientType) {
        Duration duration = Duration.between(beginTime, endTime);
        if (board) {
            costPerDay += 50; //Daily board is worth 50
        }
        return clientType.applyDiscount(Math.ceil(duration.toHours() / 24.0) * costPerDay);
    }

}
