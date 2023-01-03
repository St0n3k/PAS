package pl.lodz.p.it.pas.guesthousemvc.restClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.dto.ChangePasswordDTO;
import pl.lodz.p.it.pas.dto.LoginDTO;
import pl.lodz.p.it.pas.dto.LoginResponse;
import pl.lodz.p.it.pas.guesthousemvc.beans.auth.SessionBean;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequestScoped
public class AuthRESTClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Inject
    private SessionBean session;

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

    public int changePassword(ChangePasswordDTO dto) throws IOException, InterruptedException {
        String requestBody = this.mapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/changePassword"))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + session.getJwt())
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }
}
