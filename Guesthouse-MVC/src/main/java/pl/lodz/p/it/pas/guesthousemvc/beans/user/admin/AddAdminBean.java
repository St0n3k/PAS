package pl.lodz.p.it.pas.guesthousemvc.beans.user.admin;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.RegisterAdminDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

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
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("addAdminForm:submit",
                    new FacesMessage(bundle.getString("admin.add.error")));
        }
        return "";
    }
}
