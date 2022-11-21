package pl.lodz.p.it.pas.guesthousemvc.beans;

import pl.lodz.p.it.pas.guesthousemvc.restClients.RentRESTClient;
import pl.lodz.p.it.pas.model.Rent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Named
public class RentListBean {
    private List<Rent> rentList = new ArrayList<>();

    @Inject
    private RentRESTClient rentRESTClient;

    @PostConstruct
    private void init() {
        try {
            refreshRentList();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public List<Rent> getRentList() {
        return rentList;
    }

    public void refreshRentList() throws IOException, InterruptedException {
        rentList = rentRESTClient.refreshRentList();
    }


    public void removeRent(int id) throws IOException, InterruptedException {
        rentRESTClient.removeRent(id);
        refreshRentList();
    }
}
