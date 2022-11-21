package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RentRESTClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@Named
@RequestScoped
public class AddRentBean {
    private final CreateRentDTO rent = new CreateRentDTO();

    @Inject
    private RentRESTClient rentRESTClient;

    public CreateRentDTO getRent() {
        return rent;
    }

    public void addRent() throws IOException, InterruptedException {
        rentRESTClient.addRent(rent);
    }
}

