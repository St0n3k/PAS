package pl.lodz.p.it.pas.guesthousemvc.beans.room;


import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;
import pl.lodz.p.it.pas.model.Room;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class RoomListBean implements Serializable {

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

    public List<Room> getRoomList() {
        return roomList;
    }

    public void refreshRoomList() throws IOException, InterruptedException {
        roomList = roomRESTClient.refreshRoomList();
    }


    public void removeRoom(int id) throws IOException, InterruptedException {
        roomRESTClient.removeRoom(id);
        refreshRoomList();
    }
}
