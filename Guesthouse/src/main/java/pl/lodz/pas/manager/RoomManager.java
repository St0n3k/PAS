package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.UpdateRoomDTO;
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

    @PUT
    @Path("/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Room updateRoom(@PathParam("number") int number, UpdateRoomDTO updateRoomDTO) {
        Room existingRoom = roomRepository.getByRoomNumber(number);

        Double newPrice = updateRoomDTO.getPrice();
        Integer newNumber = updateRoomDTO.getRoomNumber();
        Integer newSize = updateRoomDTO.getSize();

        existingRoom.setPrice(newPrice == null? existingRoom.getPrice() : newPrice);
        existingRoom.setSize(newSize == null? existingRoom.getSize() : newSize);

        if (newNumber != null && roomRepository.getByRoomNumber(newNumber) == null) {
            existingRoom.setRoomNumber(newNumber);
        }

        return roomRepository.update(existingRoom);
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
