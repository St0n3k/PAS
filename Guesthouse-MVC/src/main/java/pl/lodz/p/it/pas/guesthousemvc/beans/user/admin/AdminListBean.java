package pl.lodz.p.it.pas.guesthousemvc.beans.user.admin;

import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Admin;
import pl.lodz.p.it.pas.model.user.Client;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AdminListBean implements Serializable {

    private List<Admin> adminList = new ArrayList<>();

    @Inject
    private UserRESTClient userRESTClient;

    @PostConstruct
    private void init() {
        try {
            refreshAdminList();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public List<Admin> getAdminList() {
        return adminList;
    }

    public void refreshAdminList() throws IOException, InterruptedException {
        this.adminList = userRESTClient.getAdminList();
    }

    public void deactivateAdmin(Long id) throws IOException, InterruptedException {
        userRESTClient.deactivateUser(id);
        refreshAdminList();
    }

    public void activateAdmin(Long id) throws IOException, InterruptedException {
        userRESTClient.activateClient(id);
        refreshAdminList();
    }
}
