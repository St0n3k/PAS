package pl.lodz.p.it.pas.guesthousemvc.beans.user.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Admin;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Named
@ViewScoped
public class EditAdminBean implements Serializable {

    @Inject
    private UserRESTClient userRESTClient;

    private Long adminId;

    private final ObjectMapper mapper = new ObjectMapper();

    private String ifMatch = "";

    private UpdateUserDTO updateUserDTO;
    private UpdateUserDTO oldUserDTO;

    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("admin_id");

        this.adminId = Long.valueOf(id);
        try {
            HttpResponse<String> response = userRESTClient.getClientById(this.adminId);
            Admin admin = mapper.readValue(response.body(), Admin.class);

            String ETag = response.headers().firstValue("ETag")
                    .orElseThrow(RuntimeException::new);

            ifMatch = mapper.readValue(ETag, String.class);
            this.updateUserDTO = new UpdateUserDTO(
                    admin.getId(),
                    admin.getUsername(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            this.oldUserDTO = new UpdateUserDTO(
                    admin.getId(),
                    admin.getUsername(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        } catch (InterruptedException | IOException ignored) {
        }
    }

    public UpdateUserDTO getUpdateUserDTO() {
        return updateUserDTO;
    }

    public String updateAdmin() throws IOException, InterruptedException {
        UpdateUserDTO dto = new UpdateUserDTO(
                this.updateUserDTO.getId(),
                this.updateUserDTO.getUsername(),
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (updateUserDTO.getUsername().equals(oldUserDTO.getUsername())) {
            dto.setUsername(null);
        }

        int statusCode = userRESTClient.updateUser(this.adminId, dto, ifMatch);
        if (statusCode == 200) {
            return "showAdminList";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("editAdminForm:submit",
                    new FacesMessage(bundle.getString("admin.edit.error")));
        }
        return "";
    }

}
