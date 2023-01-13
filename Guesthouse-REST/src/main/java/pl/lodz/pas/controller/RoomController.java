package pl.lodz.pas.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.dto.CreateRoomDTO;
import pl.lodz.p.it.pas.dto.RentRoomForSelfDTO;
import pl.lodz.p.it.pas.dto.UpdateRoomDTO;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.pas.exception.rent.CreateRentException;
import pl.lodz.pas.exception.room.CreateRoomException;
import pl.lodz.pas.exception.room.RoomHasActiveReservationsException;
import pl.lodz.pas.exception.room.RoomNotFoundException;
import pl.lodz.pas.exception.room.UpdateRoomException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;
import pl.lodz.pas.manager.RentManager;
import pl.lodz.pas.manager.RoomManager;

import java.security.Principal;
import java.util.List;

@RequestScoped
@Path("/rooms")
public class RoomController {

    @Context
    private SecurityContext securityContext;

    @Inject
    private RoomManager roomManager;

    @Inject
    private RentManager rentManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response addRoom(@Valid CreateRoomDTO dto) throws CreateRoomException {
        Room room = roomManager.addRoom(dto);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") Long id) throws RoomNotFoundException {
        Room room = roomManager.getRoomById(id);
        return Response.status(Response.Status.OK).entity(room).build();
    }

    @POST
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CLIENT"})
    public Response rentRoomForSelf(@PathParam("id") Long roomID, @Valid RentRoomForSelfDTO rentRoomForSelfDTO)
            throws UserNotFoundException, RoomNotFoundException, InactiveUserException, CreateRentException {
        Principal principal = securityContext.getUserPrincipal();
        if (principal instanceof Client client) {
            Long clientID = client.getId();
            CreateRentDTO createRentDTO = new CreateRentDTO(
                    rentRoomForSelfDTO.getBeginTime(),
                    rentRoomForSelfDTO.getEndTime(),
                    rentRoomForSelfDTO.isBoard(),
                    clientID,
                    roomID);
            Rent rent = rentManager.rentRoom(createRentDTO);
            return Response.status(Response.Status.CREATED).entity(rent).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


    /**
     * Endpoint which is used to get all saved rooms if param number is not set,
     * otherwise it will return room with given room number
     *
     * @return status code
     * 200(OK) and list of all rooms
     * 200(OK) if number parameter was set and room was found
     * 404(NOT_FOUND) if number parameter was set, but room was not found
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = roomManager.getAllRooms();
        return Response.status(Response.Status.OK).entity(rooms).build();
    }

    @GET
    @Path("/search/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomByNumber(@PathParam("number") Integer number) throws RoomNotFoundException {
        Room room = roomManager.getRoomByNumber(number);
        return Response.status(Response.Status.OK).entity(room).build();
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
                                      @QueryParam("past") Boolean past) throws RoomNotFoundException {
        List<Rent> rents = roomManager.getAllRentsOfRoom(roomId, past);
        return Response.status(Response.Status.OK).entity(rents).build();
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
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response updateRoom(@PathParam("id") Long id,
                               @Valid UpdateRoomDTO updateRoomDTO) throws RoomNotFoundException, UpdateRoomException {
        Room room = roomManager.updateRoom(id, updateRoomDTO);
        return Response.status(Response.Status.OK).entity(room).build();
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
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response removeRoom(@PathParam("id") Long id) throws RoomHasActiveReservationsException {
        roomManager.removeRoom(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
