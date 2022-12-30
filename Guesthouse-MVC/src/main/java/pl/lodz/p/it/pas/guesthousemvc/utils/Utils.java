package pl.lodz.p.it.pas.guesthousemvc.utils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;


@Named
public class Utils {
    public static final String API_URL = "http://localhost:8080/api";

    public boolean isAnonymous() {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("ANONYMOUS");
    }

    public boolean isClient() {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("CLIENT");
    }

    public boolean isEmployee() {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("EMPLOYEE");
    }

    public boolean isAdmin() {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("ADMIN");
    }

    public String getRole() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if (context.isUserInRole("CLIENT")) return "CLIENT";
        if (context.isUserInRole("EMPLOYEE")) return "EMPLOYEE";
        if (context.isUserInRole("ADMIN")) return "ADMIN";
        return "ANONYMOUS";
    }

}
