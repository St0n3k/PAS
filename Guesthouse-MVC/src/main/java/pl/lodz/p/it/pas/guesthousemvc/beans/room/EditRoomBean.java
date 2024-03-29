package pl.lodz.p.it.pas.guesthousemvc.beans.room;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.UpdateRoomDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;
import pl.lodz.p.it.pas.model.Room;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Named
@ViewScoped
public class EditRoomBean implements Serializable {
    @Inject
    private RoomRESTClient roomRESTClient;

    private Long roomId;

    @Getter
    private UpdateRoomDTO updateRoomDTO;
    private UpdateRoomDTO oldRoomDTO;

    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("room_id");
        this.roomId = Long.valueOf(id);
        try {
            Room room = roomRESTClient.getRoomById(this.roomId);
            this.updateRoomDTO = new UpdateRoomDTO(
                    room.getRoomNumber(),
                    room.getSize(),
                    room.getPrice()
            );
            this.oldRoomDTO = new UpdateRoomDTO(
                    room.getRoomNumber(),
                    room.getSize(),
                    room.getPrice()
            );
        } catch (InterruptedException | IOException ignored) {
        }
    }

    public String updateRoom() throws IOException, InterruptedException {
        UpdateRoomDTO dto = new UpdateRoomDTO(
                updateRoomDTO.getRoomNumber(),
                updateRoomDTO.getSize(),
                updateRoomDTO.getPrice()
        );
        if (updateRoomDTO.getRoomNumber().equals(oldRoomDTO.getRoomNumber())) {
            dto.setRoomNumber(null);
        }
        if (updateRoomDTO.getSize().equals(oldRoomDTO.getSize())) {
            dto.setSize(null);
        }
        if (updateRoomDTO.getPrice().equals(oldRoomDTO.getPrice())) {
            dto.setPrice(null);
        }

        int statusCode = roomRESTClient.updateRoom(this.roomId, dto);
        if (statusCode == 200) {
            return "showRoomList";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("editRoomForm:submit",
                    new FacesMessage(bundle.getString("room.edit.error")));
        }
        return "";
    }
}
