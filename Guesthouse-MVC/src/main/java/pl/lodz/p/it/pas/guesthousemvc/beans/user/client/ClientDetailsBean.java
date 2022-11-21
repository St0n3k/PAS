package pl.lodz.p.it.pas.guesthousemvc.beans.user.client;

import lombok.Getter;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.Rent;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class ClientDetailsBean implements Serializable {
    @Inject
    private UserRESTClient userRESTClient;

    @Getter
    private List<Rent> rentList;

    @Getter
    private Long clientId;

    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("client_id");
        this.clientId = Long.valueOf(id);
        try {
            this.rentList = userRESTClient.getRentsByClientId(this.clientId);
        } catch (InterruptedException | IOException ignored) {
        }
    }

    public void refreshRentList() {
        try {
            this.rentList = userRESTClient.getRentsByClientId(this.clientId);
        } catch (InterruptedException | IOException ignored) {
        }
    }
}
