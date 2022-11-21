package pl.lodz.p.it.pas.guesthousemvc.beans.user.client;

import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class AddClientBean implements Serializable {

    @Inject
    private UserRESTClient userRESTClient;

    private RegisterClientDTO registerClientDTO = new RegisterClientDTO();

    public RegisterClientDTO getRegisterClientDTO() {
        return registerClientDTO;
    }

    public String registerClient() throws IOException, InterruptedException {
        userRESTClient.registerClient(this.registerClientDTO);
        return "showClientList";
    }
}
