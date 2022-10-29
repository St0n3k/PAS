package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.UpdateRoomDTO;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.RoomRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
@Path("/rooms")
public class RoomManager {

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private RentRepository rentRepository;


    /**
     * Endpoint which is used to save room to database, room number has to be unique, otherwise method will throw exception
     *
     * @param room room to be saved
     * @return status code
     * 201(CREATED) if room was successfully saved,
     * 409(CONFLICT) if room was not saved due to constraints(room id / room number)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoom(@Valid Room room) {
        try {
            Room result = roomRepository.add(room);
            return Response.status(Response.Status.CREATED).entity(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") Long id) {
        Optional<Room> optionalRoom = roomRepository.getById(id);

        if (optionalRoom.isEmpty()) {
            throw new NotFoundException();
        }
        return Response.status(Response.Status.OK).entity(optionalRoom.get()).build();
    }


    /**
     * Endpoint which is used to get all saved rooms if param number is not set,
     * otherwise it will return room with given room number
     *
     * @param number room number to be found in database
     * @return status code
     * 200(OK) and list of all rooms
     * 200(OK) if number parameter was set and room was found
     * 404(NOT_FOUND) if number parameter was set, but room was not found
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRoomsOrByNumber(@QueryParam("number") Integer number) {
        if (number == null) {
            List<Room> rooms = roomRepository.getAll();
            return Response.status(Response.Status.OK).entity(rooms).build();
        } else {
            Optional<Room> optionalRoom = roomRepository.getByRoomNumber(number);
            if (optionalRoom.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.OK).entity(optionalRoom.get()).build();
        }
    }


    /**
     * Endpoint which returns list of rents of given room
     *
     * @param roomId room id
     * @param past   flag which indicates if the result will be list of past rents or future rents.
     *               If this parameter is not set, the result of the method will be list of all rents of given room
     * @return list of rents that meet given criteria
     */
    @GET
    @Path("/{roomId}/rents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRentsOfRoom(@PathParam("roomId") Long roomId,
                                      @QueryParam("past") Boolean past) {
        try {
            if (!roomRepository.existsById(roomId)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

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


    /**
     * Endpoint which is used to update room properties
     *
     * @param id            id of room to be updated
     * @param updateRoomDTO object containing new properties of existing room
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRoom(@PathParam("id") Long id,
                               @Valid UpdateRoomDTO updateRoomDTO) {
        Optional<Room> optionalRoom = roomRepository.getById(id);

        if (optionalRoom.isEmpty()) {
            throw new NotFoundException();
        }
        Room existingRoom = optionalRoom.get();
        Double newPrice = updateRoomDTO.getPrice();
        Integer newNumber = updateRoomDTO.getRoomNumber();
        Integer newSize = updateRoomDTO.getSize();

        existingRoom.setPrice(newPrice == null ? existingRoom.getPrice() : newPrice);
        existingRoom.setSize(newSize == null ? existingRoom.getSize() : newSize);
        existingRoom.setRoomNumber(newNumber == null ? existingRoom.getRoomNumber() : newNumber);

        Optional<Room> updatedRoom;
        try {
            updatedRoom = roomRepository.update(existingRoom);
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        if (updatedRoom.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(updatedRoom.get()).build();
    }


    /**
     * Endpoint for removing room from database. Room can be removed only if there are no current or future rents
     *
     * @param id id of the room to be removed
     * @return status code
     * 204(NO_CONTENT) if room was removed or was not found
     * 409(CONFLICT) if there are current or future rents for room with given id
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeRoom(@PathParam("id") Long id) {
        Optional<Room> roomOptional = roomRepository.getById(id);

        if (roomOptional.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        Room room = roomOptional.get();

        List<Rent> rentsForRoom = rentRepository.findByRoomAndStatus(room.getId(), false);
        if (rentsForRoom.isEmpty()) {
            roomRepository.remove(room);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
}
