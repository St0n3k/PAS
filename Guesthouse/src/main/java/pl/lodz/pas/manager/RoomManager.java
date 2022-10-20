package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.impl.RoomRepository;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
@Path("/rooms")
public class RoomManager {

    @Inject
    private RoomRepository roomRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Room addRoom(Room room) {
        return roomRepository.add(room);
    }

    //TODO changing only one field of room (and change method to PUT)
    @POST
    @Path("/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Room updateRoom(@PathParam("number") int number, Room room) {
        Room existingRoom = roomRepository.getByRoomNumber(number);
        room.setId(existingRoom.getId());

        return roomRepository.update(room);
    }

    @GET
    @Path("/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Room getByRoomNumber(@PathParam("number") int number) {
        return roomRepository.getByRoomNumber(number);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean removeRoom(@QueryParam("number") int number) {
        Room room = roomRepository.getByRoomNumber(number);
        return roomRepository.remove(room);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        return roomRepository.getAll();
    }
}
