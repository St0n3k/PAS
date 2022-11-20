package pl.lodz.p.it.pas.guesthousemvc.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.model.user.Client;

import javax.annotation.PostConstruct;
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
@SessionScoped
public class ClientListBean implements Serializable {

    private List<Client> clientList = new ArrayList<Client>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();


    @PostConstruct
    private void init() {
        try {
            refreshClientList();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void refreshClientList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/clients")).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        clientList = mapper.readValue(response.body(), List.class);
    }

    public void deactivateClient(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/" + id + "/deactivate"))
                .PUT(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        refreshClientList();
    }

    public void activateClient(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/" + id + "/activate"))
                .PUT(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        refreshClientList();
    }
}
