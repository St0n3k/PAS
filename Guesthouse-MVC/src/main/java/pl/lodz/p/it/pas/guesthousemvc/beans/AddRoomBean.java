package pl.lodz.p.it.pas.guesthousemvc.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import pl.lodz.p.it.pas.dto.CreateRoomDTO;

@Named
@RequestScoped
public class AddRoomBean {
    private final CreateRoomDTO room = new CreateRoomDTO();
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();


    public CreateRoomDTO getRoom() {
        return room;
    }

    public void addRoom() throws IOException, InterruptedException {
        String requestBody = this.mapper.writeValueAsString(this.room);
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("http://localhost:8080/api/rooms"))
                .POST(BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
    }
}
