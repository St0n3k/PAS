package pl.lodz.p.it.pas.guesthousemvc.beans;

import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class ClientListBean implements Serializable {

    private List<Client> clientList = new ArrayList<Client>();

    @Inject
    private UserRESTClient userRESTClient;

    @PostConstruct
    private void init() {
        try {
            refreshClientList();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void refreshClientList() throws IOException, InterruptedException {
        this.clientList = userRESTClient.refreshClientList();
    }

    public void deactivateClient(Long id) throws IOException, InterruptedException {
        userRESTClient.deactivateClient(id);
        refreshClientList();
    }

    public void activateClient(Long id) throws IOException, InterruptedException {
        userRESTClient.activateClient(id);
        refreshClientList();
    }
}
