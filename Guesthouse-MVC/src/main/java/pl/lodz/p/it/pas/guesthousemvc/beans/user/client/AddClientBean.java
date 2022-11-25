package pl.lodz.p.it.pas.guesthousemvc.beans.user.client;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
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
public class AddClientBean implements Serializable {

    @Inject
    private UserRESTClient userRESTClient;


    private RegisterClientDTO registerClientDTO = new RegisterClientDTO();

    public RegisterClientDTO getRegisterClientDTO() {
        return registerClientDTO;
    }

    public String registerClient() throws IOException, InterruptedException {
        int statusCode = userRESTClient.registerClient(this.registerClientDTO);
        if (statusCode == 201) {
            return "showClientList";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("addClientForm:submit",
                    new FacesMessage(bundle.getString("client.add.error")));

        }
        return "";
    }
}
