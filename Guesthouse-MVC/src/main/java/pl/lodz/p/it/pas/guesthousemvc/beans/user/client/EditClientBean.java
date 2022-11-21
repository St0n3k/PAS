package pl.lodz.p.it.pas.guesthousemvc.beans.user.client;

import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Client;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@Named
@ViewScoped
public class EditClientBean implements Serializable {
    @Inject
    private UserRESTClient userRESTClient;

    private Long client_id;

    private UpdateUserDTO updateUserDTO;
    private UpdateUserDTO oldUserDTO;

    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("client_id");
        this.client_id = Long.valueOf(id);
        try {
            Client client = userRESTClient.getClientById(this.client_id);
            this.updateUserDTO = new UpdateUserDTO(
                    client.getUsername(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getPersonalId(),
                    client.getAddress().getCity(),
                    client.getAddress().getStreet(),
                    client.getAddress().getHouseNumber()
            );
            this.oldUserDTO = new UpdateUserDTO(
                    client.getUsername(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getPersonalId(),
                    client.getAddress().getCity(),
                    client.getAddress().getStreet(),
                    client.getAddress().getHouseNumber()
            );
        } catch (InterruptedException | IOException ignored) {
        }
    }

    public UpdateUserDTO getUpdateUserDTO() {
        return updateUserDTO;
    }

    public String updateClient() throws IOException, InterruptedException {
        if (updateUserDTO.getUsername().equals(oldUserDTO.getUsername())) {
            updateUserDTO.setUsername(null);
        }
        if (updateUserDTO.getFirstName().equals(oldUserDTO.getFirstName())) {
            updateUserDTO.setFirstName(null);
        }
        if (updateUserDTO.getLastName().equals(oldUserDTO.getLastName())) {
            updateUserDTO.setLastName(null);
        }
        if (updateUserDTO.getPersonalId().equals(oldUserDTO.getPersonalId())) {
            updateUserDTO.setPersonalId(null);
        }
        if (updateUserDTO.getCity().equals(oldUserDTO.getCity())) {
            updateUserDTO.setCity(null);
        }
        if (updateUserDTO.getStreet().equals(oldUserDTO.getStreet())) {
            updateUserDTO.setStreet(null);
        }
        if (updateUserDTO.getNumber().equals(oldUserDTO.getNumber())) {
            updateUserDTO.setNumber(null);
        }

        userRESTClient.updateClient(this.client_id, this.updateUserDTO);
        return "showClientList";
    }

}
