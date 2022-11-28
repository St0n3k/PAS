package pl.lodz.p.it.pas.guesthousemvc.beans.user.client;

import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Client;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Named
@ViewScoped
public class EditClientBean implements Serializable {
    @Inject
    private UserRESTClient userRESTClient;

    private Long clientId;

    private UpdateUserDTO updateUserDTO;
    private UpdateUserDTO oldUserDTO;

    @PostConstruct
    private void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        String id = params.get("client_id");
        this.clientId = Long.valueOf(id);
        try {
            Client client = userRESTClient.getClientById(this.clientId);
            this.updateUserDTO = new UpdateUserDTO(client.getUsername(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getPersonalId(),
                    client.getAddress().getCity(),
                    client.getAddress().getStreet(),
                    client.getAddress().getHouseNumber()
            );
            this.oldUserDTO = new UpdateUserDTO(client.getUsername(),
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
        UpdateUserDTO dto = new UpdateUserDTO(
                updateUserDTO.getUsername(),
                updateUserDTO.getFirstName(),
                updateUserDTO.getLastName(),
                updateUserDTO.getPersonalId(),
                updateUserDTO.getCity(),
                updateUserDTO.getStreet(),
                updateUserDTO.getNumber()
        );

        if (updateUserDTO.getUsername().equals(oldUserDTO.getUsername())) {
            dto.setUsername(null);
        }
        if (updateUserDTO.getFirstName().equals(oldUserDTO.getFirstName())) {
            dto.setFirstName(null);
        }
        if (updateUserDTO.getLastName().equals(oldUserDTO.getLastName())) {
            dto.setLastName(null);
        }
        if (updateUserDTO.getPersonalId().equals(oldUserDTO.getPersonalId())) {
            dto.setPersonalId(null);
        }
        if (updateUserDTO.getCity().equals(oldUserDTO.getCity())) {
            dto.setCity(null);
        }
        if (updateUserDTO.getStreet().equals(oldUserDTO.getStreet())) {
            dto.setStreet(null);
        }
        if (updateUserDTO.getNumber().equals(oldUserDTO.getNumber())) {
            dto.setNumber(null);
        }

        int statusCode = userRESTClient.updateUser(this.clientId, dto);
        if (statusCode == 200) {
            return "showClientList";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("editClientForm:submit",
                    new FacesMessage(bundle.getString("client.edit.error")));
        }
        return "";
    }

}
