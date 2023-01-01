package pl.lodz.p.it.pas.guesthousemvc.beans.auth;


import lombok.Data;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@SessionScoped
@Named
@Data
public class SessionBean implements Serializable {

    private String jwt = "";

    @Inject
    private HttpServletRequest httpServletRequest;


    public void setLoggedInUser(String jwt) {
        this.jwt = jwt;
        try {
            httpServletRequest.logout();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAuthenticated() {
        return !FacesContext.getCurrentInstance().getExternalContext().isUserInRole("ANONYMOUS");
    }

    public String logout() {
        invalidateSession();
        return "showMainMenu";
    }

    public void invalidateSession() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
}
