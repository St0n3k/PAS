package pl.lodz.p.it.pas.guesthousemvc.beans.auth;


import lombok.Data;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;

@SessionScoped
@Named
@Data
public class SessionBean implements Serializable {

    private String jwt;

    public boolean isAuthenticated() {
        return (jwt != null);
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "showMainMenu";
    }
}
