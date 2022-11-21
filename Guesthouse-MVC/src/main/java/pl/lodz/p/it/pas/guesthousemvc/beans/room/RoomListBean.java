package pl.lodz.p.it.pas.guesthousemvc.beans.room;


import lombok.Getter;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;
import pl.lodz.p.it.pas.model.Room;

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
public class RoomListBean implements Serializable {

    @Getter
    private List<Room> roomList = new ArrayList<>();

    @Inject
    private RoomRESTClient roomRESTClient;

    @PostConstruct
    private void init() {
        try {
            refreshRoomList();
        } catch (IOException | InterruptedException ignored) {
        }
    }


    public void refreshRoomList() throws IOException, InterruptedException {
        roomList = roomRESTClient.getRoomList();
    }


    public void removeRoom(int id) throws IOException, InterruptedException {
        int statusCode = roomRESTClient.removeRoom(id);
        if (statusCode != 204) {
            //TODO display error message
        }
        refreshRoomList();
    }
}
