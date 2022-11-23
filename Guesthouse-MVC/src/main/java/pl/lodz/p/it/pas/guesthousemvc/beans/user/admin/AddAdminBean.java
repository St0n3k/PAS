package pl.lodz.p.it.pas.guesthousemvc.beans.user.admin;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import lombok.Getter;
import pl.lodz.p.it.pas.dto.RegisterAdminDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;

@Named
@ViewScoped
public class AddAdminBean implements Serializable {

    @Inject
    private UserRESTClient userRESTClient;

    @Getter
    private final RegisterAdminDTO registerAdminDTO = new RegisterAdminDTO();


    public String registerAdmin() throws IOException, InterruptedException {
        int statusCode = userRESTClient.registerAdmin(registerAdminDTO);
        if (statusCode == 201) {
            return "showAdminList";
        } else {
            //TODO Display error message
        }
        return "";
    }
}
