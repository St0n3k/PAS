package pl.lodz.p.it.pas.guesthousemvc.restClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.dto.LoginDTO;
import pl.lodz.p.it.pas.dto.LoginResponse;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;

import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequestScoped
public class AuthRESTClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String login(LoginDTO loginDTO) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/login"))
                                         .POST(
                                             HttpRequest.BodyPublishers.ofString(
                                                 mapper.writeValueAsString(loginDTO)))
                                         .header("Content-type", "application/json")
                                         .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), LoginResponse.class).getJwt();
    }

    public String changePassword() {
        throw new RuntimeException("Not implemented yet");
    }
}
