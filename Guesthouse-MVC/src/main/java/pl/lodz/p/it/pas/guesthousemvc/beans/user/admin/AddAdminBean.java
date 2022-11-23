package pl.lodz.p.it.pas.guesthousemvc.beans.user.admin;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.pas.dto.RegisterAdminDTO;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

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
