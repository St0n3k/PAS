package pl.lodz.p.it.pas.guesthousemvc.beans.user.client;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Client;

@Named
@ViewScoped
public class ClientListBean implements Serializable {

    private List<Client> clientList = new ArrayList<>();

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
        this.clientList = userRESTClient.getClientList();
    }

    public void deactivateClient(Long id) throws IOException, InterruptedException {
        userRESTClient.deactivateUser(id);
        refreshClientList();
    }

    public void activateClient(Long id) throws IOException, InterruptedException {
        userRESTClient.activateClient(id);
        refreshClientList();
    }

    public void searchByUsername(String username) throws IOException, InterruptedException {
        System.out.println(username);
        this.clientList = userRESTClient.getClientsWithMatchingUsername(username);
    }
}
