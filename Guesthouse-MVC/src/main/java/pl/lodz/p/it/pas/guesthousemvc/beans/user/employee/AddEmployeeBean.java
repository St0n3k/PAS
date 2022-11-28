package pl.lodz.p.it.pas.guesthousemvc.beans.user.client;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
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
public class AddEmployeeBean implements Serializable {

    @Inject
    private UserRESTClient userRESTClient;

    @Getter
    private final RegisterEmployeeDTO registerEmployeeDTO = new RegisterEmployeeDTO();


    public String registerEmployee() throws IOException, InterruptedException {
        int statusCode = userRESTClient.registerEmployee(this.registerEmployeeDTO);
        if (statusCode == 201) {
            return "showEmployeeList";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("addEmployeeForm:submit",
                    new FacesMessage(bundle.getString("employee.add.error")));
        }
        return "";
    }
}
