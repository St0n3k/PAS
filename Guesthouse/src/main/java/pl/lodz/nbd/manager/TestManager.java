package pl.lodz.nbd.manager;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import pl.lodz.nbd.model.Room;

@Path("/")
public class TestManager {

    @Inject
    private RoomManager roomManager;

    @GET
    public String repoInjectionTest() {
        return "Room manager was injected: " + roomManager.toString();
    }

    @GET
    @Path("/{name}")
    public String test(@PathParam("name") String name) {
        try {
            Room room = roomManager.addRoom(999, 911, 911);
            return "Room was added " + room.getId();
        } catch (Exception e) {
            return "Something bad happened during adding room";
        }
    }

    @GET
    @Path("room/{number}")
    public Room getByNumber(@PathParam("number") int number) {
        return roomManager.getByRoomNumber(number);
    }

    @GET
    @Path("room/{number}/update/{size}")
    public Room updateRoom(@PathParam("number") int number, @PathParam("size") int newSize) {
        Room room = roomManager.getByRoomNumber(number);
        room.setSize(newSize);
        roomManager.updateRoom(room);

        return roomManager.getByRoomNumber(number);
    }


}
