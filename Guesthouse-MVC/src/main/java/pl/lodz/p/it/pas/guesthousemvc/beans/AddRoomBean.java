package pl.lodz.p.it.pas.guesthousemvc.beans;

import pl.lodz.p.it.pas.dto.CreateRoomDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@Named
@RequestScoped
public class AddRoomBean {
    private final CreateRoomDTO room = new CreateRoomDTO();

    @Inject
    private RoomRESTClient roomRESTClient;

    public CreateRoomDTO getRoom() {
        return room;
    }

    public void addRoom() throws IOException, InterruptedException {
        roomRESTClient.addRoom(room);
    }
}
