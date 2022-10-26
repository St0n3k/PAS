package pl.lodz.pas.manager;

import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.UpdateRoomDTO;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.RoomRepository;

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
@Path("/rooms")
public class RoomManager {

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private RentRepository rentRepository;

    @GET
    @Path("/id/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") Long roomId) {
        Room room = roomRepository.getById(roomId);

        if (room == null) {
            throw new NotFoundException();
        }

        return Response.status(Response.Status.OK).entity(room).build();
    }

    @GET
    @Path("/{roomId}/rents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRentsOfRoom(@PathParam("roomId") Long roomId,
                                      @QueryParam("past") Boolean past) {
        try {
            List<Rent> rents;
            if (past != null) { // find past or active rents
                rents = rentRepository.findByRoomAndStatus(roomId, past);
            } else { // find all rents
                rents = rentRepository.getByRoomId(roomId);
            }
            return Response.status(Response.Status.OK).entity(rents).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoom(@Valid Room room) {
        Room result = roomRepository.add(room);

        if (result == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRoom(@PathParam("number") int number, UpdateRoomDTO updateRoomDTO) {
        Room existingRoom = roomRepository.getByRoomNumber(number);

        Double newPrice = updateRoomDTO.getPrice();
        Integer newNumber = updateRoomDTO.getRoomNumber();
        Integer newSize = updateRoomDTO.getSize();

        existingRoom.setPrice(newPrice == null ? existingRoom.getPrice() : newPrice);
        existingRoom.setSize(newSize == null ? existingRoom.getSize() : newSize);

        if (newNumber != null && roomRepository.getByRoomNumber(newNumber) == null) {
            existingRoom.setRoomNumber(newNumber);
        }

        Room updated = roomRepository.update(existingRoom);
        return Response.status(200).entity(updated).build();
    }

    @GET
    @Path("/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByRoomNumber(@PathParam("number") int number) {
        try {
            Room room = roomRepository.getByRoomNumber(number);
            return Response.status(Response.Status.OK).entity(room).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeRoom(@PathParam("id") Long id) {
        try {
            Room room = roomRepository.getById(id);
            List<Rent> rentsForRoom = rentRepository.findByRoomAndStatus(room.getId(), false);
            if(rentsForRoom.isEmpty()){
                roomRepository.remove(room);
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = roomRepository.getAll();
        return Response.status(Response.Status.OK).entity(rooms).build();
    }
}
