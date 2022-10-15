package pl.lodz.nbd.manager;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import pl.lodz.nbd.model.Room;
import pl.lodz.nbd.repository.impl.RoomRepository;

@Path("/")
public class TestManager {

    @Inject
    private RoomRepository roomRepository;

    @GET
    public String repoInjectionTest() {
        return roomRepository.toString();
    }

    @GET
    @Path("/{name}")
    public String test(@PathParam("name") String name) {

        Room room = new Room(10000, 20, 30);
        Room room2 = roomRepository.getByRoomNumber(10);
        return "Hello " + room2;
    }


}
