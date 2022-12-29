package pl.lodz.p.it.pas.guesthousemvc.beans.auth;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.LoginDTO;
import pl.lodz.p.it.pas.dto.RegisterAdminDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.AuthRESTClient;

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
public class LoginBean implements Serializable {

    @Inject
    private AuthRESTClient authRESTClient;

    @Inject
    private SessionBean session;

    @Getter
    private final LoginDTO loginDTO = new LoginDTO();

    public String login() {
        String jwt;
        try {
            jwt = authRESTClient.login(loginDTO);
        } catch (Exception e) {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("addAdminForm:submit",
                    new FacesMessage(bundle.getString("login.error")));
            return "";
        }
        if (jwt != null) {
            session.setJwt(jwt);
            return "showMainMenu";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("addAdminForm:submit",
                    new FacesMessage(bundle.getString("login.error")));
        }
        return "";
    }

}
