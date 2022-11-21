package pl.lodz.p.it.pas.guesthousemvc.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
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

    @Inject
    private UserRESTClient userRESTClient;

    private RegisterClientDTO registerClientDTO = new RegisterClientDTO();

    public RegisterClientDTO getRegisterClientDTO() {
        return registerClientDTO;
    }

    public void registerClient() throws IOException, InterruptedException {
        userRESTClient.registerClient(this.registerClientDTO);
    }
}
