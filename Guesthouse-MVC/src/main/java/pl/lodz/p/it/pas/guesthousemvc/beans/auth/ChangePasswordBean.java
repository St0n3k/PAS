package pl.lodz.p.it.pas.guesthousemvc.beans.auth;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.pas.dto.ChangePasswordDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.AuthRESTClient;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

@Named
@ViewScoped
public class ChangePasswordBean implements Serializable {

    @Inject
    private AuthRESTClient authRESTClient;

    @Inject
    private SessionBean session;

    @Getter
    private final ChangePasswordDTO dto = new ChangePasswordDTO();

    @Getter
    @Setter
    private String repeatPassword = "";

    public void changePassword() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        String messageBundleName = facesContext.getApplication().getMessageBundle();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

        if (!Objects.equals(repeatPassword, dto.getNewPassword())) {
            facesContext.addMessage("changePasswordForm:submit",
                    new FacesMessage(bundle.getString("passwords.not.match.error")));
            return;
        }

        try {
            if (authRESTClient.changePassword(dto) == 200) {
                facesContext.addMessage("backForm:showMainMenu",
                        new FacesMessage("Zmieniono hasło"));
            } else {
                facesContext.addMessage("changePasswordForm:submit",
                        new FacesMessage(bundle.getString("wrong.password.error")));
            }
        } catch (IOException | InterruptedException e) {
            facesContext.addMessage("changePasswordForm:submit",
                    new FacesMessage(bundle.getString("password.change.error")));
        }
    }

}
