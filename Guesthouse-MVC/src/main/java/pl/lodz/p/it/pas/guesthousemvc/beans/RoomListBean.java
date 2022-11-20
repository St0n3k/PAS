package pl.lodz.p.it.pas.guesthousemvc.beans;


import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;
import pl.lodz.p.it.pas.model.Room;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class RoomListBean implements Serializable {

    private List<Room> roomList = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();


    @PostConstruct
    private void init() {
        try {
            refreshRoomList();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void refreshRoomList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/rooms"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        roomList = mapper.readValue(response.body(), List.class);
    }


    public void removeRoom(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.
                newBuilder(URI.create(Utils.API_URL + "/rooms/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        refreshRoomList();
    }
}
