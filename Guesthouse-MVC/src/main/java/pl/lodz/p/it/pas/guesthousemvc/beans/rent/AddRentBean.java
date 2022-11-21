package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

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
    private final CreateRentDTO rent = new CreateRentDTO();
    private String beginTime;
    private String endTime;


    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Inject
    private RentRESTClient rentRESTClient;

    public CreateRentDTO getRent() {
        return rent;
    }

    public void addRent() throws IOException, InterruptedException {
        System.out.println(beginTime);
        this.rent.setBeginTime(LocalDateTime.parse(beginTime));
        this.rent.setEndTime(LocalDateTime.parse(endTime));
        rentRESTClient.addRent(rent);
    }
}

