package pl.lodz.p.it.pas.guesthousemvc.beans.room;


import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("table",
                    new FacesMessage(bundle.getString("room.remove.error")));
        }
        refreshRoomList();
    }

    public boolean getPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }
}
