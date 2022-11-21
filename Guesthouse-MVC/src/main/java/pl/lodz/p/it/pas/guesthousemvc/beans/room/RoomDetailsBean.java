package pl.lodz.p.it.pas.guesthousemvc.beans.room;

import lombok.Getter;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;
import pl.lodz.p.it.pas.model.Rent;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Named
@ViewScoped
public class RoomDetailsBean implements Serializable {

    @Inject
    private RoomRESTClient roomRESTClient;

    @Getter
    List<Rent> rentList = new ArrayList<>();


    @Getter
    private Long roomId;


    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("room_id");
        this.roomId = Long.valueOf(id);
        try {
            rentList = roomRESTClient.getRentsOfRoom(roomId);
        } catch (InterruptedException | IOException ignored) {
        }
    }

    public void refreshRentList() {
        try {
            this.rentList = roomRESTClient.getRentsOfRoom(this.roomId);
        } catch (InterruptedException | IOException ignored) {
        }
    }
}
