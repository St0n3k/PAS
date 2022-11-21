package pl.lodz.p.it.pas.guesthousemvc.beans.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import pl.lodz.p.it.pas.dto.CreateRoomDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;

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
