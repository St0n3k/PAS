package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.CreateRentDTO;
import pl.lodz.pas.dto.UpdateRentBoardDTO;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.model.user.Client;
import pl.lodz.pas.model.user.ClientTypes.ClientType;
import pl.lodz.pas.model.user.User;
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
@Path("/rents")
public class RentManager {

    @Inject
    private UserRepository userRepository;
    @Inject
    private RoomRepository roomRepository;
    @Inject
    private RentRepository rentRepository;


    /**
     * Endpoint for creating a new rent. Rent will be created if client and room exists in database, and if rent period
     * is not colliding with existing rents.
     *
     * @param createRentDTO object containing information about rent which creation will be attempted.
     * @return status code 201 (CREATED) if rent was successfully created,
     * 409 (CONFLICT) if rent could not be created due to rent time period,
     * 400 (BAD_REQUEST) if client or room do not exist in database
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rentRoom(@Valid CreateRentDTO createRentDTO) {
        Optional<User> optionalUser = userRepository.getById(createRentDTO.getClientId());
        Optional<Room> optionalRoom = roomRepository.getById(createRentDTO.getRoomId());

        if (optionalUser.isEmpty()) {
            throw new BadRequestException("Client not found");
        }
        if (optionalRoom.isEmpty()) {
            throw new BadRequestException("Room not found");
        }

        Client client = (Client) optionalUser.get();
        Room room = optionalRoom.get();

        if (!client.isActive()) {
            throw new NotAuthorizedException("Client not active");
        }

        double finalCost = calculateTotalCost(createRentDTO.getBeginTime(), createRentDTO.getEndTime(),
                room.getPrice(), createRentDTO.isBoard(), client.getClientType());
        Rent rent = new Rent(createRentDTO.getBeginTime(), createRentDTO.getEndTime(), createRentDTO.isBoard(),
                finalCost, client, room);

        Rent created = rentRepository.add(rent); //synchronized method

        if (created == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRentById(@PathParam("id") Long id) {
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            throw new NotFoundException();
        }
        return Response.status(Response.Status.OK).entity(optionalRent.get()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRents() {
        List<Rent> list = rentRepository.getAll();
        return Response.status(Response.Status.OK).entity(list).build();
    }


    /**
     * Endpoint used to change board option for given rent, cost is recalculated before saving to database
     *
     * @param id  id of the rent to be updated
     * @param dto object containing the choice of board option (true/false)
     * @return status 200 (OK) if rent was updated, 409 (CONFLICT) otherwise
     */
    @PATCH
    @Path("/{id}/board")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRentBoard(@PathParam("id") Long id, UpdateRentBoardDTO dto) {
        if (dto == null) {
            throw new BadRequestException();
        }
        Optional<Rent> optionalRent = rentRepository.getById(id);

        if (optionalRent.isEmpty()) {
            throw new NotFoundException();
        }
        if (dto.getBoard() == null) {
            throw new BadRequestException();
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
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.OK).entity(updatedRent).build();
    }

    /**
     * Endpoint for removing future rents, archived rent will not be removed
     *
     * @param rentId id of the rent to be removed
     * @return status code 204 (NO_CONTENT) if rent was removed, otherwise 409 (CONFLICT)
     */
    @DELETE
    @Path("/{id}")
    public Response removeRent(@PathParam("id") Long rentId) {
        Optional<Rent> optionalRent = rentRepository.getById(rentId);
        if (optionalRent.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        Rent rent = optionalRent.get();

        LocalDateTime now = LocalDateTime.now();
        if (rent.getBeginTime().isAfter(now)) {
            rentRepository.removeById(rentId);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }

    /**
     * Method used to calculate total cost of rent on creation or on board option update
     *
     * @param beginTime  begin date of the rent
     * @param endTime    end date of the rent
     * @param costPerDay room price per day
     * @param board      determines if board option is chosen
     * @param clientType client type defines percentage discount for total cost
     * @return
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
