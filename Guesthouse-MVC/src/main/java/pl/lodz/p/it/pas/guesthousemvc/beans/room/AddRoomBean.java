package pl.lodz.p.it.pas.guesthousemvc.beans.room;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.CreateRoomDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@Named
@RequestScoped
public class AddRoomBean {

    @Getter
    private final CreateRoomDTO room = new CreateRoomDTO();

    @Inject
    private RoomRESTClient roomRESTClient;

    @Getter
    private int statusCode;


    public String addRoom() throws IOException, InterruptedException {
        statusCode = roomRESTClient.addRoom(room);
        if (statusCode == 201) {
            return "showRoomList"; // redirects to room list
        } else {
            //TODO Display error message
        }
        return ""; // stays at current page
    }
}
