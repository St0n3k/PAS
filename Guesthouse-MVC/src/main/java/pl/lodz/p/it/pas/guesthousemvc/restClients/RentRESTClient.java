package pl.lodz.p.it.pas.guesthousemvc.restClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;
import pl.lodz.p.it.pas.model.Rent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RentRESTClient {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<Rent> refreshRentList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/rents"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), List.class);
    }

    public void removeRent(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.
                newBuilder(URI.create(Utils.API_URL + "/rents/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void addRent(CreateRentDTO createRentDTO) throws IOException, InterruptedException {
        String requestBody = this.mapper.writeValueAsString(createRentDTO);
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/rents"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
