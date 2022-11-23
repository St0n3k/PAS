package pl.lodz.p.it.pas.guesthousemvc.beans.room;


import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;
import pl.lodz.p.it.pas.model.Room;

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


    public void removeRoom(Long id) throws IOException, InterruptedException {
        int statusCode = roomRESTClient.removeRoom(id);
        if (statusCode != 204) {
            //TODO display error message
        }
        refreshRoomList();
    }

    public boolean getPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }
}
