package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

import lombok.Getter;
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

    @Getter
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


    public void refreshRentList() throws IOException, InterruptedException {
        rentList = rentRESTClient.getRentList();
    }


    public void removeRent(int id) throws IOException, InterruptedException {
        int statusCode = rentRESTClient.removeRent(id);
        if (statusCode == 409) {
            //TODO display error message if rent can't be deleted
        }
        refreshRentList();
    }
}
