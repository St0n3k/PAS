package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RentRESTClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.time.LocalDateTime;

@Named
@RequestScoped
public class AddRentBean {

    @Getter
    private final CreateRentDTO rent = new CreateRentDTO();

    @Getter
    @Setter
    private String beginTime;

    @Getter
    @Setter
    private String endTime;

    @Inject
    private RentRESTClient rentRESTClient;


    public String addRent() throws IOException, InterruptedException {
        this.rent.setBeginTime(LocalDateTime.parse(beginTime));
        this.rent.setEndTime(LocalDateTime.parse(endTime));
        int statusCode = rentRESTClient.addRent(rent);
        if (statusCode == 201) {
            return "showRentList";
        } else {
            //TODO Display error message
        }
        return "";
    }
}

