package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.pas.dto.RentRoomForSelfDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RentRESTClient;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;


@Named
public class RentForSelfBean {

    @Inject
    private RentRESTClient rentRESTClient;

    @Getter
    private final RentRoomForSelfDTO dto = new RentRoomForSelfDTO();

    @Getter
    @Setter
    private String beginTime;
    @Getter
    @Setter
    private String endTime;


    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("room_id");
        this.dto.setRoomId(Long.valueOf(id));
    }

    public void rentRoom() {
        this.dto.setBeginTime(LocalDateTime.parse(beginTime));
        this.dto.setEndTime(LocalDateTime.parse(endTime));
        try {
            rentRESTClient.rentRoomForSelf(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }





}
