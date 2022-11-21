package pl.lodz.p.it.pas.guesthousemvc.restClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;
import pl.lodz.p.it.pas.model.user.Client;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RequestScoped
public class UserRESTClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<Client> refreshClientList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/clients")).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), List.class);
    }

    public void activateClient(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/" + id + "/activate"))
                .PUT(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void deactivateClient(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/" + id + "/deactivate"))
                .PUT(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void registerClient(RegisterClientDTO registerClientDTO) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/clients"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(registerClientDTO)))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
