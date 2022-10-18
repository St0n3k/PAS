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

        Room room = roomManager.addRoom(999, 911, 911);
        return "Hello " + room.getPrice();
    }


}
