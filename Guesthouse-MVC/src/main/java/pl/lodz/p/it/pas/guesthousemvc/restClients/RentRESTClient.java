package pl.lodz.p.it.pas.guesthousemvc.restClients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.dto.RentRoomForSelfDTO;
import pl.lodz.p.it.pas.dto.UpdateRentBoardDTO;
import pl.lodz.p.it.pas.guesthousemvc.beans.auth.SessionBean;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;
import pl.lodz.p.it.pas.model.Rent;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RequestScoped
public class RentRESTClient {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Inject
    private SessionBean session;

    public RentRESTClient() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<Rent> getRentList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/rents"))
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + session.getJwt())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Rent>>() {
        });
    }

    public int removeRent(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.
                newBuilder(URI.create(Utils.API_URL + "/rents/" + id))
                .DELETE()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + session.getJwt())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int addRent(CreateRentDTO createRentDTO) throws IOException, InterruptedException {
        String requestBody = this.mapper.writeValueAsString(createRentDTO);
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/rents"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + session.getJwt())
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int rentRoomForSelf(RentRoomForSelfDTO dto, Long roomId) throws IOException, InterruptedException {
        String requestBody = this.mapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/rooms/" + roomId))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + session.getJwt())
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int updateRoom(Long id, UpdateRentBoardDTO dto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/rents/" + id + "/board"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dto)))
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + session.getJwt())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }
}
