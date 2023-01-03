package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.pas.dto.RentRoomForSelfDTO;
import pl.lodz.p.it.pas.guesthousemvc.beans.room.RoomDetailsBean;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RentRESTClient;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;


@Named
@ViewScoped
public class RentForSelfBean implements Serializable {

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

    @Inject
    private RoomDetailsBean roomDetailsBean;



    public void rentRoom() {
        this.dto.setBeginTime(LocalDateTime.parse(beginTime));
        this.dto.setEndTime(LocalDateTime.parse(endTime));
        Long roomId = roomDetailsBean.getRoomId();

        int statusCode;
        try {
            statusCode = rentRESTClient.rentRoomForSelf(dto, roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (statusCode == 201) {
            roomDetailsBean.refreshRentList();
        } else {

        }
    }





}
