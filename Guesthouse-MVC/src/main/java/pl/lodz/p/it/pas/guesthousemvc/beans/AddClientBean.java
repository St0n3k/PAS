package pl.lodz.p.it.pas.guesthousemvc.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
@SessionScoped
public class AddClientBean implements Serializable {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private RegisterClientDTO registerClientDTO = new RegisterClientDTO();

    public RegisterClientDTO getRegisterClientDTO() {
        return registerClientDTO;
    }

    public void registerClient() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/clients"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(this.registerClientDTO)))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
